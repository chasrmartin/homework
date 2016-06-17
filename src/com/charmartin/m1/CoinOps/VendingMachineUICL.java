// -*-mode: java;-*-
package com.chasrmartin.m1.CoinOps;

class VendingMachineUICL implements VendingMachineUI {

    private VendingMachine machine ;
    private CoinBox coinBox ;

    public VendingMachineUICL(VendingMachine machine,
                              CoinBox coinBox) {
        this.machine = machine ;
        this.coinBox = coinBox ;
    }

    public void run()
        throws EndSimulationException {

        throw new EndSimulationException();

    }
}

                                                  
