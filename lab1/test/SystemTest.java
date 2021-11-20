import org.testng.annotations.Test;

public class SystemTest {
    @Test
    public void init() {
        BlockManager.startAll();
        FileManager.startAll();
        new FileManager();
    }

    @Test
    public void noBM() {
        BlockManager.startAll();
        FileManager.startAll();
        FileManager fm = FileManager.fms.get(0);
        CSEFile f1 = fm.newFile("test1");
        if (f1 == null) {
            f1 = fm.getFile("test1");
        }
        f1.write(new byte[]{1, 2, 3, 4, 5});
    }

    @Test
    public void deepCopy() {
    }
}
