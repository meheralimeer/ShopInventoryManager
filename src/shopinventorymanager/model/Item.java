package shopinventorymanager.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents an item in the shop inventory system.
 * This class uses the Builder pattern for object creation.
 * 
 * @author meheralimeer
 */
public class Item {
    /** Unique identifier for the item */
    private final int id;
    /** Name of the item */
    private String name;
    /** Timestamp when the item was created */
    private LocalDateTime createdAt;
    /** Timestamp when the item was last updated */
    private LocalDateTime updatedAt;
    /** Expiration date of the itsem */
    private LocalDate expiryDate;

    /**
     * Constructs a new Item with the specified parameters.
     * 
     * @param id         The unique identifier
     * @param name       The name of the item
     * @param createdAt  Creation timestamp
     * @param updatedAt  Last update timestamp
     * @param expiryDate Expiration date
     */
    Item(int id, String name, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDate expiryDate) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.expiryDate = expiryDate;
    }

    /**
     * @return The item's unique identifier
     */
    public int getId() {
        return id;
    }

    /**
     * @return The item's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the item's name
     * 
     * @param name New name for the item
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The timestamp when the item was created
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp
     * 
     * @param createdAt Creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return The timestamp when the item was last updated
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last update timestamp
     * 
     * @param updatedAt Update timestamp
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return The expiration date of the item
     */
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the expiration date
     * 
     * @param expiryDate New expiration date
     */
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Builder class for creating Item instances.
     * Implements the Builder pattern for flexible object creation.
     */
    public static class Builder {
        private int id;
        private String name;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDate expiryDate;

        /**
         * Sets the item's ID
         * 
         * @param id The unique identifier
         * @return The builder instance
         */
        public Builder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the item's name
         * 
         * @param name The item name
         * @return The builder instance
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the creation timestamp
         * 
         * @param createdAt Creation timestamp
         * @return The builder instance
         */
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        /**
         * Sets the update timestamp
         * 
         * @param updatedAt Update timestamp
         * @return The builder instance
         */
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        /**
         * Sets the expiration date
         * 
         * @param expiryDate Expiration date
         * @return The builder instance
         */
        public Builder expiryDate(LocalDate expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        /**
         * Builds and returns a new Item instance
         * 
         * @return A new Item with the configured values
         */
        public Item build() {
            return new Item(id, name, createdAt, updatedAt, expiryDate);
        }
    }
}