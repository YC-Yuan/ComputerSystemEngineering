import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        /*
         * initialize your file system here
         * for example, initialize FileManagers and BlockManagers
         * and offer all the required interfaces
         * */
        FileManager fileManager0 = new FileManager();
        new BlockManager();
        new BlockManager();
        new BlockManager();
        new BlockManager();
// test code
        CSEFile file = fileManager0.newFile("test1"); // id为1的⼀个file
        file.write("FileSystem".getBytes(StandardCharsets.UTF_8));
        System.out.println(Arrays.toString(file.readAll()));
        file.move(0, CSEFile.MOVE_HEAD);
        file.write("Smart".getBytes(StandardCharsets.UTF_8));
        System.out.println(Arrays.toString(file.readAll()));
        file.setSize(100);
        System.out.println(Arrays.toString(file.readAll()));
        file.setSize(16);
        System.out.println(Arrays.toString(file.readAll()));
        file.close();
        Tools.smartLs();

        //here we will destroy a block, and you should handle this exception

        CSEFile file1 = fileManager0.getFile("test1");
        System.out.println(Arrays.toString(file1.read(file.getSize())));
        Tools.smartLs();
        CSEFile file2 = Tools.smartCopy(0);
        assert file2 != null;
        System.out.println(Arrays.toString(file2.read(file.getSize())));
        Tools.smartHex(file2.getFileId());
        Tools.smartWrite(0, CSEFile.MOVE_HEAD, file2.getFileId());
        file2.close();
        Tools.smartLs();
    }
}
