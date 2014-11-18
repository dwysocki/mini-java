class Simple {
    public static void main(String[] args) {
        {
            System.out.println(new C().f(10));
            System.out.println(new C().arr(13));
            System.out.println(new C().fac(10));
            System.out.println(new C().fac_rec(10));
            System.out.println(new C().sum(100));
        }
    }
}

class C {
    public int f(int n) {
        return this.f_iter(n, 1);
    }

    public int f_iter(int n, int acc) {
        recur (1 < n) ? (n-1, n*acc) : n*acc;
    }

    public int arr(int n) {
        int[] a;

        a = new int[n];

        return a.length;
    }

    public int fac(int n) {
        int acc;

        acc = 1;

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
