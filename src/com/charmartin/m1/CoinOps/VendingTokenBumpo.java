// -*-mode: java;-*-
package com.chasrmartin.m1.CoinOps;

import java.math.BigDecimal;
// "Bumpo" is Japanglish slang for "a silly fun thing that replaces a
// serious thing".

class VendingTokenBumpo implements VendingToken {
    // This Bumpo implementation uses the standard
    // BigDecimal. Shouldn't use float because money is always
    // computed precisely. A better implementation would be to define
    // a Currency class but this code is getting out of hand for a
    // worked example as is.
    
    private BigDecimal amount ;
}
