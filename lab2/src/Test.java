import java.util.concurrent.CountDownLatch;

public class Test {
    public static void main(String[] args) {
        MyLock lock = new ArrayLamportLock(20);
//        MyLock lock = new LinkedLamportLock();
        testA1(lock);
    }

    static int cnt = 0;

    public static void testA1(MyLock lock) {
        System.out.println("Test A start");
        int threadNumber = 5;
        final CountDownLatch cdl = new CountDownLatch(threadNumber);//参数为线程个数
        Thread[] threads = new Thread[threadNumber];
        for (int i = 0; i < threadNumber; i++) {
            threads[i] = new Thread(() -> {
                lock.lock();
                int tmp = cnt;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                cnt = tmp + 1;
                lock.unLock();
                cdl.countDown();//此方法是CountDownLatch的线程数-1
            });
        }
        for (int i = 0; i < threadNumber; i++) {
            threads[i].start();
        }
//线程启动后调用countDownLatch方法
        try {
            cdl.await();//需要捕获异常，当其中线程数为0时这里才会继续运行
            String res = cnt == 5 ? "Test A passed" : "Test A failed,cnt should be 5, but " + cnt;
            System.out.println(res);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}