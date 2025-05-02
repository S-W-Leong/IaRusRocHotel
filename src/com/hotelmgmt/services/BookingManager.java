package com.hotelmgmt.services;

import com.hotelmgmt.models.room.Room;
import com.hotelmgmt.models.user.Guest;
import com.hotelmgmt.models.reservation.Reservation;
import com.hotelmgmt.models.reservation.ReservationStatus;
import com.hotelmgmt.models.room.RoomStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.hotelmgmt.services.PaymentService;
import com.hotelmgmt.services.RoomServiceManager;
import com.hotelmgmt.models.billing.Invoice;
import com.hotelmgmt.models.billing.PaymentMethod;
import com.hotelmgmt.models.roomService.RoomServiceOrder;
import java.util.List;
import java.math.BigDecimal;
import com.hotelmgmt.models.billing.PaymentStatus;

//
public class BookingManager {
    private RoomService roomService;
    private ReservationService reservationService;
    private PaymentService paymentService;
    private RoomServiceManager roomServiceManager;

    public BookingManager(RoomService roomService, ReservationService reservationService, PaymentService paymentService, RoomServiceManager roomServiceManager) {
        this.roomService = roomService;
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.roomServiceManager = roomServiceManager;
    }

    public Reservation bookRoom(Guest guest, String roomType, LocalDate checkIn, LocalDate checkOut) {
        LocalDateTime checkInDateTime = checkIn.atStartOfDay();
        LocalDateTime checkOutDateTime = checkOut.atStartOfDay();
        Room availableRoom = roomService.findAvailableRoom(
            roomType, checkInDateTime, checkOutDateTime, reservationService.getAllReservations()
        );
        if (availableRoom == null) {
            return null; // No available room
        }
        // Assign the room
        roomService.assignRoom(availableRoom);
        // Create reservation with LocalDateTime and CONFIRMED status
        Reservation reservation = new Reservation(guest, availableRoom, checkInDateTime, checkOutDateTime, null);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationService.createReservation(reservation);
        return reservation;
    }

    public boolean cancelReservation(Reservation reservation) {
        // Only allow cancellation if not checked in or checked out
        if (reservation.getStatus() == ReservationStatus.CHECKED_IN || reservation.getStatus() == ReservationStatus.CHECKED_OUT) {
            return false;
        }
        boolean cancelled = reservationService.cancelReservation(reservation);
        if (cancelled) {
            roomService.updateRoomStatus(reservation.getRoom(), RoomStatus.AVAILABLE);
        }
        return cancelled;
    }

    public boolean checkIn(Reservation reservation) {
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            reservation.setStatus(ReservationStatus.CHECKED_IN);
            roomService.updateRoomStatus(reservation.getRoom(), RoomStatus.OCCUPIED);
            return true;
        }
        return false;
    }

    public boolean checkOut(Reservation reservation, PaymentMethod paymentMethod, String transactionReference, String notes) {
        if (reservation.getStatus() == ReservationStatus.CHECKED_IN) {
            // Get the existing invoice
            Invoice invoice = paymentService.getInvoiceById(reservation.getId());
            
            
            if (invoice != null) {
                System.out.println("Invoice ID: " + invoice.getId());
                System.out.println("Reservation ID: " + reservation.getId());
                System.out.println("Payment Status: " + invoice.getPaymentStatus());
                System.out.println("Total Amount: " + invoice.getTotalAmount());
                System.out.println("Paid Amount: " + invoice.getPaidAmount());
            }

            if (invoice == null || invoice.getPaymentStatus() != PaymentStatus.PAID) {
                return false;
            }

            // If invoice is paid, proceed with check-out
            reservation.setStatus(ReservationStatus.CHECKED_OUT);
            roomService.updateRoomStatus(reservation.getRoom(), RoomStatus.AVAILABLE);
            return true;
        }
        return false;
    }
} 