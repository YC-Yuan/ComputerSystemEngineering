import java.io.*;
import java.util.*;

public class FileManager {
    // FMs公用
    static Map<Integer, FileManager> fms = new HashMap<>();

    // FM自身信息
    static int countFM = 0;
    private static final String rootPath = "./FMs/";
    private final int id;

    public int getId() {
        return id;
    }

    // 所有File manager公用的file池，在系统内部以int做index，对用户不可见
    static int countFile = 0;
    static Map<Integer, CSEFile> files = new HashMap<>();
    // 每个FM独立的命名空间
    Map<String, CSEFile> fileNames = new HashMap<>();

    public static boolean existFm() {
        return fms.size() > 0;
    }

    public FileManager() {
        id = countFM++;
        fms.put(id, this);
    }

    private FileManager(int id) {
        this.id = id;
        countFM = Math.max(countFM, id + 1);
        fms.put(id, this);
    }


    public static CSEFile getFile(int fileId) {
        if (files.containsKey(fileId)) {
            return files.get(fileId);
        }
        {
            try {
                throw new ErrorCode(ErrorCode.FILE_NOT_FOUND);
            } catch (ErrorCode e) {
                e.printStackTrace();
                System.out.println(e.getErrorText());
                return null;
            }
        }
    }

    public static void saveAll() {
        for (FileManager fm : fms.values()) {
            fm.save();
        }
    }

    // 持久化：创建FM对应文件夹和file meta
    public void save() {
        // 创建文件夹
        File dir = new File(getFmPath());
        if (!dir.exists()) {
            boolean mkdir = dir.mkdirs();
        }
        // 对File逐个创建.meta, 重启时读取
        for (CSEFile f : fileNames.values()) {
            String fPath = getFmPath() + f.name + ".meta";
            try {
                FileWriter fw;
                fw = new FileWriter(fPath);
                fw.write(f.toString());
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 持久化：从对应目录读取文件夹结构和file meta信息，恢复static变量和各个对象
    public static Map<Integer, FileManager> startAll() {
        init();
        loadFileManagers();
        return fms;
    }


    CSEFile getFile(String fileName) {
        if (fileNames.containsKey(fileName)) {
            return fileNames.get(fileName);
        } else {
            // 无此文件
            try {
                throw new ErrorCode(ErrorCode.FILE_NOT_FOUND);
            } catch (ErrorCode e) {
                e.printStackTrace();
                System.out.println(e.getErrorText());
                return null;
            }
        }
    }

    public CSEFile newFile(String fileName) {
        if (fileNames.containsKey(fileName)) {
            // 不能重复创建
            try {
                throw new ErrorCode(ErrorCode.FILE_NAME_REPEATED);
            } catch (ErrorCode e) {
                e.printStackTrace();
                System.out.println(e.getErrorText());
                return null;
            }
        }
        // 添加到file池和FM命名空间
        int fileId = countFile++;
        if (files.containsKey(fileId)) {
            try {
                throw new ErrorCode(ErrorCode.FILE_ID_REPEATED);
            } catch (ErrorCode e) {
                e.printStackTrace();
                System.out.println(e.getErrorText());
                countFile--;// 复原参数
                return null;
            }
        }
        CSEFile file = new CSEFile(this, fileName, fileId);
        indexFile(file);
        return file;
    }

    public CSEFile copyFile(CSEFile file) {
        int fileId = countFile++;
        String copyName = "copy-" + file.getFileName();
        while (fileNames.containsKey(copyName)) {
            copyName = "c" + copyName;
        }
        CSEFile cf = new CSEFile(this, copyName, fileId, file.getLogicBlocks());
        indexFile(cf);
        return cf;
    }

    // 在内部索引File对象
    private void indexFile(CSEFile file) {
        countFile = Math.max(countFile, file.getFileId() + 1);
        files.put(file.getFileId(), file);
        fileNames.put(file.getFileName(), file);
    }

    private String getFmPath() {
        return rootPath + id + "/";
    }

    private static void init() {
        fms = new HashMap<>();
        countFile = 0;
        countFM = 0;
        files = new HashMap<>();
    }

    private static void loadFileManagers() {
        File file = new File(rootPath);
        if (file.exists()) {
            File[] fl = file.listFiles();
            // 每个文件夹对应一个FM，名字是FM id
            assert fl != null;
            for (File f : fl) {
                int fmId = Integer.parseInt(f.getName());
                FileManager fm = new FileManager(fmId);
                // 每个文件夹再读入file mate信息
                File[] fileMetas = f.listFiles();
                assert fileMetas != null;
                for (File meta : fileMetas) {
                    try {
                        Scanner sc;
                        sc = new Scanner(new FileReader(meta));
                        String filename = sc.nextLine();
                        int fileId = sc.nextInt();
                        List<Integer> lbIndexes = new ArrayList<>();
                        while (sc.hasNextInt()) {
                            lbIndexes.add(sc.nextInt());
                        }
                        // 创建文件对象 添加索引
                        countFile = Math.max(countFile, fileId + 1);
                        fm.indexFile(new CSEFile(fm, filename, fileId, lbIndexes));
                        sc.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
