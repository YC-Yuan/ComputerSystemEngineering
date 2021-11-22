public class Dining {
    public static void main(String[] args) {
        Philosopher[] philosophers = new Philosopher[5];
        Fork[] forks = new Fork[5];
        for (int i = 0; i < forks.length; i++) {
// initialize fork object
            forks[i] = new Fork(i);
        }
        for (int i = 0; i < philosophers.length; i++) {
// initialize Philosopher object
            philosophers[i] = new Philosopher(forks[i % 5],forks[(i + 1) % 5],"Philosopher" + i);
        }

        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(philosophers[i]);
        }
        for (int i = 0; i < 5; i++) {
            threads[i].start();
        }
    }
}