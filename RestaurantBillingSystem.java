// File: RestaurantBillingSystem.java
import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

class MenuItem {
    private int id;
    private String name;
    private double price;

    public MenuItem(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    @Override
    public String toString() {
        return String.format("%2d. %-20s Rs %.2f", id, name, price);
    }
}

class OrderItem {
    private MenuItem item;
    private int qty;
    public OrderItem(MenuItem item, int qty) {
        this.item = item; this.qty = qty;
    }
    public MenuItem getMenuItem() { return item; }
    public int getQty() { return qty; }
    public double getTotal() { return item.getPrice() * qty; }
    @Override
    public String toString() {
        return String.format("%-20s x%2d  Rs %.2f", item.getName(), qty, getTotal());
    }
}

public class RestaurantBillingSystem {
    private static final Scanner sc = new Scanner(System.in);
    private static final List<MenuItem> menu = new ArrayList<>();
    private static final DecimalFormat df = new DecimalFormat("0.00");
    // GST percent (change if needed)
    private static final double GST_PERCENT = 5.0;

    public static void main(String[] args) {
        seedMenu();
        System.out.println("=== Welcome to Simple Restaurant Billing System ===");
        boolean running = true;
        while (running) {
            showMainMenu();
            int choice = readInt("Choose option: ");
            switch (choice) {
                case 1 -> showMenu();
                case 2 -> placeOrder();
                case 3 -> adminAddItem();
                case 4 -> adminRemoveItem();
                case 5 -> {
                    System.out.println("Thank you! Exiting...");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void showMainMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. View Menu");
        System.out.println("2. Place Order");
        System.out.println("3. Admin - Add Menu Item");
        System.out.println("4. Admin - Remove Menu Item");
        System.out.println("5. Exit");
    }

    private static void seedMenu() {
        // default menu items
        menu.add(new MenuItem(1, "Margherita Pizza", 199));
        menu.add(new MenuItem(2, "Veg Burger", 99));
        menu.add(new MenuItem(3, "French Fries", 79));
        menu.add(new MenuItem(4, "Caesar Salad", 129));
        menu.add(new MenuItem(5, "Cold Coffee", 89));
        menu.add(new MenuItem(6, "Paneer Butter Masala", 179));
    }

    private static void showMenu() {
        System.out.println("\n----- MENU -----");
        for (MenuItem m : menu) {
            System.out.println(m);
        }
    }

    private static void placeOrder() {
        if (menu.isEmpty()) {
            System.out.println("Menu is empty. Ask admin to add items.");
            return;
        }
        List<OrderItem> order = new ArrayList<>();
        boolean ordering = true;
        while (ordering) {
            showMenu();
            int id = readInt("Enter Menu ID to add to order (0 to finish): ");
            if (id == 0) break;
            Optional<MenuItem> opt = menu.stream().filter(x -> x.getId() == id).findFirst();
            if (opt.isEmpty()) {
                System.out.println("Invalid Menu ID. Try again.");
                continue;
            }
            int qty = readInt("Enter quantity: ");
            if (qty <= 0) { System.out.println("Quantity must be >= 1"); continue; }
            order.add(new OrderItem(opt.get(), qty));
            System.out.println("Added: " + opt.get().getName() + " x" + qty);
            System.out.print("Add more? (y/n): ");
            String more = sc.next().trim().toLowerCase();
            if (!more.equals("y")) ordering = false;
        }
        if (order.isEmpty()) {
            System.out.println("No items ordered.");
            return;
        }
        printReceipt(order);
    }

    private static void printReceipt(List<OrderItem> order) {
        System.out.println("\n---------- RECEIPT ----------");
        StringBuilder receipt = new StringBuilder();
        receipt.append("Simple Restaurant\n");
        receipt.append("Date: ").append(new Date()).append("\n\n");
        double subTotal = 0.0;
        for (OrderItem oi : order) {
            System.out.println(oi);
            receipt.append(String.format("%-20s x%2d  Rs %.2f\n",
                    oi.getMenuItem().getName(), oi.getQty(), oi.getTotal()));
            subTotal += oi.getTotal();
        }
        double gst = subTotal * GST_PERCENT / 100.0;
        double grandTotal = subTotal + gst;
        System.out.println("-----------------------------");
        System.out.println("Subtotal: Rs " + df.format(subTotal));
        System.out.println("GST (" + GST_PERCENT + "%): Rs " + df.format(gst));
        System.out.println("Grand Total: Rs " + df.format(grandTotal));
        System.out.println("-----------------------------");
        receipt.append("\nSubtotal: Rs ").append(df.format(subTotal)).append("\n");
        receipt.append("GST ("+GST_PERCENT+"%): Rs ").append(df.format(gst)).append("\n");
        receipt.append("Grand Total: Rs ").append(df.format(grandTotal)).append("\n");
        // Save receipt to file
        String fileName = "receipt_" + System.currentTimeMillis() + ".txt";
        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write(receipt.toString());
            System.out.println("Receipt saved to " + fileName);
        } catch (IOException e) {
            System.out.println("Could not save receipt: " + e.getMessage());
        }
    }

    private static void adminAddItem() {
        sc.nextLine(); // consume newline
        String name = readString("Enter item name: ");
        double price = readDouble("Enter price (Rs): ");
        int nextId = menu.stream().mapToInt(MenuItem::getId).max().orElse(0) + 1;
        menu.add(new MenuItem(nextId, name, price));
        System.out.println("Item added with ID " + nextId);
    }

    private static void adminRemoveItem() {
        showMenu();
        int id = readInt("Enter Menu ID to remove: ");
        boolean removed = menu.removeIf(m -> m.getId() == id);
        if (removed) {
            System.out.println("Item removed.");
            // reassign ids so menu ids remain continuous (optional)
            reassignIds();
        } else {
            System.out.println("Item id not found.");
        }
    }

    private static void reassignIds() {
        int i = 1;
        for (MenuItem m : menu) {
            try {
                // reflection to set private id (not ideal, but simple)
                java.lang.reflect.Field f = MenuItem.class.getDeclaredField("id");
                f.setAccessible(true);
                f.setInt(m, i++);
            } catch (Exception e) {
                // ignore, ids will stay as-is if reflection fails
            }
        }
    }

    // ----- Helper input methods -----
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid number. Try again.");
                sc.next(); // clear bad token
            }
        }
    }
    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return sc.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("Invalid number. Try again.");
                sc.next();
            }
        }
    }
    private static String readString(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }
}
