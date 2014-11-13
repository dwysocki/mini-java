class Simple {
    public static void main(String[] args) {
        {
            System.out.println(new C().fac());
            System.out.println(new C().sum());
        }
    }
}

class C {
    public int fac() {
        int acc;
        int n;

        acc = 1;
        n = 10;

        while (0 < n) {
            acc = acc * n;
            n = n - 1;
        }

        return acc;
    }

    public int sum() {
        int acc;
        int n;

        acc = 0;
        n = 10;

        while (0 < n) {
            acc = acc + n;
            n = n - 1;
        }

        return acc;
    }
}
