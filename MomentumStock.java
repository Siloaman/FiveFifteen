package com.holdings.siloaman.fivefifteen;

import java.util.Date;



public class MomentumStock {

    public String Ticker;
    public String Name;

    public String Sector;
    public String Industry;

    // attempt to capture the general candlestick formations
    public double five_min_open;
    public double five_min_high;
    public double five_min_low;
    public double five_min_close;

    public double Price;
    public double Percentage;
    public double PreMarketPercentage;
    public int Volume;

    public double PreviousClose;
    public double Open;

    public double DayRangeLow;
    public double DayRangeHigh;

    public Image StockChart;

    public String NewsAnalysis;
    public double Yield;
    public Date ExDate;

    public MomentumStock(String ticker, double price, double percentage, int volume){

        this.Ticker = ticker;
        this.Price = price;
        this.Percentage = percentage;
        this.Volume = volume;

    }

    public String getTicker(){
        return Ticker;
    }
    public double getPrice() { return Price; }
    public double getPercentage() { return Percentage; }
    public int getVolume() { return Volume; }

}
