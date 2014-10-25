class ClassesDupes {
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
    int foo;
    int bar;
    public int square(int x) {
        int x;
        int x;
        return x * x;
    }
    public int square(int y) {
        return y - y;
    }
}

class C extends B {
    int baz;
}

class A {}
