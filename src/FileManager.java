import java.util.HashMap;

public class FileManager {
    HashMap<Integer, File> files = new HashMap<>();

    File getFile(int fileId) {
        if (files.containsKey(fileId)) {
            return files.get(fileId);
        } else {
            // 无此文件
            throw new ErrorCode(ErrorCode.FILE_NOT_FOUND);
        }
    }

    File newFile(int fileId) {
        if (files.containsKey(fileId)) {
            // 不能重复创建
            throw new ErrorCode(ErrorCode.FILE_CREATION_REPEATED);
        } else {
            files.put(fileId, new File(this, fileId));
        }
        return null;
    }
}
