// -*-mode: java;-*-
package com.chasrmartin.m1.CoinOps;
import java.math.BigDecimal;
import java.util.*;

class SupplierSim implements Supplier {

    public BigDecimal quote(List<String> order) {
        return new BigDecimal("0.00");
    }
    
    public List<Product> order(List<String> order,
                               BigDecimal payment)
        throws InsufficientFundsException,
               ProductsNotAvailableException {
        return new ArrayList<Product>() ;
   
    }       
}
