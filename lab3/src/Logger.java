import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Logger {

    public List<Log> logs = new CopyOnWriteArrayList<>();
    private String path;

    public Logger(String path) throws IOException {
        // 解析存储
        this.path = path;
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        while ((line = br.readLine()) != null) {
            logs.add(new Log(line));
        }
        // 数据载入
    }

    public void start(int tid) {
        log(new Log(tid, Log.START));
    }

    public void commit(int tid) {
        log(new Log(tid, Log.COMMIT));
    }

    public void abort(int tid) {
        log(new Log(tid, Log.ABORT));
    }

    public void write(int tid, int index, char old, char value) {
        log(new Log(tid, Log.WRITE, index, old, value));
    }

    public synchronized void save() {
        try {
            PrintWriter writer = new PrintWriter(path);
            for (Log log : logs) {
                writer.println(log.toString());
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(Log l) {
        System.out.println(l);
        logs.add(l);
    }

    // 获取最新tid
    public int getLastTid() {
        int tid = 0;
        for (Log log : logs) {
            tid = Math.max(log.tid, tid);
        }
        return tid;
    }

    // 根据tid获取数据
    public List<Log> getLogsByTid(int tid) {
        List<Log> list = new ArrayList<>();
        for (Log log : logs) {
            if (log.tid == tid) {
                list.add(log);
            }
        }
        return list;
    }
}
