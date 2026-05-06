package example;

class Client {
    // contract is needed to allow verification of assertions in KeY
    /*@ normal_behavior
      @  ensures true;
      @*/
    public static void m() {
        // Cell: proven with DF + KeY
        Cell c0 = Cell.init(); c0.set(5);
        Cell c1 = Cell.init(); c1.set(9);

        // IntTreeSet: proven with UT + KeY
        IntTreeSet set = IntTreeSet.init();
        set.add(5); set.add(9);

        // LinkedCellList: proven with VeriFast
        LinkedCellList l1 = LinkedCellList.init();
        l1.add(c0); l1.add(c1);

        Cell last1 = l1.getLast();
        int r1 = last1.value();
        assert set.contains(r1);
        assert r1 == 9;
    }
}
