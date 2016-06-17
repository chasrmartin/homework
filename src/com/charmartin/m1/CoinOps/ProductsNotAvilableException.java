// -*-mode: java;-*-
package com.chasrmartin.m1.CoinOps;
import java.util.*;

class ProductsNotAvailableException extends Exception {
    List<String> unavailable ;
    public List<String> getUnavailable(){ return this.unavailable; }
    public ProductsNotAvailableException (List<String> unavailable) {
        super("It's over.");
        this.unavailable = unavailable;
    }
}
        
