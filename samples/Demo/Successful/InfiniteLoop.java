class InfiniteLoop {
    public static void main(String[] args) {
        /* Change 'false' to 'true' to cause the infinite loop.
         *
         * I've done this so that the sample run doesn't get stuck here.
         */
        if (false) {
            System.out.println(new Inf().loop());
        } else {
            System.out.println(0);
        }
    }
}

class Inf {
    /**
     *  Infinite recursive loop using a single stack frame.
     **/
    public int loop() {
        System.out.println(1);

        recur true ? () : 0;
    }
}
