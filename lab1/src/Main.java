import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static FileManager fm = null;
    static CSEFile file = null;

    private static void start() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Reload from data or Start new system?(R:S)");
            String input = sc.nextLine();
            if (input.equalsIgnoreCase("r")) {
                BlockManager.startAll();
                FileManager.startAll();
                break;
            } else if (input.equalsIgnoreCase("s")) {
                BlockManager.startAll();
                break;
            } else {
                System.out.println("Unresolved input");
            }
        }
    }

    public static void main(String[] args) {
        // 启动阶段 需要选择是否从持久化记录中恢复
        start();
        // 启动后 如果有足够FM 选择FM,如果没有足够FM, 要求创建FM
        chooseFm();
        System.out.println("Current FM:" + fm.getId());
        while (true) {
            String input = sc.nextLine();
            switch (input.toLowerCase()) {
                // 全局类
                case "help":
                    help();
                    break;
                case "cf":
                    chooseFm();
                    break;
                case "new fm":
                    newFm();
                    break;
                case "new bm":
                    newBm();
                    break;
                case "ls fm":
                    listFm();
                    break;
                case "ls bm":
                    listBm();
                    break;
                // smart工具
                case "smart-cat":
                    smartCat();
                    break;
                case "smart-copy":
                    smartCopy();
                    break;
                case "smart-hex":
                    smartHex();
                    break;
                case "smart-write":
                    smartWrite();
                    break;
                case "smart-ls":
                    Tools.smartLs();
                    break;
                // 针对当前FM类
                case "ls":
                    listFiles();
                    break;
                case "open":
                    open();
                    break;
                case "new file":
                    newFile();
                    break;
                // 针对当前文件类
                case "pos":
                    pos();
                    break;
                case "move":
                    move();
                    break;
                case "size":
                    fileSize();
                    break;
                case "fid":
                    fileId();
                    break;
                case "write":
                    fileWrite();
                    break;
                case "read":
                    read();
                    break;
                case "read all":
                    readAll();
                    break;
                case "set size":
                    fileSetSize();
                    break;
                case "close":
                    close();
                    break;
                case "save":
                    perseverance();
                    break;
                case "quit":
                    safeQuit();
                    break;
                default:
                    System.out.println("input 'help' to check commands");
            }
        }
    }

    private static void smartCat() {
        int id = enterInd("fileId");
        if (id != -1) {
            System.out.println(Arrays.toString(Tools.smartCat(id)));
        }
    }

    private static void smartCopy() {
        int id = enterInd("fileId");
        if (id != -1) Tools.smartCopy(id);
    }

    private static void smartWrite() {
        int offset = enterInd("offset");
        int where = -2;
        while (where != -1 && where != 0 && where != 1 && where != 2) {
            where = enterInd("method, 0 for cursor, 1 for head, 2 for tail");
        }
        int id = enterInd("fileId");
        if (id != -1) Tools.smartWrite(offset, where, id);
    }

    private static void smartHex() {
        int id = enterInd("logic block id");
        if (id != -1) Tools.smartHex(id);
    }

    private static void help() {
        String sb = "--命令不区分大小写--\n" +
                "help" + ":" + "指令集" + "\n" +
                "cf" + ":" + "改变当前所在FM" + "\n" +
                "new fm" + ":" + "新建FM" + "\n" +
                "new bm" + ":" + "新建BM" + "\n" +
                "smart-ls" + ":" + "数据系统完整信息" + "\n" +
                "smart-cat" + ":" + "根据文件全局ID读取其全部内容" + "\n" +
                "smart-copy" + ":" + "根据文件全局ID,在FM下复制一个一样的文件" + "\n" +
                "smart-hex" + ":" + "根据LogicBlock ID用16进制输出数据内容" + "\n" +
                "smart-write" + ":" + "给出指针位置和文件全局ID,写入数据" + "\n" +
                "new file" + ":" + "当前FM下新建文件" + "\n" +
                "ls" + ":" + "列出当前FM下文件" + "\n" +
                "ls fm" + ":" + "列出所有FM" + "\n" +
                "ls bm" + ":" + "列出所有BM" + "\n" +
                "open" + ":" + "打开当前FM下的文件" + "\n" +
                "fid" + ":" + "查看打开文件的全局ID" + "\n" +
                "pos" + ":" + "查看打开文件的指针位置" + "\n" +
                "size" + ":" + "查看打开文件的大小" + "\n" +
                "write" + ":" + "向打开文件的指针位置写入" + "\n" +
                "read " + "查看打开文件从光标起一定长度的内容，以UTF-8字符串输出\n" +
                "read all" + "查看打开文件的所有内容\n" +
                "set size" + ":" + "更改打开文件的大小" + "\n" +
                "close" + ":" + "关闭文件(buffer方式打开必须关闭,否则修改将无效)" + "\n" +
                "save" + ":" + "系统数据持久化为文件" + "\n" +
                "quit" + ":" + "安全退出" + "\n";
        System.out.println(sb);
    }

    private static void chooseFm() {
        if (FileManager.existFm()) {
            boolean chosen = false;
            while (!chosen) {// 有fm 选一个
                listFm();
                System.out.println("Input id of FM to choose a current FM");
                String input = sc.nextLine();
                try {
                    int fmId = Integer.parseInt(input);
                    if (FileManager.fms.containsKey(fmId)) {
                        fm = FileManager.fms.get(fmId);
                        chosen = true;
                    } else {
                        throw new Exception();
                    }
                } catch (Exception ignored) {
                    System.out.println("Please input an id of FM");
                }
            }
        } else {// 没fm 自动创建并设置
            System.out.println("No available FM in current system, auto creating!");
            fm = new FileManager();
        }
    }

    private static void newFm() {
        new FileManager();
        listFm();
    }

    private static void newBm() {
        new BlockManager();
        listBm();
    }

    private static void newFile() {
        System.out.println("Enter file name");
        String input = sc.nextLine();
        if (fm.fileNames.containsKey(input)) {
            System.out.println("File already exist in this FM, change FM or change file name than try again");
        } else {
            fm.newFile(input);
        }
        listFiles();
    }

    private static void listFiles() {
        for (String filename : fm.fileNames.keySet()) {
            System.out.print(filename + " ");
            System.out.println();
        }
    }

    private static void listFm() {
        System.out.print("FM:");
        for (int id : FileManager.fms.keySet()) {
            System.out.print(id + " ");
        }
        System.out.println();
    }

    private static void listBm() {
        System.out.print("BM:");
        for (int id : BlockManager.bms.keySet()) {
            System.out.print(id + " ");
        }
        System.out.println();
    }

    private static void open() {
        System.out.println("Enter file name");
        String input = sc.nextLine();
        if (fm.fileNames.containsKey(input)) {
            String yon;
            do {
                System.out.println("Open it with buffer or not?(Y:N)");
                yon = sc.nextLine();
            } while (!yon.equalsIgnoreCase("y") && !yon.equalsIgnoreCase("n"));
            if (yon.equalsIgnoreCase("y")) {// buffer 打开
                file = new BufferFile(fm.fileNames.get(input));
            } else {// 普通打开
                file = fm.fileNames.get(input);
            }
        } else {
            System.out.println("No such file!");
            listFiles();
        }
    }

    private static void close() {
        if (file == null) {
            System.out.println("No file opened, cannot close");
        } else {
            file.close();
            file = null;
        }
    }

    private static void fileSize() {
        if (file == null) {
            System.out.println("No file opened, cannot check file size");
        } else {
            System.out.println(file.getFileName() + ":" + file.getSize() + "bytes");
        }
    }

    private static void fileWrite() {
        if (file == null) {
            System.out.println("No file opened, cannot check file size");
        } else {
            String input = sc.nextLine();
            file.write(input.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static void fileId() {
        if (file == null) {
            System.out.println("No file opened, cannot check file size");
        } else {
            System.out.println("fileId:" + file.getFileId());
        }
    }

    private static void fileSetSize() {
        if (file == null) {
            System.out.println("No file opened, cannot check file size");
        } else {
            int size = enterInd("new size");
            if (size > -1) {
                file.setSize(size);
            }
        }
    }

    private static void read() {
        if (file == null) {
            System.out.println("No file opened, cannot check file size");
        } else {
            int length = enterInd("length to read");
            byte[] read = file.read(length);
            String s = new String(read);
            System.out.println(s);
        }
    }

    private static void readAll() {
        if (file == null) {
            System.out.println("No file opened, cannot check file size");
        } else {
            System.out.println(new String(file.readAll()));
        }
    }

    private static void pos() {
        if (file == null) {
            System.out.println("No file opened, cannot pos");
        } else {
            System.out.println(file.getFileName() + ": cursor at " + file.pos() + " in " + file.getSize());
        }
    }

    private static void move() {
        if (file == null) {
            System.out.println("No file opened, cannot pos");
        } else {
            int offset = enterInd("offset");
            int where = -2;
            while (where != -1 && where != 0 && where != 1 && where != 2) {
                where = enterInd("method, 0 for cursor, 1 for head, 2 for tail");
            }
            file.move(offset, where);
        }
    }

    private static void perseverance() {
        FileManager.saveAll();
        BlockManager.saveAll();
    }

    private static void safeQuit() {
        System.out.println("Files haven't saved, quit or save and quit?(Q:S)");
        while (true) {
            String input = sc.nextLine();
            if (input.equalsIgnoreCase("Q")) {
                System.exit(0);
                break;
            } else if (input.equalsIgnoreCase("S")) {
                FileManager.saveAll();
                BlockManager.saveAll();
                System.exit(0);
                break;
            } else {
                System.out.println("Unresolved input");
            }
        }
    }

    private static int enterInd(String target) {
        // 整个数组输入
        String input;
        Integer size = null;
        do {
            System.out.println("Enter " + target + "(-1 for quit)");
            input = sc.nextLine();
            try {
                size = Integer.parseInt(input);
            } catch (NumberFormatException ignored) {
            }
        } while (size == null || size < -1);
        return size;
    }
}
