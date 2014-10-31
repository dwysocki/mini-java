class Classes {
    public static void main(String[] args) {
        System.out.println(new A().run());
    }
}

class A {
    int foo;
    public int run() {
        int x;
        int y;
        B b;

        x = 1;
        y = 2;

        x = b.square();
        x = b.square(1);
        x = b.square(2,3);

        return new B().square(x + y);
    }

    public int f(int x, int z) {
        return 0;
    }
}

class B extends A{
    int foo;
    int bar;
    Fizz baz;
    public int square(int x) {
        Flub y;
        y = new Flub();

        x = this.square();
        x = this.square(true);
        x = this.square(2,3);
        return x * x;
    }

    public int f(int x, int y) {
        recur (3 < 2) ? (x+1, y-1) : x*y;
    }
}

class C extends B {
    boolean foo;
    int[] baz;

    public int f(int x) {
        return 1;
    }
}
