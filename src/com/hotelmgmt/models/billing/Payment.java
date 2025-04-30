package com.hotelmgmt.models.billing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Payment implements Serializable {
    private String id;
    private String invoiceId;
    private BigDecimal amount;
    private PaymentMethod method;
    private String transactionReference;
    private LocalDateTime paymentDate;
    private String notes;

    public Payment(String invoiceId, BigDecimal amount, PaymentMethod method, 
                  String transactionReference, String notes) {
        this.id = UUID.randomUUID().toString();
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.method = method;
        this.transactionReference = transactionReference;
        this.paymentDate = LocalDateTime.now();
        this.notes = notes;
    }

    // Getters
    public String getId() { return id; }
    public String getInvoiceId() { return invoiceId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentMethod getMethod() { return method; }
    public String getTransactionReference() { return transactionReference; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return String.format("Payment{id='%s', invoice='%s', amount=%s, method=%s, date=%s}",
                id, invoiceId, amount, method, paymentDate);
    }
} 