package com.hotelmgmt.models.roomService;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
        return String.format("%s - %s (RM %.2f)", name, description, price);
    }

    // Static method to initialize all menu items
    public static List<MenuItem> initializeMenuItems() {
        List<MenuItem> menuItems = new ArrayList<>();

        // Breakfast Items
        menuItems.add(new MenuItem("1", "American Breakfast", "Scrambled eggs, bacon, toast, hash browns", new BigDecimal("28.00"), MenuCategory.BREAKFAST));
        menuItems.add(new MenuItem("2", "French Toast", "Brioche bread with maple syrup", new BigDecimal("22.00"), MenuCategory.BREAKFAST));
        menuItems.add(new MenuItem("3", "Avocado Toast", "Sourdough with smashed avocado and poached egg", new BigDecimal("24.00"), MenuCategory.BREAKFAST));
        menuItems.add(new MenuItem("4", "Breakfast Bowl", "Quinoa, kale, sweet potato, poached egg", new BigDecimal("26.00"), MenuCategory.BREAKFAST));
        menuItems.add(new MenuItem("5", "Smoked Salmon Bagel", "Cream cheese, capers, red onion", new BigDecimal("30.00"), MenuCategory.BREAKFAST));
        menuItems.add(new MenuItem("6", "Acai Bowl", "Acai berries, granola, fresh fruits", new BigDecimal("25.00"), MenuCategory.BREAKFAST));

        // Lunch Items
        menuItems.add(new MenuItem("7", "Grilled Chicken Caesar", "Romaine, parmesan, croutons, dressing", new BigDecimal("32.00"), MenuCategory.LUNCH));
        menuItems.add(new MenuItem("8", "Beef Burger", "Angus beef, cheddar, lettuce, tomato", new BigDecimal("35.00"), MenuCategory.LUNCH));
        menuItems.add(new MenuItem("9", "Poke Bowl", "Tuna, rice, avocado, seaweed", new BigDecimal("38.00"), MenuCategory.LUNCH));
        menuItems.add(new MenuItem("10", "Margherita Pizza", "Tomato sauce, mozzarella, basil", new BigDecimal("32.00"), MenuCategory.LUNCH));
        menuItems.add(new MenuItem("11", "Chicken Quesadilla", "Grilled chicken, cheese, salsa", new BigDecimal("30.00"), MenuCategory.LUNCH));
        menuItems.add(new MenuItem("12", "Falafel Wrap", "Chickpea patties, tahini, vegetables", new BigDecimal("28.00"), MenuCategory.LUNCH));

        // Dinner Items
        menuItems.add(new MenuItem("13", "Filet Mignon", "8oz beef tenderloin, truffle sauce", new BigDecimal("88.00"), MenuCategory.DINNER));
        menuItems.add(new MenuItem("14", "Seafood Risotto", "Scallops, shrimp, saffron rice", new BigDecimal("68.00"), MenuCategory.DINNER));
        menuItems.add(new MenuItem("15", "Duck Confit", "Slow-cooked duck leg, orange sauce", new BigDecimal("78.00"), MenuCategory.DINNER));
        menuItems.add(new MenuItem("16", "Vegetable Curry", "Seasonal vegetables, coconut milk", new BigDecimal("42.00"), MenuCategory.DINNER));
        menuItems.add(new MenuItem("17", "Lobster Pasta", "Fresh lobster, linguine, cream sauce", new BigDecimal("92.00"), MenuCategory.DINNER));
        menuItems.add(new MenuItem("18", "Beef Wellington", "Beef tenderloin, mushroom duxelles", new BigDecimal("98.00"), MenuCategory.DINNER));

        // Snacks
        menuItems.add(new MenuItem("19", "Truffle Fries", "Hand-cut fries, truffle oil, parmesan", new BigDecimal("25.00"), MenuCategory.SNACKS));
        menuItems.add(new MenuItem("20", "Bruschetta", "Tomato, basil, garlic on toasted bread", new BigDecimal("18.00"), MenuCategory.SNACKS));
        menuItems.add(new MenuItem("21", "Calamari", "Crispy squid, marinara sauce", new BigDecimal("28.00"), MenuCategory.SNACKS));
        menuItems.add(new MenuItem("22", "Charcuterie Board", "Assorted meats, cheeses, fruits", new BigDecimal("45.00"), MenuCategory.SNACKS));
        menuItems.add(new MenuItem("23", "Edamame", "Steamed soybeans, sea salt", new BigDecimal("15.00"), MenuCategory.SNACKS));
        menuItems.add(new MenuItem("24", "Spring Rolls", "Vegetable rolls, sweet chili sauce", new BigDecimal("20.00"), MenuCategory.SNACKS));

        // Beverages
        menuItems.add(new MenuItem("25", "Espresso", "Single shot of espresso", new BigDecimal("12.00"), MenuCategory.BEVERAGES));
        menuItems.add(new MenuItem("26", "Matcha Latte", "Green tea powder, steamed milk", new BigDecimal("15.00"), MenuCategory.BEVERAGES));
        menuItems.add(new MenuItem("27", "Craft Beer", "Local brewery selection", new BigDecimal("28.00"), MenuCategory.BEVERAGES));
        menuItems.add(new MenuItem("28", "Fresh Coconut", "Whole young coconut", new BigDecimal("15.00"), MenuCategory.BEVERAGES));
        menuItems.add(new MenuItem("29", "Mojito", "White rum, mint, lime, soda", new BigDecimal("32.00"), MenuCategory.BEVERAGES));
        menuItems.add(new MenuItem("30", "Wine Selection", "House red or white wine", new BigDecimal("35.00"), MenuCategory.BEVERAGES));

        // Desserts
        menuItems.add(new MenuItem("31", "Chocolate Fondant", "Warm chocolate cake, vanilla ice cream", new BigDecimal("28.00"), MenuCategory.DESSERTS));
        menuItems.add(new MenuItem("32", "Crème Brûlée", "Vanilla custard, caramelized sugar", new BigDecimal("25.00"), MenuCategory.DESSERTS));
        menuItems.add(new MenuItem("33", "Cheesecake", "New York style, berry compote", new BigDecimal("22.00"), MenuCategory.DESSERTS));
        menuItems.add(new MenuItem("34", "Panna Cotta", "Italian cream dessert, berry sauce", new BigDecimal("24.00"), MenuCategory.DESSERTS));
        menuItems.add(new MenuItem("35", "Affogato", "Vanilla gelato, espresso shot", new BigDecimal("18.00"), MenuCategory.DESSERTS));
        menuItems.add(new MenuItem("36", "Fruit Platter", "Seasonal fruits, chocolate dip", new BigDecimal("32.00"), MenuCategory.DESSERTS));

        return menuItems;
    }

    // Static method to get menu items by category
    public static List<MenuItem> getMenuItemsByCategory(List<MenuItem> allItems, MenuCategory category) {
        List<MenuItem> categoryItems = new ArrayList<>();
        for (MenuItem item : allItems) {
            if (item.getCategory() == category) {
                categoryItems.add(item);
            }
        }
        return categoryItems;
    }
} 