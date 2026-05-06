package example;

import universe.qual.*;

@DSI
public abstract class IntTreeSet {
    //@ public ghost \dl_Set absVal;
    //@ public accessible \inv : \dl_createdRepfp(this);

    // Note: needed at the moment for technical reasons, UET checker needs the annotation
    @RepOnly
    protected IntTreeSet() {
    }

    /*@ normal_behavior
         requires true;
         ensures \result.absVal == \dl_sEmpty();
         ensures \fresh(\dl_createdRepfp(\result));
         ensures \invariant_for(\result);
         assignable \nothing;
        */
    // Note: static methods do not have to adhere to @RepOnly/@Payload restrictions, @Peer is default
    public static IntTreeSet init() {
        return new IntTreeSetImpl();
    }

    /*@ normal_behavior
         requires true;
         ensures absVal == \dl_sUnion(\old(absVal), \dl_sSingleton(v));
         assignable \dl_createdRepfp(this);
        */
    @RepOnly
    public abstract void add(int v);

    /*@ normal_behavior
         requires true;
         ensures \result == \dl_sElementOf(v, this.absVal);
         accessible \dl_createdRepfp(this);
         assignable \dl_createdRepfp(this);
        */
    @RepOnly
    public abstract boolean contains(int v);
}
