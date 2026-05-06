package example;

public abstract class IntTreeSet {

    //@ public instance ghost \locset footprint;


    //@ public instance invariant \subset(this.*, this.footprint);


    //@ public instance accessible \inv : footprint;


    //@ public instance ghost \dl_Set absVal;


    /*@  normal_behavior
        requires (false | true);
        ensures (true & (true ==> \result.absVal == \dl_sEmpty()));
        ensures \fresh(\result.footprint);
        ensures \invariant_for(\result);
        */
    public static IntTreeSet init() {
        //NOTE: This is never called, it is only the interface for verification! The actual implementation is in ../universe/IntTreeSet.java and proven with Universe Types + KeY. Therefore, assume false is ok here.
        //@ assume false;
        return null;
    }

    /*@  normal_behavior
        requires (false | true);
        ensures (true & (true ==> (\forall int x;\dl_sElementOf(x, \old(this.absVal)) || x == v) && \dl_sElementOf(v, this.absVal)));
        assignable this.footprint;
        */
    public abstract void add(int v);

    /*@  normal_behavior
        requires (false | true);
        ensures (true & (true ==> \result == \dl_sElementOf(v, this.absVal)));
        accessible this.footprint;
        assignable this.footprint;
        */
    public abstract boolean contains(int v);
}
