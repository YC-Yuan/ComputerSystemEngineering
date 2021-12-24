import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Logger {
    private final PrintWriter log;

    public Logger(File file) throws IOException {
        // 文件挂载
        log = new PrintWriter(new FileWriter(file));
        // 数据载入
    }

    public void log(int tid, int index, char value) {
        log(tid + " " + index + " " + value);
    }

    public void commit(int tid){
        log(tid+" "+"committed");
    }

    public void log(String text) {

    }
}
