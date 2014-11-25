class Instantiation {
    public static void main(String[] args) {
        System.out.println(new I().run(0));
    }
}

class I {
    public int run(int i) {
        int x;
        int y;
        /* use before instantiation!!   */
        /* should be compile-time error */
        
        while (i < 0) {
            x = 1;
            i = i - 1;
        }

        return x + 1;
    }
}
