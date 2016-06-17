// -*-mode: java;-*-
package com.chasrmartin.m1.CoinOps;
import java.math.BigDecimal;
import java.util.*;

interface Supplier {

    public BigDecimal quote(List<String> order);
    public List<Product> order(List<String> order,
                               BigDecimal payment)
        throws InsufficientFundsException,
               ProductsNotAvailableException ;
   
}
