package model;

import java.time.LocalDate;

// Child class: goods going OUT of the country
public class ExportTrade extends Trade {

    public ExportTrade(int tradeId, String product, int quantity, double amount,
                       String currency, String country, LocalDate date, int workerId) {
        super(tradeId, product, quantity, amount, currency, country, date, workerId);
    }

    @Override
    public String getTradeType() {
        return "EXPORT";
    }
}