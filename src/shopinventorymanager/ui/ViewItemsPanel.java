package shopinventorymanager.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import shopinventorymanager.model.Item;
import shopinventorymanager.utils.FileHandler;

/**
 * A panel for viewing and managing items in the shop inventory system.
 * 
 * This panel provides a graphical user interface for displaying, editing, and
 * deleting items in the shop inventory. It uses a table to display the items
 * and provides buttons for editing and deleting selected items.
 * 
 * @author meheralimeer
 */
public class ViewItemsPanel extends JPanel {
    /**
     * The table for displaying items.
     * 
     * This table is used to display the items in the shop inventory. Each row
     * in the table represents a single item, and the columns represent the
     * item's ID, name, creation date, update date, and expiration date.
     */
    private JTable itemTable;

    /**
     * The table model for the item table.
     * 
     * This table model is used to manage the data displayed in the item table.
     * It provides methods for adding, removing, and updating items in the table.
     */
    private DefaultTableModel tableModel;

    /**
     * The search field for filtering items.
     * 
     * This field is used to search for specific items in the item table.
     */
    private JTextField searchField;

    /**
     * The sort combo box for sorting items.
     * 
     * This combo box is used to sort the items in the item table based on the
     * selected column.
     */
    private JComboBox<String> sortComboBox;

    /**
     * The row sorter for sorting items.
     * 
     * This row sorter is used to sort the items in the item table based on the
     * selected column.
     */
    private TableRowSorter<DefaultTableModel> sorter;

    /**
     * The scheduled executor service for scheduled tasks.
     * 
     * This executor service is used to schedule tasks to be executed at
     * specific times or intervals.
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Constructs a new ViewItemsPanel instance.
     * 
     * This constructor initializes the panel's components, including the item
     * table and table model.
     */
    public ViewItemsPanel() {
        
        setLayout(new BorderLayout());
        initComponents();
        loadItems();
        startAutoRefresh();
    }

    /**
     * Starts the auto-refresh of the item table.
     * This method schedules a task to be executed every hour to refresh the
     * item table by loading the items from the file.
     */
    private void startAutoRefresh() {
        scheduler.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(this::loadItems);
        }, 1, 1, TimeUnit.HOURS);
    }

    /**
     * Initializes and sets up the GUI components.
     * Creates and adds the item table and button panel to the frame.
     * Also sets up the document listener for the search field and
     * the action listener for the sort combo box.
     */
    @SuppressWarnings("unused")
    private void initComponents() {
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }
        });
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);

        // Sort combo box
        sortComboBox = new JComboBox<>(new String[] { "Name", "Expiry Date" });
        sortComboBox.addActionListener(e -> sort());
        searchPanel.add(new JLabel("Sort by:"));
        searchPanel.add(sortComboBox);

        add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = { "ID", "Name", "Created At", "Updated At", "Expiry Date" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        itemTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        itemTable.setRowSorter(sorter);

        // Add custom renderer for row colors
        itemTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {

            /**
             * Returns a custom table cell renderer component for the given table cell.
             * 
             * This method is overridden to provide a custom renderer that colors the rows
             * based on the expiration date of the item. Rows with expired or expiring
             * items are colored red or yellow, respectively, while rows with non-expiring
             * items are left with the default background color.
             * 
             * @param table      The table that is asking the renderer to draw the cell.
             * @param value      The value of the cell to be rendered.
             * @param isSelected True if the cell is selected, else false.
             * @param hasFocus   True if the cell has focus, else false.
             * @param row        The row index of the cell.
             * @param column     The column index of the cell.
             * @return The custom table cell renderer component.
             */
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);

                int modelRow = table.convertRowIndexToModel(row);
                LocalDate expiryDate = (LocalDate) table.getModel().getValueAt(modelRow, 4);
                LocalDate today = LocalDate.now();

                if (!isSelected) {
                    if (expiryDate.isBefore(today) || expiryDate.isEqual(today)) {
                        c.setBackground(new java.awt.Color(255, 102, 102)); // Red for expired
                    } else if (expiryDate.equals(today.plusDays(1))) {
                        c.setBackground(new java.awt.Color(255, 255, 153)); // Yellow for expiring tomorrow
                    } else {
                        c.setBackground(table.getBackground());
                    }
                }

                return c;
            }
        });
        JScrollPane scrollPane = new JScrollPane(itemTable);
        add(scrollPane, BorderLayout.CENTER);

        // Updated button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton addButton = new JButton("Add New Item");
        addButton.setIcon(getResizedIcon("res/icons/new.png"));
        addButton.addActionListener(e -> showAddItemDialog());

        JButton editButton = new JButton("Edit");
        editButton.setIcon(getResizedIcon("res/icons/edit.png"));
        editButton.addActionListener(e -> editSelectedItem());

        JButton deleteButton = new JButton("Delete");
        deleteButton.setIcon(getResizedIcon("res/icons/delete.png"));
        deleteButton.addActionListener(e -> deleteSelectedItems());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setIcon(getResizedIcon("res/icons/refresh.png"));
        refreshButton.addActionListener(e -> loadItems());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Shows a dialog to add a new item to the shop inventory.
     * The dialog contains text fields for the item's name and expiry date, and
     * buttons to cancel or confirm the addition.
     * If the user confirms the addition, a new item is created with the given
     * name and expiry date, and added to the shop inventory.
     */
    private void showAddItemDialog() {
        JPanel addPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField nameField = new JTextField();
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);

        addPanel.add(new JLabel("Name:"));
        addPanel.add(nameField);
        addPanel.add(new JLabel("Expiry Date:"));
        addPanel.add(dateSpinner);

        int result = JOptionPane.showConfirmDialog(this, addPanel,
                "Add New Item", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                LocalDateTime now = LocalDateTime.now();
                LocalDate expiryDate = LocalDate
                        .parse(((JSpinner.DateEditor) dateSpinner.getEditor()).getTextField().getText());

                Item newItem = new Item.Builder()
                        .id(FileHandler.getNextId())
                        .name(name)
                        .createdAt(now)
                        .updatedAt(now)
                        .expiryDate(expiryDate)
                        .build();

                FileHandler.saveItem(newItem);
                loadItems();
                JOptionPane.showMessageDialog(this, "Item added successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding item: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Loads all items from the file and updates the table model.
     * Clears the table model and adds a new row for each item loaded.
     * If an error occurs while loading the items, shows an error dialog.
     */
    private void loadItems() {
        tableModel.setRowCount(0);
        try {
            List<Item> items = FileHandler.loadItems();
            for (Item item : items) {
                tableModel.addRow(new Object[] {
                        item.getId(),
                        item.getName(),
                        item.getCreatedAt(),
                        item.getUpdatedAt(),
                        item.getExpiryDate()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading items: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Filters the table rows based on the search text entered by the user.
     * If the search text is empty, clears the filter and shows all items.
     * Otherwise, applies a regex filter to the table model to show only
     * rows that match the search text (case-insensitive).
     */
    private void filter() {
        String text = searchField.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

        /**
         * Sorts the table rows based on the selected column.
         * 1 for Name, 4 for Expiry Date.
         * The table model is sorted in ascending order based on the selected column.
         */
    private void sort() {
        int column = sortComboBox.getSelectedIndex() == 0 ? 1 : 4; // 1 for Name, 4 for Expiry Date
        sorter.setSortKeys(List.of(new RowSorter.SortKey(column, SortOrder.ASCENDING)));
    }

    /**
     * Edits the selected item in the table.
     * Shows a dialog with name and expiry date fields for editing.
     * If the user clicks OK, updates the item in the file and refreshes the table.
     * If the user clicks Cancel, does nothing.
     */
    private void editSelectedItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        selectedRow = itemTable.convertRowIndexToModel(selectedRow);

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        // ? LocalDate currentExpiryDate = (LocalDate)
        // tableModel.getValueAt(selectedRow, 4);

        // Create edit dialog
        JPanel editPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField nameField = new JTextField(currentName);
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);

        editPanel.add(new JLabel("Name:"));
        editPanel.add(nameField);
        editPanel.add(new JLabel("Expiry Date:"));
        editPanel.add(dateSpinner);

        int result = JOptionPane.showConfirmDialog(this, editPanel,
                "Edit Item", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String newName = nameField.getText().trim();
                LocalDateTime now = LocalDateTime.now();
                LocalDate newExpiryDate = LocalDate
                        .parse(((JSpinner.DateEditor) dateSpinner.getEditor()).getTextField().getText());

                Item updatedItem = new Item.Builder()
                        .id(id)
                        .name(newName)
                        .createdAt((LocalDateTime) tableModel.getValueAt(selectedRow, 2))
                        .updatedAt(now)
                        .expiryDate(newExpiryDate)
                        .build();

                FileHandler.updateItem(updatedItem);
                loadItems();
                JOptionPane.showMessageDialog(this, "Item updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating item: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Deletes the selected items from the table and file.
     * Shows a confirmation dialog with the number of items to be deleted.
     * If the user clicks Yes, deletes the items from the file and refreshes the table.
     * If the user clicks No, does nothing.
     */
    private void deleteSelectedItems() {
        int[] selectedRows = itemTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select items to delete", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + selectedRows.length + " item(s)?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    int modelRow = itemTable.convertRowIndexToModel(selectedRows[i]);
                    int id = (int) tableModel.getValueAt(modelRow, 0);
                    FileHandler.deleteItem(id);
                }
                loadItems();
                JOptionPane.showMessageDialog(this, "Items deleted successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting items: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Returns an ImageIcon with the given filename resized to 15x15.
     * Useful for setting icons in buttons and other UI components.
     * @param filename The path to the image file.
     * @return A resized ImageIcon.
     */
    public static ImageIcon getResizedIcon(String filename) {
        ImageIcon icon = new ImageIcon(filename);
        Image img = icon.getImage() ;  
        Image newimg = img.getScaledInstance( 15, 15,  java.awt.Image.SCALE_SMOOTH ) ;  
        return new ImageIcon( newimg );
    }
}
