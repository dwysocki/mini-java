class InfiniteLoop {
    public static void main(String[] args) {
        System.out.println(new Inf().loop());
    }
}

class Inf {
    public int loop() {
        System.out.println(1);

        recur true ? () : 0;
    }
}
