class Simple {
    public static void main(String[] args) {
        {
            System.out.println(new C().f());
            /*
            if (!true) {
                System.out.println(5);
            } else {
                if (-3 < -100) {
                    System.out.println(6);
                } else {
                    System.out.println(7);
                }
            }
            */
        }
    }
}

class C {
    public int f() {
        return 1;
    }
}
