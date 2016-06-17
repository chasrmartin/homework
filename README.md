

# Notes on Exam Assignment #

So I'm just writing some notes to get the design clear in my head.  This is a simple Java simulation of a vending machine that accepts currency in exchange for a product stored in the vending machine.

## Object Model

The domain model is straightforward: nouns and verbs lead us to a domain including:

* a VendingMachine
* a Product

The VendingMachine needs to store an inentory of products, and present a user interface to allow the machine to interact with the users.  Following Parnas's Rule for objects -- an object should keep a secret, that secret being a point at which requirements can change -- leads us to a sort of degenerate model-view-controller pattern: we need an object hiding the details of user interaction. In this case, that will be a  simple command-line interface; in other situations that might be replaced with anything from a web interface to a specification for hardware.

Similarly, we need something to handle the actual exchange of currency. Obviously, one thing that can change is the particular currency accepted. without loss of generality for this exercise we'll just accept US currency, limited to 5, 10, and 25 cent coins and dollar bills, but the implementation should be easily adaptable to other currencies.

This gives us two more classes:

* a VendingMachineUI
* a CoinBox

(Suggestions for better ideas for the class names welcomed.)

A CoinBox needs a CoinBoxUI

* a CoinBoxUI

Something implicit in the machine description is the notion of a Supplier that can load the machine with Products and reload the CoinBox with change. For this simpulation, this basically looks like a Data Access Object that, on demand, fills an order or fills the CoinBox.

One easy approach here is to make the Supplier follow the Factory pattern, accepting an order, and filling it by returning Product objects. The VendingMachine knows its current inventory and its capacity, and in response to an external stimulus requests the products needed to fill itself to capacity. (The CoinBox must also include a similar feature.) So, this adds one more class to the object model:

* a Supplier

### Notes

* This design overall is being done for a relatively quick exercise. In a real project, I'd extend this by building a sequence diagram for the objects and laying out at least some representative use cases, to look for pathologies in the messaging (such as a central controller "ladder".) In this case, I'm trusting my intuition.

* Although concurrency issues are called out in the problem statement, I have to admit that I can't think of a vending machine I've ever seen that physically supports much concurrency. I can imagine a machine that allows a supplier to load one end while it vends from the other, though, so we'll assume the need for mutex on the inventory and coin box change supply.

* True hcakers remember the coke machines in various computer science departments that accepted keyboard commands from a user's machine and vended (is that a word?) products, charging the cost against a pre-paid account held by the department. This presents interesting security and concurrency issues that are largely left as an exercise for the interested reader. In one small bow in that direction, however, instead of simply exchanging a currency amount between the CoinBox and the VendingMachine, what will be exchanged is a VendingToken object that represents all the needed details of the transaction. Conveniently, this also helps support multiple currencies, by having the VendingObject canonicalize the amount to the VendingMachine's native currency.

* For reference later, the full list of domain objects then is:
  - VendingMachine
  - VendingMachineUI
  - Product (no UI, what the customer does with a delivered product is their own damn business)
  - CoinBox
  - CoinBoxUI
  - Supplier
  - VendingToken, with the degenerate case being simply an object containing a transaction value.
  
## Implementation

The target implementation language here is Java. It has become common in Java -- for good reason! -- to implement all classes as an Interface and an Implementation.  We shall follow this convention rigorously, and use a naming convention where:

* domain level objects are defined as _interfaces_, and their implementations as classes that implement the interface.

* the domain level _interface_ will get the unadorned domain object name, and the implementation a name ornamented with information about how it implements that interface.  Thus, for example, `VendingMachine` is declared as an interface, and `VendingMachineSim` as a class implementing `VendingMachine` and simulating the behavior of the real machine.

### Sources

    .
    ├── README.md
    ├── build.xml
    └── src
        ├── com
        │   └── charmartin
        │       └── m1
        │           └── CoinOps
        │               ├── CoinBox.java
        │               ├── CoinBoxCL.java
        │               ├── CoinBoxSim.java
        │               ├── CoinBoxUI.java
        │               ├── CoinOps.java
        │               ├── Product.java
        │               ├── ProductSim.java
        │               ├── Supplier.java
        │               ├── SupplierSim.java
        │               ├── VendingMachine.java
        │               ├── VendingMachineSim.java
        │               ├── VendingMachineUI.java
        │               ├── VendingMachineUICL.java
        │               ├── VendingToken.java
        │               └── VendingTokenBumpo.java
        └── manifest.mf


## Design Interfaces By Wrriting User Code

Now that we've completed out object model, we can design the interfaces. The easiest way to do this is to _write the code you wish you could write to do that problem_. So, the main routine simply needs to set up the appropriate UI and run it.  At that time we need to choose a particular implemention. That UI needs to know about the CoinBox, which needs to know about the CoinBox UI to present.

So, the code for that will look like this:

```
    VendingMachineUI machine =
        new VendingMachineUICL( new VendingMachineSim(),
                                new CoinBoxSim( new CoinBoxUICL()));
```

which tells us the ctor for a VendingMachineUI needs to look like:

```
    public VendingMachineUICL(VendingMachine machine,
                              CoinBox coinBox)
```

and then we also know that the CoinBox ctor needs its own UI:

```
    public CoinBoxSim(CoinBoxUI ui )
```

Much rest of the implementation for this is left as an exercise (read "maybe I'll finish this out as a full-fledged article later"), but I do want to examine two points.

First, the VendingMachineUI implements a single method:
```
    public void run() throws EndSimulationException
```

This, purposely, looks a bit like a `lava.lang.Runnable` to allow for multiple instances of the UI, for a hypothetical Horn&Hardarts super-vending-machine -- or, with some fiddling, an implementation as a Swing application.  

It also illustrates an example of some things that are a bit of a pet peeve for me: 

* exceptions visible to the outside world should be specific to the application, not something generic
* exceptions should be used for exceptional conditions.

In this case, we define an application-specific exception with a somewhat whimsical implementation:

```
class EndSimulationException extends Exception {
    public EndSimulationException () {
        super("It's over.");
    }
}
```

which is thrown by the UI when the user is tired of the simulation. Why not just make this a returned value from the `::run()` method? Because while its a normal bit of processing for a _simulation_, "stop vending and destroy yourself" is _not_ normal for a VendingMachine in real life. 

Second, the Supplier interface is interesting as an example of a Factory pattern. (I think it's strictly a Factory Method, but I always thought the GoF broke that up too finely and so never quite remeber.) 

In this case, we consider what a Supplier looks like in the real world: when you want to make an order, you first obtain a quote to find out how much it will cost, then you make the order, presenting payment, and the Supplier delivers the goods.

So, we give the Supplier a two-part interface:
```
    public BigDecimal quote(Array<String> order);
    public Array<Product> order(Array<String> order,
                                BigDecimal payment)
        throws InsufficientFundsException,
               ProductsNotAvailableException ;
```

The associated user code is something like:
* compute the needed Products to fill the machine to capacity
* obtain a quote from the supplier
* make an order.

In normal processing, this will result in the Supplier returning an array of product items. If a ProductNotAvailable exception is thrown, it includes in the exception a list of the products the Supplier can't supply, allowing the caller to modify their order or potentially consult another supplier.

# Conclusions

This is a (somewhat extended) example of my design process for a vending machine simulation.  The code is (barely) enough to allow it to compile, but it does compile and happily runs, ending the simulation having done nothing of significans.

I hope this satisifies your needs.


**EMACS goodies**

Ignore this, I'm just saving myself a little typing and messing about.

```
    Local Variables:
    compile-command: "ant -emacs -find build.xml"
    End:
```
