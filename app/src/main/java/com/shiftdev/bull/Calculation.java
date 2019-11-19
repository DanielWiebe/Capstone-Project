package com.shiftdev.bull;

import androidx.annotation.Nullable;

public class Calculation {
     private String symbol;
     private int shares;
     private double buy;
     private double sell;
     private double comm;

     public Calculation() {
          //no arg constructor
     }

     Calculation(@Nullable String symbol, int shares, double buy, double sell, double comm) {
          this.symbol = symbol;
          this.shares = shares;
          this.buy = buy;
          this.sell = sell;
          this.comm = comm;
     }

     String getSymbol() {
          return symbol;
     }

     public void setSymbol(String symbol) {
          this.symbol = symbol;
     }

     int getShares() {
          return shares;
     }

     public void setShares(int shares) {
          this.shares = shares;
     }

     double getBuy() {
          return buy;
     }

     public void setBuy(double buy) {
          this.buy = buy;
     }

     double getSell() {
          return sell;
     }

     public void setSell(double sell) {
          this.sell = sell;
     }

     double getComm() {
          return comm;
     }

     public void setComm(double comm) {
          this.comm = comm;
     }
}
