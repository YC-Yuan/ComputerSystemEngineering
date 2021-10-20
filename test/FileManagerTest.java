import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class FileManagerTest {

    @Test
    public void save() {
        FileManager fileManager = new FileManager();
        FileManager fileManager2 = new FileManager();
        fileManager.newFile("name1");
        fileManager.newFile("name2");
        fileManager.newFile("nam1");
        fileManager2.newFile("123");
        fileManager.save();
        fileManager2.save();
    }

    @Test
    public void start() {
        Map<Integer,FileManager> fms = FileManager.startAll();
        System.out.println("FileManager.countIndex = " + FileManager.countFM);
        System.out.println();
    }
}