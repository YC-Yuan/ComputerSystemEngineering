import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DBWriter {
    private final RandomAccessFile db;

    public DBWriter(String path) throws FileNotFoundException {
        db = new RandomAccessFile(new File(path), "rw");
    }

    public synchronized char read(int index) throws IOException {
        db.seek(index * 2L);
        return (char) db.read();
    }

    public synchronized void write(int index, char ch) throws IOException {
        db.seek(index * 2L);
        db.write((ch + "\n").getBytes());
    }
}
