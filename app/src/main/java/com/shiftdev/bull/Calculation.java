package com.shiftdev.bull;

import androidx.annotation.Nullable;

//import org.parceler.Parcel;
//import org.parceler.ParcelConstructor;

//@Parcel
public class Calculation {
     //   @SerializedName("symbol")
     public String symbol;

     //    @SerializedName("shares")
     public int shares;

     //  @SerializedName("buy")
     public double buy;

     //@SerializedName("sell")
     public double sell;

     //   @SerializedName("comm")
     public double comm;

     public Calculation() {
          //no arg constructor
     }

     //  @ParcelConstructor
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

     @Override
     public String toString() {
          return "Calculation{" +
                  "symbol='" + symbol + '\'' +
                  ", shares=" + shares +
                  ", buy=" + buy +
                  ", sell=" + sell +
                  ", comm=" + comm +
                  '}';
     }
}
