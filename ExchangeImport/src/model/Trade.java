package model;

import java.time.LocalDate;

// Abstract parent class: common data for every trade
public abstract class Trade {
    private int tradeId;
    private String product;
    private int quantity;
    private double amount;
    private String currency;
    private String country;
    private LocalDate date;
    private int workerId;

    public Trade(int tradeId, String product, int quantity, double amount,
                 String currency, String country, LocalDate date, int workerId) {
        this.tradeId = tradeId;
        this.product = product;
        this.quantity = quantity;
        this.amount = amount;
        this.currency = currency;
        this.country = country;
        this.date = date;
        this.workerId = workerId;
    }

    // Each child class decides its own type (polymorphism)
    public abstract String getTradeType();

    public int getTradeId()      { return tradeId; }
    public String getProduct()   { return product; }
    public int getQuantity()     { return quantity; }
    public double getAmount()    { return amount; }
    public String getCurrency()  { return currency; }
    public String getCountry()   { return country; }
    public LocalDate getDate()   { return date; }
    public int getWorkerId()     { return workerId; }

    public void display() {
        System.out.printf("%-4d %-7s %-15s qty:%-5d %10.2f %-3s %-12s %s worker:%d%n",
                tradeId, getTradeType(), product, quantity, amount,
                currency, country, date, workerId);
    }
}