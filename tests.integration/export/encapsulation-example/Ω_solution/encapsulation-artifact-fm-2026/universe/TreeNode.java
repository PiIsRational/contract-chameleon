package example;

import universe.qual.*;

public final class TreeNode {
    private @Rep /*@ nullable */ TreeNode left;
    private @Rep /*@ nullable */ TreeNode right;
    private int value;

    //@ public ghost \dl_Set absVal;

    //@ private invariant absVal == \dl_sUnion(\dl_sSingleton(value), \dl_sUnion(left != null ? left.absVal : \dl_sEmpty(), right != null ? right.absVal : \dl_sEmpty()));
    //@ private invariant \dl_sCard(absVal) == 1 + (left != null ? \dl_sCard(left.absVal) : 0) + (right != null ? \dl_sCard(right.absVal) : 0);

    //@ private invariant left != null ==> \invariant_for(left);
    //@ private invariant left != null ==> !\dl_sElementOf(value, left.absVal);
    //@ private invariant left != null ==> (\forall int v; \dl_sElementOf(v, left.absVal) ==> v < value);

    //@ private invariant right != null ==> \invariant_for(right);
    //@ private invariant right != null ==> !\dl_sElementOf(value, right.absVal);
    //@ private invariant right != null ==> (\forall int v; \dl_sElementOf(v, right.absVal) ==> v > value);

    //@ private invariant left != null && right != null ==> right != left;
    //@ private invariant left != null && right != null ==> \dl_sIntersect(left.absVal, right.absVal) == \dl_sEmpty();

    //@ accessible \inv: \dl_createdRepfp(this);

    // leaf
    /*@ normal_behavior
      @  ensures absVal == \dl_sSingleton(v);
      @  ensures \fresh(\dl_createdRepfp(this));
      @  assignable \nothing;
      @*/
    @RepOnly
    public TreeNode(int v) {
        value = v;
        //@ set absVal = \dl_sSingleton(v);
    }

    /*@ normal_behavior
      @  ensures absVal == \dl_sUnion(\old(absVal), \dl_sSingleton(v));
      @  assignable \dl_createdRepfp(this);
      @  measured_by \dl_sCard(absVal);
      @*/
    @RepOnly
    public void add(int v) {
        if (v == value)
            return;
        else if (v < value) {
            if (left == null) {
                left = new @Rep TreeNode(v);
                //@ assert \dl_sCard(left.absVal) == 1;
            } else {
                left.add(v);
            }
        } else {
            if (right == null) {
                right = new @Rep TreeNode(v);
                //@ assert \dl_sCard(right.absVal) == 1;
            } else {
                right.add(v);
            }
        }
        //@ set absVal = \dl_sUnion(absVal, \dl_sSingleton(v));
    }

    /*@ normal_behavior
      @  ensures \result <==> \dl_sElementOf(v, absVal);
      @  assignable \strictly_nothing;
      @  measured_by \dl_sCard(absVal);
      @  accessible \dl_createdRepfp(this);
      @*/
    @RepOnly
    public boolean contains(int v) {
        if (v == value)
            return true;
        else if (v < value)
            return left != null && left.contains(v);
        else
            return right != null && right.contains(v);
    }
}
