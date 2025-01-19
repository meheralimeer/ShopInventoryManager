package shopinventorymanager.utils;

import shopinventorymanager.model.Item;
import java.io.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all file operations for the Shop Inventory Manager.
 * This class manages reading and writing item data to persistent storage.
 * 
 * @author meheralimeer
 */

public class FileHandler {
    /** The path where item data is stored */
    private static final String FILE_PATH = "data/items.txt";

    /**
     * Generates the next available ID for a new item.
     * 
     * @return The next sequential ID number
     * @throws IOException If there's an error reading the file
     */
    public static int getNextId() throws IOException {
        List<Item> items = loadItems();
        if (items.isEmpty()) {
            return 1;
        }
        return items.stream()
                .mapToInt(Item::getId)
                .max()
                .getAsInt() + 1;
    }

    /**
     * Saves a new item to the file.
     * 
     * @param item The item to be saved
     * @throws IOException If there's an error writing to the file
     */
    public static void saveItem(Item item) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(String.format("%d,%s,%s,%s,%s%n",
                    item.getId(),
                    item.getName(),
                    item.getCreatedAt(),
                    item.getUpdatedAt(),
                    item.getExpiryDate()));
        }
    }

    /**
     * Loads all items from the file.
     * 
     * @return List of all items stored in the file
     * @throws IOException If there's an error reading the file
     */
    public static List<Item> loadItems() throws IOException {
        List<Item> items = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return items;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Item item = new Item.Builder()
                        .id(Integer.parseInt(parts[0]))
                        .name(parts[1])
                        .createdAt(LocalDateTime.parse(parts[2]))
                        .updatedAt(LocalDateTime.parse(parts[3]))
                        .expiryDate(LocalDate.parse(parts[4]))
                        .build();
                items.add(item);
            }
        }
        return items;
    }

    /**
     * Updates an existing item in the file.
     * 
     * @param updatedItem The item with updated information
     * @throws IOException If there's an error writing to the file
     */
    public static void updateItem(Item updatedItem) throws IOException {
        List<Item> items = loadItems();
        List<Item> updatedItems = new ArrayList<>();

        for (Item item : items) {
            if (item.getId() == updatedItem.getId()) {
                updatedItems.add(updatedItem);
            } else {
                updatedItems.add(item);
            }
        }

        saveAllItems(updatedItems);
    }

    /**
     * Deletes an item from the file.
     * 
     * @param id The ID of the item to delete
     * @throws IOException If there's an error writing to the file
     */
    public static void deleteItem(int id) throws IOException {
        List<Item> items = loadItems();
        items.removeIf(item -> item.getId() == id);
        saveAllItems(items);
    }

    /**
     * Saves all items to the file, overwriting existing content.
     * 
     * @param items List of items to save
     * @throws IOException If there's an error writing to the file
     */
    private static void saveAllItems(List<Item> items) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Item item : items) {
                writer.write(String.format("%d,%s,%s,%s,%s%n",
                        item.getId(),
                        item.getName(),
                        item.getCreatedAt(),
                        item.getUpdatedAt(),
                        item.getExpiryDate()));
            }
        }
    }
}