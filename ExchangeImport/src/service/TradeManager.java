package service;

import exception.*;
import model.Trade;
import storage.TradeDAO;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Business logic: validates input, then asks TradeDAO to talk to the database
public class TradeManager {
    private TradeDAO tradeDAO = new TradeDAO();
    private CurrencyConverter converter;

    public TradeManager(CurrencyConverter converter) {
        this.converter = converter;
    }

    public void addTrade(Trade t) throws InvalidAmountException, InvalidCurrencyException, SQLException {
        if (t.getAmount() <= 0 || t.getQuantity() <= 0) {
            throw new InvalidAmountException("Amount and quantity must be greater than 0.");
        }
        if (!converter.isSupported(t.getCurrency())) {
            throw new InvalidCurrencyException("Currency not supported: " + t.getCurrency());
        }
        tradeDAO.addTrade(t);
    }

    public List<Trade> getAllTrades() throws SQLException {
        return tradeDAO.getAllTrades();
    }

    public List<Trade> searchByProduct(String keyword) throws SQLException {
        return tradeDAO.searchByProduct(keyword);
    }

    public List<Trade> searchByCountry(String keyword) throws SQLException {
        return tradeDAO.searchByCountry(keyword);
    }

    public boolean updateAmount(int id, double newAmount) throws InvalidAmountException, SQLException {
        if (newAmount <= 0) {
            throw new InvalidAmountException("Amount must be greater than 0.");
        }
        return tradeDAO.updateAmount(id, newAmount);
    }

    public boolean deleteTrade(int id) throws SQLException {
        return tradeDAO.deleteTrade(id);
    }

    // Adds up all trades of one type ("IMPORT" or "EXPORT") converted to INR
    public double getTotalInINR(String type) throws SQLException, InvalidCurrencyException {
        double total = 0;
        for (Trade t : tradeDAO.getAllTrades()) {
            if (t.getTradeType().equals(type)) {
                total += converter.toINR(t.getAmount(), t.getCurrency());
            }
        }
        return total;
    }

    // Total trade value per country (in INR), stored in a HashMap
    public Map<String, Double> getTotalsByCountry() throws SQLException, InvalidCurrencyException {
        Map<String, Double> totals = new HashMap<>();
        for (Trade t : tradeDAO.getAllTrades()) {
            double inr = converter.toINR(t.getAmount(), t.getCurrency());
            totals.put(t.getCountry(), totals.getOrDefault(t.getCountry(), 0.0) + inr);
        }
        return totals;
    }
}