package storage;

import model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TradeDAO {

    // CREATE
    public void addTrade(Trade t) throws SQLException {
        String sql = "INSERT INTO trades (trade_type, product, quantity, amount, "
                   + "currency_code, country, trade_date, worker_id) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getTradeType());
            ps.setString(2, t.getProduct());
            ps.setInt(3, t.getQuantity());
            ps.setDouble(4, t.getAmount());
            ps.setString(5, t.getCurrency());
            ps.setString(6, t.getCountry());
            ps.setDate(7, Date.valueOf(t.getDate()));
            ps.setInt(8, t.getWorkerId());
            ps.executeUpdate();
        }
    }

    // READ all
    public List<Trade> getAllTrades() throws SQLException {
        return runQuery("SELECT * FROM trades ORDER BY trade_id", null);
    }

    // SEARCH
    public List<Trade> searchByProduct(String keyword) throws SQLException {
        return runQuery("SELECT * FROM trades WHERE product LIKE ?", "%" + keyword + "%");
    }

    public List<Trade> searchByCountry(String keyword) throws SQLException {
        return runQuery("SELECT * FROM trades WHERE country LIKE ?", "%" + keyword + "%");
    }

    // UPDATE
    public boolean updateAmount(int id, double newAmount) throws SQLException {
        String sql = "UPDATE trades SET amount = ? WHERE trade_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, newAmount);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    // DELETE
    public boolean deleteTrade(int id) throws SQLException {
        String sql = "DELETE FROM trades WHERE trade_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Helper: runs a SELECT (with an optional single ? parameter) and builds Trade objects
    private List<Trade> runQuery(String sql, String param) throws SQLException {
        List<Trade> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (param != null) {
                ps.setString(1, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildTrade(rs));
                }
            }
        }
        return list;
    }

    // Turns one database row into an ImportTrade or ExportTrade object
    private Trade buildTrade(ResultSet rs) throws SQLException {
        int id = rs.getInt("trade_id");
        String type = rs.getString("trade_type");
        String product = rs.getString("product");
        int qty = rs.getInt("quantity");
        double amount = rs.getDouble("amount");
        String currency = rs.getString("currency_code");
        String country = rs.getString("country");
        LocalDate date = rs.getDate("trade_date").toLocalDate();
        int workerId = rs.getInt("worker_id");

        if (type.equals("IMPORT")) {
            return new ImportTrade(id, product, qty, amount, currency, country, date, workerId);
        } else {
            return new ExportTrade(id, product, qty, amount, currency, country, date, workerId);
        }
    }
}