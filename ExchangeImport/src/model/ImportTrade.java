package model;

import java.time.LocalDate;

// Child class: goods coming INTO the country
public class ImportTrade extends Trade {

    public ImportTrade(int tradeId, String product, int quantity, double amount,
                       String currency, String country, LocalDate date, int workerId) {
        super(tradeId, product, quantity, amount, currency, country, date, workerId);
    }

    @Override
    public String getTradeType() {
        return "IMPORT";
    }
}