package com.hotelmgmt.models.roomService;

import java.io.Serializable;
import java.math.BigDecimal;

public class MenuItem implements Serializable {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private MenuCategory category;
    private boolean available;

    public MenuItem(String id, String name, String description, BigDecimal price, MenuCategory category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public MenuCategory getCategory() { return category; }
    public void setCategory(MenuCategory category) { this.category = category; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("%s - %s ($%s)", name, description, price);
    }
} 