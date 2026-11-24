import exception.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import model.*;
import service.*;
import storage.*;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static WorkerDAO workerDAO = new WorkerDAO();
    private static CurrencyConverter converter;
    private static TradeManager manager;

    public static void main(String[] args) {
        // Step 1: connect to the database and load exchange rates
        try {
            converter = new CurrencyConverter();
            manager = new TradeManager(converter);
        } catch (SQLException e) {
            System.out.println("Could not connect to the database: " + e.getMessage());
            System.out.println("Check MySQL is running and the password in DBConnection.java.");
            return;
        }

        // Step 2: show the menu until the user chooses 0
        int choice;
        do {
            showMenu();
            choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1:  addWorker(); break;
                case 2:  viewWorkers(); break;
                case 3:  addTrade(true); break;   // import
                case 4:  addTrade(false); break;  // export
                case 5:  viewAllTrades(); break;
                case 6:  searchTrades(true); break;
                case 7:  searchTrades(false); break;
                case 8:  updateTrade(); break;
                case 9:  deleteTrade(); break;
                case 10: showSummary(); break;
                case 11: showRates(); break;
                case 0:  System.out.println("Goodbye!"); break;
                default: System.out.println("Invalid choice, try again.");
            }
        } while (choice != 0);
    }

    private static void showMenu() {
        System.out.println("\n===== IMPORT-EXPORT TRADE TRACKER =====");
        System.out.println("1.  Add worker");
        System.out.println("2.  View workers");
        System.out.println("3.  Add IMPORT trade");
        System.out.println("4.  Add EXPORT trade");
        System.out.println("5.  View all trades");
        System.out.println("6.  Search trades by product");
        System.out.println("7.  Search trades by country");
        System.out.println("8.  Update a trade amount");
        System.out.println("9.  Delete a trade");
        System.out.println("10. Summary (totals in INR)");
        System.out.println("11. View exchange rates");
        System.out.println("0.  Exit");
    }

    // ---------- Worker options ----------
    private static void addWorker() {
        String name = readText("Worker name: ");
        String role = readText("Role: ");
        String phone = readText("Phone: ");
        try {
            workerDAO.addWorker(new Worker(0, name, role, phone));
            System.out.println("Worker added!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void viewWorkers() {
        try {
            List<Worker> workers = workerDAO.getAllWorkers();
            if (workers.isEmpty()) {
                System.out.println("No workers found.");
                return;
            }
            for (Worker w : workers) {
                w.display();
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // ---------- Trade options ----------
    private static void addTrade(boolean isImport) {
        try {
            String product = readText("Product name: ");
            int qty = readInt("Quantity: ");
            double amount = readDouble("Amount: ");
            String currency = readText("Currency code (USD/EUR/INR/GBP/AED/JPY): ").toUpperCase();
            String country = readText(isImport ? "Imported from (country): " : "Exported to (country): ");
            LocalDate date = readDate();
            int workerId = readInt("Worker ID handling this trade: ");

            Trade t;
            if (isImport) {
                t = new ImportTrade(0, product, qty, amount, currency, country, date, workerId);
            } else {
                t = new ExportTrade(0, product, qty, amount, currency, country, date, workerId);
            }
            manager.addTrade(t);
            System.out.println("Trade saved!");
        } catch (InvalidAmountException | InvalidCurrencyException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error (is the worker ID valid?): " + e.getMessage());
        }
    }

    private static void viewAllTrades() {
        try {
            printTrades(manager.getAllTrades());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void searchTrades(boolean byProduct) {
        try {
            String keyword = readText(byProduct ? "Product to search: " : "Country to search: ");
            List<Trade> result = byProduct ? manager.searchByProduct(keyword)
                                           : manager.searchByCountry(keyword);
            printTrades(result);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void updateTrade() {
        try {
            int id = readInt("Trade ID to update: ");
            double newAmount = readDouble("New amount: ");
            if (manager.updateAmount(id, newAmount)) {
                System.out.println("Trade updated!");
            } else {
                System.out.println("No trade found with that ID.");
            }
        } catch (InvalidAmountException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void deleteTrade() {
        try {
            int id = readInt("Trade ID to delete: ");
            if (manager.deleteTrade(id)) {
                System.out.println("Trade deleted!");
            } else {
                System.out.println("No trade found with that ID.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void showSummary() {
        try {
            double imports = manager.getTotalInINR("IMPORT");
            double exports = manager.getTotalInINR("EXPORT");
            System.out.printf("Total imports : INR %.2f%n", imports);
            System.out.printf("Total exports : INR %.2f%n", exports);
            System.out.printf("Net balance   : INR %.2f (exports - imports)%n", exports - imports);

            System.out.println("\nTrade value by country (INR):");
            for (Map.Entry<String, Double> entry : manager.getTotalsByCountry().entrySet()) {
                System.out.printf("  %-15s %.2f%n", entry.getKey(), entry.getValue());
            }
        } catch (SQLException | InvalidCurrencyException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showRates() {
        System.out.println("Currency -> INR rate");
        for (Map.Entry<String, Double> entry : converter.getRates().entrySet()) {
            System.out.printf("  1 %s = %.4f INR%n", entry.getKey(), entry.getValue());
        }
    }

    private static void printTrades(List<Trade> trades) {
        if (trades.isEmpty()) {
            System.out.println("No trades found.");
            return;
        }
        for (Trade t : trades) {
            t.display();
        }
    }

    // ---------- Input helpers (keep the program from crashing on bad input) ----------
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) {
                return s;
            }
            System.out.println("This cannot be empty.");
        }
    }

    private static LocalDate readDate() {
        while (true) {
            System.out.print("Date (yyyy-MM-dd, or press Enter for today): ");
            String s = sc.nextLine().trim();
            if (s.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(s);
            } catch (DateTimeParseException e) {
                System.out.println("Use the format yyyy-MM-dd, e.g. 2026-11-24");
            }
        }
    }
}