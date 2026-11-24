package service;

import exception.InvalidCurrencyException;
import storage.CurrencyDAO;
import java.sql.SQLException;
import java.util.Map;

public class CurrencyConverter {
    private Map<String, Double> rates; // currency code -> rate in INR

    public CurrencyConverter() throws SQLException {
        rates = new CurrencyDAO().getAllRates();
    }

    public boolean isSupported(String code) {
        return rates.containsKey(code);
    }

    public double toINR(double amount, String code) throws InvalidCurrencyException {
        if (!isSupported(code)) {
            throw new InvalidCurrencyException("Currency not supported: " + code);
        }
        return amount * rates.get(code);
    }

    public Map<String, Double> getRates() {
        return rates;
    }
}