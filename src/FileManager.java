import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class FileManager {
    public static void main(String[] args) {
        FileManager fileManager = new FileManager();
        FileManager fileManager2 = new FileManager();
        fileManager.newFile("name1");
        fileManager.newFile("name2");
        fileManager.newFile("nam1");
        fileManager2.newFile("123");
        fileManager.save();
        fileManager2.save();
    }

    // FM自身信息
    static int countId = 0;
    static String rootPath = "./FMs/";
    private final String savePath;
    private final int id;

    public FileManager() {
        id = countId++;
        savePath = rootPath + id + "/";
    }

    // 持久化
    public void save() {
        // 创建文件夹
        File dir = new File(savePath);
        if (!dir.exists()) {
            boolean mkdir = dir.mkdirs();
            System.out.println("mkdir = " + mkdir);
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

    // 所有File manager公用的file池，在系统内部以int做index，对用户不可见
    static int countIndex = 0;// TODO 持久化复原时也需要复原
    static HashMap<Integer, CSEFile> files = new HashMap<>();
    // 每个FM独立的命名空间
    HashMap<String, CSEFile> fileNames = new HashMap<>();

    CSEFile getFile(String fileName) {
        if (fileNames.containsKey(fileName)) {
            return fileNames.get(fileName);
        } else {
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
        int fileId = countIndex++;
        if (files.containsKey(fileId)) {
            throw new ErrorCode(ErrorCode.FILE_ID_REPEATED);
        }
        CSEFile file = new CSEFile(this, fileName, fileId);
        // 理论上不应出现重复插入，递增索引保证了id不重复
        files.put(fileId, file);
        fileNames.put(fileName, file);
        return file;
    }
}
