import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BufferFile extends CSEFile {
    private List<Byte> data = new ArrayList<>();
    private CSEFile of;

    BufferFile(CSEFile file) {
        super(file.getFileManager(), file.getFileName(), file.getFileId(), file.getLogicBlocks());
        of = file;
        cursor = file.cursor;
        byte[] bytes = file.readAll();
        for (Byte b : bytes) {
            data.add(b);
        }
    }

    public int getSize() {
        return data.size();
    }

    // read 无需修改

    public boolean write(byte[] b) {
        // 倒序逐个插入
        int size = b.length;
        for (int i = 0; i < size; i++) {
            byte b1 = b[size - 1 - i];
            data.add(cursor, b1);
        }
        cursor += size;
        return true;
    }

    public void setSize(int newSize) {
        if (newSize > getSize()) {
            while (getSize() < newSize) {
                data.add((byte) 0);
            }
        } else {
            Util.removeListTail(data, newSize);
        }
    }

    public void close() {
        // 将buffer中的所有东西写回
        of.move(0, CSEFile.MOVE_HEAD);
        boolean writeFlag = of.write(getByteData());
        if (writeFlag) {
            of.setSize(data.size());
        }
    }

    public byte[] readAll() {
        return getByteData();
    }

    private byte[] getByteData() {
        int size = getSize();
        byte[] bs = new byte[size];
        for (int i = 0; i < size; i++) {
            bs[i] = data.get(i);
        }
        return bs;
    }
}
