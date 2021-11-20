import org.testng.annotations.Test;

import java.util.Arrays;

public class ToolsTest {

    @Test
    public void smartCat() {
        FileManager fm = new FileManager();
        new FileManager();
        new BlockManager();
        new BlockManager();
        new BlockManager();
        CSEFile file = fm.newFile("test");
        file.write(new byte[]{1, 2, 3, 4, 5});
        System.out.println(Arrays.toString(Tools.smartCat(0)));
    }

    @Test
    public void smartHex() {
        FileManager fm = new FileManager();
        new FileManager();
        new BlockManager();
        new BlockManager();
        new BlockManager();
        BlockManager.newLogicBlock(new byte[]{123, -22, 5});
        Tools.smartHex(0);
    }

    @Test
    public void smartLs() {
        FileManager fm = new FileManager();
        new FileManager();
        new BlockManager();
        new BlockManager();
        new BlockManager();
        CSEFile f = fm.newFile("smart-ls test");
        f.write(new byte[]{1, 2, 3, 4, 5, 111});
        Tools.smartLs();
    }

    @Test
    public void smartCopy() {
        FileManager fm = new FileManager();
        new BlockManager();
        new BlockManager();
        new BlockManager();
        new BlockManager();
        CSEFile file = fm.newFile("test");
        file.write(new byte[]{1, 2, 3});
        CSEFile f2 = Tools.smartCopy(file.getFileId());
        assert f2 != null;
        f2.move(1, 0);
        f2.write(new byte[]{3, 2, 1});
        Tools.smartLs();
        System.out.println(Arrays.toString(Tools.smartCat(0)));
        System.out.println(Arrays.toString(Tools.smartCat(1)));
    }


    // smart-write:带有控制台输入的无法junit测试
    public static void main(String[] args) {
        FileManager fm = new FileManager();
        new FileManager();
        new BlockManager();
        new BlockManager();
        new BlockManager();
        CSEFile file = fm.newFile("test");
        file.write(new byte[]{1, 2, 3, 4, 5});
        Tools.smartWrite(1, CSEFile.MOVE_CURR, 0);
        System.out.println(Arrays.toString(file.readAll()));
    }
}