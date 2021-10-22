import java.nio.charset.StandardCharsets;
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
                case "size":
                    fileSize();
                    break;
                case "fid":
                    fileId();
                case "write":
                    fileWrite();
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
                default:
                    System.out.println("input 'help' to check commands");
            }
        }
    }

    private static void smartCat() {
        int id = enterInd("fileId");
        if (id != -1) Tools.smartCat(id);
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
        StringBuilder sb = new StringBuilder();
        sb.append("--命令不区分大小写--\n");
        sb.append("help").append(":").append("指令集").append("\n");
        sb.append("cf").append(":").append("改变当前所在FM").append("\n");
        sb.append("new fm").append(":").append("新建FM").append("\n");
        sb.append("new bm").append(":").append("新建BM").append("\n");
        sb.append("smart-ls").append(":").append("数据系统完整信息").append("\n");
        sb.append("smart-cat").append(":").append("根据文件全局ID读取其全部内容").append("\n");
        sb.append("smart-copy").append(":").append("根据文件全局ID,在FM下复制一个一样的文件").append("\n");
        sb.append("smart-hex").append(":").append("根据LogicBlock ID用16进制输出数据内容").append("\n");
        sb.append("smart-write").append(":").append("给出指针位置和文件全局ID,写入数据").append("\n");
        sb.append("new file").append(":").append("当前FM下新建文件").append("\n");
        sb.append("ls").append(":").append("列出当前FM下文件").append("\n");
        sb.append("ls fm").append(":").append("列出所有FM").append("\n");
        sb.append("ls bm").append(":").append("列出所有BM").append("\n");
        sb.append("open").append(":").append("打开当前FM下的文件").append("\n");
        sb.append("fid").append(":").append("查看打开文件的全局ID").append("\n");
        sb.append("pos").append(":").append("查看打开文件的指针位置").append("\n");
        sb.append("size").append(":").append("查看打开文件的大小").append("\n");
        sb.append("write").append(":").append("向打开文件的指针位置写入").append("\n");
        sb.append("set size").append(":").append("更改打开文件的大小").append("\n");
        sb.append("close").append(":").append("关闭文件(buffer方式打开必须关闭,否则修改将无效)").append("\n");
        sb.append("save").append(":").append("系统数据持久化为文件").append("\n");
        sb.append("quit").append(":").append("安全退出").append("\n");
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
            String yon = "";
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

    private static void pos() {
        if (file == null) {
            System.out.println("No file opened, cannot pos");
        } else {
            System.out.println(file.getFileName() + ": cursor at " + file.pos() + " in " + file.getSize());
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
