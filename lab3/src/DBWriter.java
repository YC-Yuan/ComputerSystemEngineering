import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DBWriter {
    private final RandomAccessFile db;

    public DBWriter(File file) throws FileNotFoundException {
        db = new RandomAccessFile(new File("./db.txt"), "rw");
    }

    public char read(int index) throws IOException {
        db.seek(index * 2L);
        return (char) db.read();
    }

    public void dbWrite(int index, char ch) throws IOException {
        db.seek(index * 2L);
        db.write((ch + "\n").getBytes());
    }
}
