// -*-mode: java;-*-
package com.chasrmartin.m1.CoinOps;

public class CoinOps {
    public static void main(String[] argv) {
        System.out.printf("Coin Operated Vending machine Simulation\n");

        VendingMachineUI machine =
            new VendingMachineUICL( new VendingMachineSim(),
                                    new CoinBoxSim( new CoinBoxUICL()));

        try {
            machine.run();
        } catch (EndSimulationException end) {
                System.out.println("Simulation ended by user.");
        } catch (RuntimeException ex) {
            System.err.printf("Abnormal termination, exception:\n%s\n",
                              ex.getMessage());
            System.exit(-1);
        }
        System.exit(0);
    }
}

