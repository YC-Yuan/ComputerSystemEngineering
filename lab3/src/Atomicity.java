import java.io.*;
import java.nio.file.AtomicMoveNotSupportedException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Atomicity {
    public static void main(String[] args) throws InterruptedException {
        Atomicity atomicity = new Atomicity("db.txt", "log.txt");
        List<Thread> ts = new ArrayList<>();
        ts.add(atomicity.update('1'));
        ts.add(atomicity.update('2'));
        ts.add(atomicity.update('3'));
        ts.add(atomicity.update('4'));
        for (Thread t : ts) {
            t.join();
        }
    }

    // 程序启动后 公用log与db
    private DBWriter db;
    private Logger logger;
    private volatile int lastTid;

    // 两个线程安全flag数组,分别表示变量是否正在被编辑,变量的water-mark
    ConcurrentHashMap<Integer, Integer> waterMarks = new ConcurrentHashMap<>();

    public Atomicity(String dbPath, String logPath) {
        try {
            db = new DBWriter(dbPath);
            logger = new Logger(logPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 找到最新的tid
        lastTid = logger.getLastTid();
        // waterMarks初始化
        for (int i = 0; i < 10; i++) {
            waterMarks.put(i, -1);
        }
    }

    private synchronized int getTid() {
        return ++lastTid;
    }

    private synchronized boolean checkWaterMark(int index, int tid) {
        if (waterMarks.get(index) > tid) {
            System.out.println("tid:" + tid + "请求查看waterMark" + index + ",值为" + waterMarks.get(index) + ",abort!");
            return false;
        } else {
            waterMarks.put(index, tid);
            return true;
        }
    }

    // 开启线程更新文件
    public Thread update(char ch) {
        Thread thread = new Thread(() -> {
            int tid = getTid();
            // 开启事务
            logger.start(tid);
            // recover
            try {
                // 先恢复再更新
                System.out.println("tid" + tid + "启动recover");
                recover();
                System.out.println("tid" + tid + "结束recover");
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 先打log
            try {
                for (int i = 0; i < 10; i++) {
                    // read-capture策略中检查waterMark步骤
                    if (!checkWaterMark(i, tid)) {
                        // 需要abort
                        throw new Exception();
                    }
                    char old = db.read(i);
                    logger.write(tid, i, old, ch);
                    Thread.sleep(1000);
                    // 写入
                    if (Math.random() < 0.05) {
                        // 人为制造随机写入失败,也将导致abort
                        System.out.println("倒霉蛋tid+" + tid + "写入失败了,abort!");
                        throw new Exception();
                    }
                    db.write(i, ch);
                }
                // 如果都成功 就commit
                logger.commit(tid);
            } catch (Exception e) {
                // 出错abort
                logger.abort(tid);
                update(ch);
            } finally {
                // 不论如何 存log
                logger.save();
            }
        });
        thread.start();
        return thread;
    }

    private synchronized void recover() throws IOException {
        // 反读 对于每个abort的tid,撤销其操作,但如果有更晚的commit,就不用撤销
        List<Log> logs = logger.logs;
        Set<Integer> recovers = new HashSet<>();
        for (int i = logs.size() - 1; i >= 0; i--) {
            Log log = logs.get(i);
            // 反读
            if (log.isAbort()) {
                // 发现ABORT!
                recovers.add(log.tid);
            } else if (log.isCommit()) {
                // 已有commit,不需要再撤销更早的修改了
                break;
            }
            // 需要恢复的操作
            if (log.isWrite() && recovers.contains(log.tid)) {
                // 撤销操作,即写回旧值
                System.out.println("recover执行:" + "撤销tid" + log.tid + "对index" + log.index + "的" + log.old + "->" + log.value + "修改");
                db.write(log.index, log.old);
            }
        }
    }
}
