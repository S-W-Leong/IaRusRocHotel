package com.hotelmgmt.models.billing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class AdditionalCharge implements Serializable {
    private String id;
    private String description;
    private BigDecimal amount;
    private ChargeType type;
    private LocalDateTime chargeDate;
    private String notes;

    public AdditionalCharge(String description, BigDecimal amount, ChargeType type, String notes) {
        this.id = UUID.randomUUID().toString();
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.chargeDate = LocalDateTime.now();
        this.notes = notes;
    }

    // Getters
    public String getId() { return id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public ChargeType getType() { return type; }
    public LocalDateTime getChargeDate() { return chargeDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return String.format("AdditionalCharge{id='%s', description='%s', amount=%s, type=%s}",
                id, description, amount, type);
    }
} 