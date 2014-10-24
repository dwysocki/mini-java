class Classes {
    public static void main(String[] args) {
        System.out.println(new A().run());
    }
}

class A {
    public int run() {
        int x;
        int y;

        x = 1;
        y = 2;

        return new B().square(x + y);
    }
}

class B {
    int foo;
    int bar;
    public int square(int x) {
        return x * x;
    }
}

class C extends B {
    int baz;
}
