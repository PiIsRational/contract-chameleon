package example;

public abstract class LinkedCellList {

    //@ public instance ghost \locset footprint;


    //@ public instance invariant \subset(this.*, this.footprint);


    //@ public instance accessible \inv : footprint;


    //@ public instance ghost \seq absVal;


    //@ public instance invariant (\forall int a;0<=a<((\seq) this.absVal).length; ((\seq) this.absVal)[a] instanceof Cell);


    /*@  normal_behavior
        requires (false | true);
        ensures (true & (true ==> \result.absVal == \seq_empty));
        ensures \fresh(\result.footprint);
        ensures \invariant_for(\result);
        */
    public static LinkedCellList init() {
        //NOTE: This is never called, it is only the interface for verification! The actual implementation is in ../verifast/LinkedCellList.java and proven with VeriFast. Therefore, assume false is ok here.
        //@ assume false;
        return null;
    }

    /*@  normal_behavior
        requires (false | true);
        ensures (true & (true ==> this.absVal == \seq_concat(\old(this.absVal), \seq_singleton(v))));
        assignable this.footprint;
        */
    public abstract void add(Cell v);

    /*@  normal_behavior
        requires (false | this.absVal != \seq_empty);
        ensures (true & (this.absVal != \seq_empty ==> \result == this.absVal[this.absVal.length - 1]));
        accessible this.footprint;
        assignable this.footprint;
        */
    public abstract Cell getLast();
}
