public class File {
    static final int MOVE_CURR = 0; //只是光标的三个枚举值，具体数值⽆实际意义
    static final int MOVE_HEAD = 1;
    static final int MOVE_TAIL = 2;

    private int size = 0;
    private final int id;
    private final FileManager fm;

    File(FileManager fm, int fileId) {
        this.fm = fm;
        id = fileId;
    }

    int getFileId() {
        return id;
    }

    FileManager getFileManager() {
        return fm;
    }

    byte[] read(int length) {
        return null;
    }

    void write(byte[] b) {

    }

    int pos() {
        return move(0, MOVE_CURR);
    }

    int move(int offset, int where) {
        return 0;
    }

    int getSize() {
        return size;
    }

    void setSize(int newSize) {
        if (newSize > size) {
            // TODO: 扩充空间并用0填满
        } else {
            // TODO: 删减空间并将被删除部分用0填满
        }
    }

    //使⽤buffer的同学需要实现
    void close() {

    }
}
