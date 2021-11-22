public class Philosopher implements Runnable {
    private final Fork leftFork;
    private final Fork rightFork;
    final String name;

    Philosopher(Fork left,Fork right,String name) {
        this.leftFork = left;
        this.rightFork = right;
        this.name = name;
    }


    private void doAction(String action) throws InterruptedException {
        System.out.println(name + " " + action);
        Thread.sleep(((int) (Math.random() * 100)));
    }

    void think() throws InterruptedException {
        doAction(System.nanoTime() + ": Thinking"); // thinking
    }

    void eat() throws InterruptedException {
        doAction(System.nanoTime() + ": Eating"); // thinking
    }

    void pick_up_left_fork() throws InterruptedException {
        leftFork.lock();
        doAction(System.nanoTime() + ": Pick up left fork");
    }

    void pick_up_right_fork() throws InterruptedException {
        rightFork.lock();
        doAction(System.nanoTime() + ": Pick up right fork");
    }

    void put_down_right_fork() throws InterruptedException {
        doAction(System.nanoTime() + ": Put down right fork");
        rightFork.unLock();
    }

    void put_down_left_fork() throws InterruptedException {
        doAction(System.nanoTime() + ": Put down left fork");
        leftFork.unLock();
    }

    void pick_up_first_key() throws InterruptedException {
        if (leftFork.getId() < rightFork.getId()) {
            pick_up_left_fork();
        }
        else {
            pick_up_right_fork();
        }
    }

    void pick_up_second_key() throws InterruptedException {
        if (leftFork.getId() > rightFork.getId()) {
            pick_up_left_fork();
        }
        else {
            pick_up_right_fork();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                think();
                // pick_up_left_fork();
                // pick_up_right_fork();
                pick_up_first_key();
                pick_up_second_key();
                eat();
                put_down_left_fork();
                put_down_right_fork();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}