import java.io.*;

public class Atomicity {
    public static void main(String[] args) throws InterruptedException {
        Atomicity atomicity = new Atomicity();
        Thread t = atomicity.update('2');
        t.join();
    }

    private static RandomAccessFile db;
    private static PrintWriter log;

    static {
        try {
            db = new RandomAccessFile(new File("./db.txt"), "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            log = new PrintWriter(new FileWriter(new File("./log.txt"), true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 开启线程更新文件
    public Thread update(char ch) {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                write(i, ch);
            }
        });
        thread.start();
        return thread;
    }


    private void write(int index, char ch) {
        try {
            // 读出旧值
            char old = dbRead(index);
            // log
            log("line " + index + ", writing " + old + " to " + ch);
            // 写入新值
            dbWrite(index, ch);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void log(String text) {
        log.println(text);
        log.flush();
    }

    private void recover() {

    }


    private char dbRead(int index) throws IOException {
        db.seek(index * 2L);
        return (char) db.read();
    }

    private void dbWrite(int index, char ch) throws IOException {
        db.seek(index * 2L);
        db.write((ch + "\n").getBytes());
    }
}
