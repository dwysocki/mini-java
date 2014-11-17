class Simple {
    public static void main(String[] args) {
        {
            System.out.println(new C().fac(10));
            System.out.println(new C().fac_rec(10));
            System.out.println(new C().sum(100));
        }
    }
}

class C {
    int x;
    public int fac(int n) {
        int acc;

        acc = 1;
        x = 2;

        while (0 < n) {
            acc = acc * n;
            n = n - 1;
        }

        return acc;
    }

    public int fac_rec(int n) {
        int m;

        if (1 < n)
            m = this.fac_rec(n-1);
        else
            m = 1;

        return n*m;
    }


    public int sum(int n) {
        int acc;

        acc = 0;

        while (0 < n) {
            acc = acc + n;
            n = n - 1;
        }

        return acc;
    }
}
