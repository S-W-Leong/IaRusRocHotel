package com.hotelmgmt.services;

import com.hotelmgmt.models.billing.Invoice;
import com.hotelmgmt.models.billing.Payment;
import com.hotelmgmt.models.billing.PaymentMethod;
import com.hotelmgmt.models.billing.PaymentStatus;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PaymentService {
    private List<Invoice> invoices;

    public PaymentService() {
        this.invoices = new ArrayList<>();
    }

    public Invoice createInvoice(com.hotelmgmt.models.reservation.Reservation reservation) {
        // Check if invoice already exists for this reservation
        for (Invoice existingInvoice : invoices) {
            if (existingInvoice.getReservation().getId().equals(reservation.getId())) {
                return existingInvoice;  // Return existing invoice instead of creating a new one
            }
        }
        // Create new invoice only if one doesn't exist
        Invoice invoice = new Invoice(reservation);
        invoices.add(invoice);
        return invoice;
    }

    public boolean processPayment(Invoice invoice, BigDecimal amount, PaymentMethod method, String transactionReference, String notes) {
        Payment payment = new Payment(invoice.getId(), amount, method, transactionReference, notes);
        invoice.addPayment(payment);
        return invoice.getPaymentStatus() == PaymentStatus.PAID;
    }

    public Invoice getInvoiceById(String reservationId) {
        for (Invoice invoice : invoices) {
            if (invoice.getReservation().getId().equals(reservationId)) {
                return invoice;
            }
        }
        return null;
    }

    public List<Invoice> getAllInvoices() {
        return new ArrayList<>(invoices);
    }
} 