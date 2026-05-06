package example;

public final class CellImpl extends Cell {
    private int value;

    // coupling:
    //@ private invariant absVal == value;

    //@ private invariant footprint == this.*;

    CellImpl() {
        //@ set footprint = this.*;
        //@ set absVal = 0;
    }

    public int value() {
        return value;
    }

    public void set(int v) {
        value = v;
        //@ set absVal = value;
    }
}
