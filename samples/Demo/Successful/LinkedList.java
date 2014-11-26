class LinkedList {
    public static void main(String[] args) {
        System.out.println(new LL().demo());
    }
}

class LL {
    public int demo() {
        int i;
        int _;
        List list;

        list = new List();
        _ = list.setCAR(0);

        i = 2;
        while (i < 20) {
            list = this.cons(i, list);
            i = i + 2;
        }

        _ = list.print();

        return 1;
    }

    public List cons(int car, List cdr) {
        List cons;
        int _;

        cons = new List();
        _ = cons.setCAR(car);
        _ = cons.setCDR(cdr);

        return cons;
    }

}

class List {
    int CAR;
    List CDR;
    boolean HASCDR;

    public int car() {
        return CAR;
    }

    public List cdr() {
        return CDR;
    }

    public boolean hasCDR() {
        return HASCDR;
    }

    public int setCAR(int car) {
        CAR = car;

        return 0;
    }

    public int setCDR(List cdr) {
        CDR = cdr;
        HASCDR = true;

        return 0;
    }

    public int print() {
        List x;

        x = this;
        System.out.println(x.car());

        while (x.hasCDR()) {
            x = x.cdr();
            System.out.println(x.car());
        }

        return 0;
    }
}
