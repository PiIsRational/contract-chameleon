package example;

public abstract class Cell {
    //@ public ghost int absVal;
    //@ public ghost \locset footprint;
    //@ public invariant \subset(this.*, this.footprint);
    //@ public accessible \inv : footprint;

    /*@ normal_behavior
         requires true;
         ensures \result.absVal == 0;
         ensures \fresh(\result.footprint);
         ensures \invariant_for(\result);
        */
    public static Cell init() {
        return new CellImpl();
    }

    /*@ normal_behavior
         requires true;
         ensures \result == this.absVal;
         assignable this.footprint;
         accessible this.footprint;
        */
    public abstract int value();

    /*@ normal_behavior
         requires true;
         ensures this.absVal == v;
         ensures \new_elems_fresh(footprint);
         assignable this.footprint;
        */
    public abstract void set(int v);
}
