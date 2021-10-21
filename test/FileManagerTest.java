import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;

public class FileManagerTest {

    @Test
    public void save() {
        BlockManager.startAll();
        FileManager fileManager = new FileManager();
        CSEFile file1 = fileManager.newFile("name1");
        file1.write(new byte[]{1, 2, 3, 4, 5});
        FileManager.saveAll();
        BlockManager.saveAll();
    }

    @Test
    public void start() {
        Map<Integer, FileManager> fms = FileManager.startAll();
        System.out.println("FileManager.countIndex = " + FileManager.countFM);
        System.out.println();
    }

    @Test
    public void read() {
        BlockManager.startAll();
        Map<Integer, FileManager> fms = FileManager.startAll();
        for (FileManager fm : fms.values()) {
            CSEFile file1 = fm.getFile("name1");
            System.out.println("");
        }
    }
}