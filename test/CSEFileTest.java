import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.*;

public class CSEFileTest {

    @Test
    public void testFile(){
        FileManager fm = new FileManager();
        new BlockManager();
        new BlockManager();
        new BlockManager();
        new BlockManager();
        fm.getFile("1");
    }

    @Test
    public void testSetSize() {
        FileManager fm = new FileManager();
        new BlockManager();
        new BlockManager();
        new BlockManager();
        new BlockManager();
        CSEFile file = fm.newFile("test");
        file.setSize(6);
        System.out.println(Arrays.toString(file.readAll()));
        file.write(new byte[]{1, 2, 3});
        System.out.println(Arrays.toString(file.readAll()));
        Tools.smartLs();
        file.setSize(3);
        System.out.println(Arrays.toString(file.readAll()));
    }
}