class Factorial {
    public static void main(String[] args) {
        System.out.println(new Fac().fac_all(10));
    }
}

class Fac {
    public int fac_all(int n) {
        System.out.println(this.fac_iter(n));
        System.out.println(this.fac_recur(n));

        return n;
    }

    public int fac_iter(int n) {
        int acc;
        acc = 1;

        while (0 < n) {
            acc = n*acc;
            n = n - 1;
        }

        return acc;
    }

    public int fac_recur(int n) {
        return this.fac_recur_helper(n, 1);
    }

    public int fac_recur_helper(int n, int result) {
        recur (0 < n) ? (n-1, n*result) : result;
    }
}
