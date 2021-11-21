import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class LamportLock implements MyLock {
    private final boolean[] choosing;
    private final int[] numbers;

    public LamportLock(int count) {
        choosing = new boolean[count];
        numbers = new int[count];
    }

    private int getMaxNumber() {
        int ans = 0;
        for (int number : numbers) {
            ans = Math.max(number, ans);
        }
        return ans;
    }

    @Override
    public void lock() {
        Thread currentThread = Thread.currentThread();
        int id = (int) currentThread.getId();
        choosing[id] = true;
        numbers[id] = 1 + getMaxNumber();
        choosing[id] = false;
        for (int i = 0; i < choosing.length; i++) {
            if (id != i) {// 考虑所有其他线程
                while (choosing[i]) {
//                    System.out.println(id + " waiting for chooser " + i);
                }
                ;
                while ((numbers[i] != 0) &&
                        ((numbers[i] < numbers[id]) ||
                                (numbers[i] == numbers[id]) && i < id)) {
//                    System.out.println(id + " " + numbers[id] + " waiting for smaller " + i + " " + numbers[i]);
                }
                ;
            }
        }
    }

    @Override
    public void unLock() {
        Thread currentThread = Thread.currentThread();
        int id = (int) currentThread.getId();
        numbers[id] = 0;
    }

    public static void main(String[] args) {
    }
}
