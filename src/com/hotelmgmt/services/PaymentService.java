package com.hotelmgmt.services;

import com.hotelmgmt.models.billing.Invoice;
import com.hotelmgmt.models.billing.Payment;
import com.hotelmgmt.models.billing.PaymentMethod;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PaymentService {
    private List<Invoice> invoices;

    public PaymentService() {
        this.invoices = new ArrayList<>();
    }

    public Invoice createInvoice(com.hotelmgmt.models.reservation.Reservation reservation) {
        Invoice invoice = new Invoice(reservation);
        invoices.add(invoice);
        return invoice;
    }

    public boolean processPayment(Invoice invoice, BigDecimal amount, PaymentMethod method, String transactionReference, String notes) {
        Payment payment = new Payment(invoice.getId(), amount, method, transactionReference, notes);
        invoice.addPayment(payment);
        return invoice.getPaymentStatus().toString().equals("PAID");
    }

    public Invoice getInvoiceById(String id) {
        for (Invoice invoice : invoices) {
            if (invoice.getId().equals(id)) {
                return invoice;
            }
        }
        return null;
    }

    public List<Invoice> getAllInvoices() {
        return new ArrayList<>(invoices);
    }
} 