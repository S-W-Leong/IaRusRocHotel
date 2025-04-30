package com.hotelmgmt.models.room;

public enum RoomType {
    STANDARD("Standard room with basic amenities"),
    DELUXE("Deluxe room with premium amenities and city view"),
    SUITE("Luxury suite with separate living area and premium amenities"),
    EXECUTIVE_SUITE("Executive suite with premium amenities and services"),
    PRESIDENTIAL_SUITE("Top-tier suite with all premium amenities and services");

    private final String description;

    RoomType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 