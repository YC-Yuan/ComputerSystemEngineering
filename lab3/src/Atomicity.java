import java.io.*;
import java.nio.file.AtomicMoveNotSupportedException;

public class Atomicity {
    public static void main(String[] args) throws InterruptedException {
        File dbFile = new File("db.txt");
        File logFile = new File("log.txt");
        Atomicity atomicity = new Atomicity(dbFile, logFile);
        Thread t = atomicity.update('2');
        t.join();
    }

    // 程序启动后 公用log与db
    private DBWriter db;
    private Logger logger;
    private volatile int lastTid = 0;
    private int[] marks = new int[10];

    private synchronized int getTid() {
        return lastTid++;
    }

    public Atomicity(File dbFile, File logFile) {
        try {
            db = new DBWriter(dbFile);
            logger = new Logger(logFile);
            // 重启时运行恢复
            recover();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 开启线程更新文件
    public Thread update(char ch) {
        Thread thread = new Thread(() -> {
            int tid = getTid();
            // 先打log
            for (int i = 0; i < 10; i++) {
                logger.log(tid, i, ch);
                write(i, ch);
            }
            // 如果都成功 就commit
            logger.commit(tid);
        });
        thread.start();
        return thread;
    }


    private void write(int index, char ch) {

    }

    private void recover() {

    }
}
