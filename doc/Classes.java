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

        x = 1;
        y = 2;

        return new B().square(x + y);
    }
}

class B extends A{
    int foo;
    int bar;
    int baz;
    public int square(int x) {
        return x * x;
    }
}

class C extends B {
    boolean foo;
    int[] baz;
}
