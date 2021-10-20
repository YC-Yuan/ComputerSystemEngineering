import java.io.*;
import java.util.*;

public class FileManager {
    // FMs公用
    static Map<Integer,FileManager> fms = new HashMap<>();

    // FM自身信息
    static int countFM = 0;
    private static final String rootPath = "./FMs/";
    private final String savePath;
    private final int id;

    // 所有File manager公用的file池，在系统内部以int做index，对用户不可见
    static int countFile = 0;
    static Map<Integer,CSEFile> files = new HashMap<>();
    // 每个FM独立的命名空间
    Map<String,CSEFile> fileNames = new HashMap<>();

    public FileManager() {
        id = countFM++;
        savePath = rootPath + id + "/";
        fms.put(id,this);
    }

    public FileManager(int id) {
        this.id = id;
        countFM = Math.max(countFM,id);
        savePath = rootPath + id + "/";
        if (!fms.containsKey(id)) {
            fms.put(id,this);
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
        File dir = new File(savePath);
        if (!dir.exists()) {
            boolean mkdir = dir.mkdirs();
        }
        // 对File逐个创建.meta, 重启时读取
        for (CSEFile f : fileNames.values()) {
            String fPath = savePath + f.name + ".meta";
            FileWriter fw;
            try {
                fw = new FileWriter(fPath);
                fw.write(f.toString());
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 持久化：从对应目录读取文件夹结构和file meta信息，恢复static变量和各个对象
    public static Map<Integer,FileManager> startAll() {
        File file = new File(rootPath);
        File[] fl = file.listFiles();
        // 如果FM不存在 则读取失败
        if (fl == null) {
            throw new ErrorCode(ErrorCode.START_LACK_FM);
        }
        // 每个文件夹对应一个FM，名字是FM id
        for (File f : fl) {
            int fmId = Integer.parseInt(f.getName());
            FileManager fm = new FileManager(fmId);
            // 每个文件夹再读入file mate信息
            File[] fileMetas = f.listFiles();
            assert fileMetas != null;
            for (File meta : fileMetas) {
                Scanner sc;
                try {
                    sc = new Scanner(new FileReader(meta));
                    String filename = sc.nextLine();
                    int fileId = sc.nextInt();
                    List<Integer> lbIndexes = new ArrayList<>();
                    while (sc.hasNextInt()) {
                        lbIndexes.add(sc.nextInt());
                    }
                    // 创建文件对象 添加索引
                    countFile = Math.max(countFile,fileId);
                    fm.loadFile(new CSEFile(fm,filename,fileId,lbIndexes));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return fms;
    }


    CSEFile getFile(String fileName) {
        if (fileNames.containsKey(fileName)) {
            return fileNames.get(fileName);
        }
        else {
            // 无此文件
            throw new ErrorCode(ErrorCode.FILE_NOT_FOUND);
        }
    }

    CSEFile newFile(String fileName) {
        if (fileNames.containsKey(fileName)) {
            // 不能重复创建
            throw new ErrorCode(ErrorCode.FILE_NAME_REPEATED);
        }
        // 添加到file池和FM命名空间
        int fileId = countFile++;
        if (files.containsKey(fileId)) {
            throw new ErrorCode(ErrorCode.FILE_ID_REPEATED);
        }
        CSEFile file = new CSEFile(this,fileName,fileId);
        // 理论上不应出现重复插入，递增索引保证了id不重复
        loadFile(file);
        return file;
    }

    // 在内部索引File对象
    private void loadFile(CSEFile file) {
        files.put(file.getFileId(),file);
        fileNames.put(file.getFileName(),file);
    }
}
