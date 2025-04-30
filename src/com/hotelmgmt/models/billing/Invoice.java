package com.hotelmgmt.models.billing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.hotelmgmt.models.reservation.Reservation;
import com.hotelmgmt.models.roomService.RoomService;

public class Invoice implements Serializable {
    private String id;
    private Reservation reservation;
    private List<RoomService> roomServices;
    private List<AdditionalCharge> additionalCharges;
    private BigDecimal roomCharges;
    private BigDecimal serviceCharges;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private PaymentStatus paymentStatus;
    private List<Payment> payments;
    private LocalDateTime generatedAt;
    private LocalDateTime paidAt;

    public Invoice(Reservation reservation) {
        this.id = UUID.randomUUID().toString();
        this.reservation = reservation;
        this.roomServices = new ArrayList<>();
        this.additionalCharges = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.roomCharges = reservation.getTotalAmount();
        this.serviceCharges = BigDecimal.ZERO;
        this.paidAmount = BigDecimal.ZERO;
        this.paymentStatus = PaymentStatus.PENDING;
        this.generatedAt = LocalDateTime.now();
        calculateTotalAmount();
    }

    public void addRoomService(RoomService roomService) {
        roomServices.add(roomService);
        serviceCharges = serviceCharges.add(roomService.getTotalAmount());
        calculateTotalAmount();
    }

    public void addAdditionalCharge(AdditionalCharge charge) {
        additionalCharges.add(charge);
        calculateTotalAmount();
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
        paidAmount = paidAmount.add(payment.getAmount());
        updatePaymentStatus();
        if (paymentStatus == PaymentStatus.PAID) {
            paidAt = LocalDateTime.now();
        }
    }

    private void calculateTotalAmount() {
        BigDecimal subtotal = roomCharges.add(serviceCharges);
        for (AdditionalCharge charge : additionalCharges) {
            subtotal = subtotal.add(charge.getAmount());
        }
        // Calculate tax (assuming 10% tax rate)
        this.taxAmount = subtotal.multiply(new BigDecimal("0.10"));
        this.totalAmount = subtotal.add(taxAmount);
        updatePaymentStatus();
    }

    private void updatePaymentStatus() {
        if (paidAmount.compareTo(totalAmount) >= 0) {
            paymentStatus = PaymentStatus.PAID;
        } else if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            paymentStatus = PaymentStatus.PARTIALLY_PAID;
        } else {
            paymentStatus = PaymentStatus.PENDING;
        }
    }

    // Getters
    public String getId() { return id; }
    public Reservation getReservation() { return reservation; }
    public List<RoomService> getRoomServices() { return new ArrayList<>(roomServices); }
    public List<AdditionalCharge> getAdditionalCharges() { return new ArrayList<>(additionalCharges); }
    public List<Payment> getPayments() { return new ArrayList<>(payments); }
    public BigDecimal getRoomCharges() { return roomCharges; }
    public BigDecimal getServiceCharges() { return serviceCharges; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public LocalDateTime getPaidAt() { return paidAt; }

    @Override
    public String toString() {
        return String.format("Invoice{id='%s', reservation='%s', total=%s, paid=%s, status=%s}",
                id, reservation.getId(), totalAmount, paidAmount, paymentStatus);
    }
} 