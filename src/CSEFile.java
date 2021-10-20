import java.util.ArrayList;
import java.util.List;

public class CSEFile {
    static final int MOVE_CURR = 0; //只是光标的三个枚举值，具体数值无实际意义
    static final int MOVE_HEAD = 1;
    static final int MOVE_TAIL = 2;

    private int size = 0;
    private final int id;
    private final FileManager fm;
    public final String name;
    public List<Integer> blockIndexes = new ArrayList<>();

    CSEFile(FileManager fm,String filename,int fileId) {
        this.fm = fm;
        id = fileId;
        name = filename;
    }

    CSEFile(FileManager fm,String filename,int fileId,List<Integer> blockIndexes) {
        this.fm = fm;
        id = fileId;
        name = filename;
        this.blockIndexes = blockIndexes;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n");
        sb.append(id).append("\n");
        for (int i : blockIndexes) {
            sb.append(i).append(" ");
        }
        sb.append("\n");
        return sb.toString();
    }

    int getFileId() {
        return id;
    }

    String getFileName() {return name;}

    FileManager getFileManager() {
        return fm;
    }

    byte[] read(int length) {
        return null;
    }

    void write(byte[] b) {

    }

    int pos() {
        return move(0,MOVE_CURR);
    }

    int move(int offset,int where) {
        return 0;
    }

    int getSize() {
        return size;
    }

    void setSize(int newSize) {
        if (newSize > size) {
            // TODO: 扩充空间并用0填满
        }
        else {
            // TODO: 删减空间并将被删除部分用0填满
        }
    }

    //使⽤buffer的同学需要实现
    void close() {

    }
}
