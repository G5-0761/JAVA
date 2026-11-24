package storage;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CurrencyDAO {

    // Reads all exchange rates into a HashMap, e.g. "USD" -> 83.5
    public Map<String, Double> getAllRates() throws SQLException {
        Map<String, Double> rates = new HashMap<>();
        String sql = "SELECT currency_code, rate_to_inr FROM currencies";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rates.put(rs.getString("currency_code"), rs.getDouble("rate_to_inr"));
            }
        }
        return rates;
    }
}