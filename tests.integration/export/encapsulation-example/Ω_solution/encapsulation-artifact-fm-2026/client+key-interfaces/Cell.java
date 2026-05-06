package example;

public abstract class Cell {

    //@ public instance ghost \locset footprint;


    //@ public instance invariant \subset(this.*, this.footprint);


    //@ public instance accessible \inv : footprint;


    //@ public instance ghost int absVal;


    /*@  normal_behavior
        requires (false | true);
        ensures (true & (true ==> \result.absVal == 0));
        ensures \fresh(\result.footprint);
        ensures \invariant_for(\result);
        */
    public static Cell init() {
        //NOTE: This is never called, it is only the interface for verification! The actual implementation is in ../key/Cell.java and proven with KeY. Therefore, assume false is ok here.
        //@ assume false;
        return null;
    }

    /*@  normal_behavior
        requires (false | true);
        ensures (true & (true ==> \result == this.absVal));
        accessible this.footprint;
        assignable this.footprint;
        */
    public abstract int value();

    /*@  normal_behavior
        requires (false | true);
        ensures (true & (true ==> this.absVal == v));
        assignable this.footprint;
        */
    public abstract void set(int v);
}
