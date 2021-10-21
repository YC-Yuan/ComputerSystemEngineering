import com.sun.deploy.util.ArrayUtil;
import sun.awt.image.ImageWatched;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSEFile {
    // 光标位置 不应＜0 不应超过size
    private int cursor = 0;
    private int size = 0;
    private final int id;
    private final FileManager fm;
    public final String name;
    public List<Integer> blockIndexes = new ArrayList<>();
    public List<Integer> blockSizes = new ArrayList<>();

    public int getFileId() {
        return id;
    }

    public FileManager getFileManager() {
        return fm;
    }

    public String getFileName() {
        return name;
    }

    CSEFile(FileManager fm, String filename, int fileId) {
        this.fm = fm;
        id = fileId;
        name = filename;
    }

    CSEFile(FileManager fm, String filename, int fileId, List<Integer> blockIndexes) {
        this.fm = fm;
        id = fileId;
        name = filename;
        this.blockIndexes = blockIndexes;
        size = 0;
        for (Integer index : blockIndexes) {
            Block lb = BlockManager.getLogicBlock(index);
            size += lb.getSize();
            blockSizes.add(lb.getSize());
        }
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


    // 返回光标位置
    public int pos() {
        return cursor;
    }

    // 更改光标位置
    public int move(int offset, int where) {
        int target = offset + where;
        try {
            if (target < 0) {
                throw new ErrorCode(ErrorCode.CURSOR_LESS_THAN_ZERO);
            }
            if (target >= size) {
                throw new ErrorCode(ErrorCode.CURSOR_GREATER_THAN_SIZE);
            }
        } catch (ErrorCode e) {
            System.out.println(e.getErrorText());
            target = Math.max(target, 0);
            target = Math.min(target, size - 1);
        }
        cursor = target;
        return cursor;
    }

    // 从光标位置读取length
    byte[] read(int length) {
        return null;
    }

    // 写入数据
    void write(byte[] b) {
        int curBlock = checkCursorBlock();
        if (curBlock == -1) {
            writeNewBlocks(b, 0);
        } else {
            // 先读入指针所在block的数据
            Block lb = BlockManager.getLogicBlock(curBlock);
            byte[] originalB = lb.read();
            // 在内存中改正内容
            int preCursor = getPreBytesNum();
            int preIndex = cursor - preCursor;
            byte[] preB = Arrays.copyOfRange(originalB, 0, preIndex);
            byte[] nextB = Arrays.copyOfRange(originalB, preIndex, originalB.length);
            byte[] newB = Util.concatArray(preB, b, nextB);
            // 删除原本的block
            blockIndexes.remove(curBlock);
            blockSizes.remove(curBlock);
            // 将新数据插入block位置
            writeNewBlocks(newB, curBlock);
        }
    }

    // 工具函数 单纯拓展block写入数据
    private void writeNewBlocks(byte[] b, int index) {
        int blockSize = BlockManager.MAX_SIZE;
        int doneSize = 0;
        int needSize = b.length;
        while (needSize > 0) {
            int newSize = Math.min(blockSize, needSize);
            // 这次申请curSize的block
            byte[] bToWrite = Arrays.copyOfRange(b, doneSize, newSize);
            Block nb = BlockManager.newLogicBlock(bToWrite);
            blockIndexes.add(index, nb.getLogicBlockId());
            blockSizes.add(index, nb.getSize());
            index++;
            doneSize += newSize;
            needSize -= newSize;
        }
        // 更新size
        size += b.length;
    }


    int getSize() {
        return size;
    }

    // TODO
    void setSize(int newSize) {
        if (newSize > size) {
            // TODO: 扩充空间并用0填满
        } else {
            // TODO: 删减空间并将被删除部分用0填满
        }
    }

    //使用buffer的同学需要实现
    void close() {

    }

    // 根据当前光标找到被指中的block
    private int checkCursorBlock() {
        if (size == 0) return -1;
        int curCursor = 0, curIndex = 0;
        while (curCursor < cursor) {
            curCursor += blockSizes.get(curIndex);
            curIndex++;
        }
        return curIndex;
    }

    private int getPreBytesNum() {
        if (size == 0) return 0;
        int curCursor = 0, ans = 0, curIndex = 0;
        while (curCursor < cursor) {
            ans = curCursor;
            curCursor += blockSizes.get(curIndex);
            curIndex++;
        }
        return ans;
    }
}
