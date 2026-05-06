package example;

import universe.qual.*;

@DSI
public final class IntTreeSetImpl extends IntTreeSet {
    private @Rep /*@ nullable */ TreeNode root;

    //@ private invariant root == null <==> absVal == \dl_sEmpty();
    //@ private invariant root != null ==> \invariant_for(root) && absVal == root.absVal;
    //@ private invariant root != null ==> \disjoint(this.*, \dl_createdRepfp(root));

    /*@ normal_behavior
      @  ensures absVal == \dl_sEmpty();
      @  ensures \fresh(\dl_createdRepfp(this));
      @  assignable \nothing;
      @*/
    @RepOnly
    IntTreeSetImpl() {
        //@ set absVal = \dl_sEmpty();
    }

    @RepOnly
    public void add(int v) {
        if (root == null) {
            root = new @Rep TreeNode(v);
            //@ set absVal = \dl_sSingleton(v);
            //@ assert \dl_owner(root) == this;
        } else {
            root.add(v);
            //@ set absVal = \dl_sUnion(absVal, \dl_sSingleton(v));
        }
    }

    @RepOnly
    public boolean contains(int v) {
        if (root == null) {
            return false;
        } else {
            return root.contains(v);
        }
    }
}
