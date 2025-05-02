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
        BigDecimal remainingAmount = invoice.getTotalAmount().subtract(invoice.getPaidAmount());
        
        // Always show receipt
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    PAYMENT RECEIPT                             ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                                ║");
        System.out.printf("║  Amount Paid: RM%-46.2f ║\n", amount);
        System.out.printf("║  Amount Due: RM%-47.2f ║\n", remainingAmount);
        
        if (amount.compareTo(remainingAmount) > 0) {
            // Overpayment case
            BigDecimal change = amount.subtract(remainingAmount);
            System.out.printf("║  Change Given: RM%-45.2f ║\n", change);
            Payment payment = new Payment(invoice.getId(), remainingAmount, method, transactionReference, 
                notes + " (Original payment: RM" + amount + ", Change: RM" + change + ")");
            invoice.addPayment(payment);
        } else {
            // Normal payment case
            System.out.println("║  Change Given: RM0.00                                          ║");
            Payment payment = new Payment(invoice.getId(), amount, method, transactionReference, notes);
            invoice.addPayment(payment);
        }
        
        System.out.println("║                                                                ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        
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