package com.hotelmgmt.services;

import com.hotelmgmt.models.reservation.Reservation;
import com.hotelmgmt.models.reservation.ReservationStatus;
import com.hotelmgmt.models.user.Guest;
import java.util.ArrayList;
import java.util.List;

// ReservationService is a class that manages the reservations in the hotel. It allows the user to create reservations, cancel reservations, and get reservations for a guest.
public class ReservationService {
    private List<Reservation> reservations;

    public ReservationService() {
        this.reservations = new ArrayList<>();
    }

    public Reservation createReservation(Reservation reservation) {
        reservations.add(reservation);
        return reservation;
    }

    public boolean cancelReservation(Reservation reservation) {
        if (reservations.contains(reservation)) {
            reservation.setStatus(ReservationStatus.CANCELLED);
            return true;
        }
        return false;
    }

    public List<Reservation> getReservationsForGuest(Guest guest) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation.getGuest().equals(guest)) {
                result.add(reservation);
            }
        }
        return result;
    }

    public List<Reservation> getAllReservations() {
        return reservations;
    }
} 