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
            reservation.setStatus(ReservationStatus.CHECKED_OUT);
            roomService.updateRoomStatus(reservation.getRoom(), RoomStatus.AVAILABLE);
            // Billing integration
            Invoice invoice = paymentService.createInvoice(reservation);
            // Add all room service orders for this room and guest to the invoice
            List<RoomServiceOrder> orders = roomServiceManager.getOrdersForRoom(reservation.getRoom());
            for (RoomServiceOrder order : orders) {
                if (order.getGuest().equals(reservation.getGuest())) {
                    invoice.addRoomService(order);
                }
            }
            // Process payment (assume full payment at check-out)
            BigDecimal total = invoice.getTotalAmount();
            paymentService.processPayment(invoice, total, paymentMethod, transactionReference, notes);
            return true;
        }
        return false;
    }
} 