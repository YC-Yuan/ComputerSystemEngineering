import com.sun.deploy.util.ArrayUtil;
import sun.awt.image.ImageWatched;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSEFile {
    public static final int MOVE_CURR = 0; //只是光标的三个枚举值，具体数值⽆实际意义
    public static final int MOVE_HEAD = 1;
    public static final int MOVE_TAIL = 2;

    // 光标位置 不应＜0 不应超过size
    protected int cursor = 0;
    protected final int id;
    protected final FileManager fm;
    public final String name;
    // 真正的blockIndexes,只在构造和close()中修改
    public List<Integer> blockIndexes = new ArrayList<>();
    public List<Integer> blockSizes = new ArrayList<>();

    public int getFileId() {
        return id;
    }

    public List<Integer> getLogicBlocks() {
        return blockIndexes;
    }

    public FileManager getFileManager() {
        return fm;
    }

    public String getFileName() {
        return name;
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

    public int getSize() {
        int size = 0;
        for (int i : blockSizes) {
            size += i;
        }
        return size;
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
        this.blockIndexes = new ArrayList<>(blockIndexes);
        for (Integer index : blockIndexes) {
            Block lb = BlockManager.getLogicBlock(index);
            if (lb != null) {
                blockSizes.add(lb.getSize());
            }
        }
    }

    // 更改光标位置
    public int move(int offset, int where) {
        int target = 0;
        int size = getSize();
        switch (where) {
            // 当前
            case 0:
                target = offset + cursor;
                break;
            // 头部
            case 1:
                break;
            // 尾部
            case 2:
                target = size;
                break;
            default:
                // 未定义方法 终止系统
                throw new ErrorCode(ErrorCode.UNDEFINED_CURSOR_MOVE_METHOD);
        }
        try {
            if (target < 0) {
                throw new ErrorCode(ErrorCode.CURSOR_LESS_THAN_ZERO);
            }
            if (target > size) {
                throw new ErrorCode(ErrorCode.CURSOR_GREATER_THAN_SIZE);
            }
        } catch (ErrorCode e) {
            System.out.println(e.getErrorText());
            target = Math.max(target, 0);
            target = Math.min(target, size);
        }
        cursor = target;
        return cursor;
    }

    // 从光标位置读取length
    public byte[] read(int length) {
        int size = getSize();
        try {
            if (length + cursor > size) {// 超了
                throw new ErrorCode(ErrorCode.CURSOR_GREATER_THAN_SIZE);
            }
        } catch (ErrorCode e) {
            System.out.println(e.getErrorText());
            length = size - cursor;// 改读余量
        }
        byte[] b = readAll();
        byte[] ans = Arrays.copyOfRange(b, cursor, cursor + length);
        move(length, MOVE_CURR);// 读取后移动光标
        return ans;
    }

    // 写入数据
    public boolean write(byte[] b) {
        int curBlock = checkCursorBlock();
        // 存储信息以在读取出错时纠正
        List<Integer> saveBI = new ArrayList<>(blockIndexes);
        List<Integer> saveBS = new ArrayList<>(blockSizes);
        boolean successFlag = false;
        try {
            if (curBlock == -1) {
                successFlag = writeNewBlocks(b, 0);
            } else {
                // 先读入指针所在block的数据
                Block lb = BlockManager.getLogicBlock(blockIndexes.get(curBlock));
                if (lb == null) {
                    throw new ErrorCode(ErrorCode.LOGIC_BLOCK_READ_FAILED);
                }
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
                successFlag = writeNewBlocks(newB, curBlock);
            }
        } catch (ErrorCode e) {
            System.out.println(e.getErrorText());
            blockIndexes = saveBI;
            blockSizes = saveBS;
        } finally {
            if (successFlag) {
                move(b.length, MOVE_CURR);// 写入成功后移动光标
            }
        }
        return successFlag;
    }

    public void setSize(int newSize) {
        int saveCursor = cursor;
        int size = getSize();
        if (newSize > size) {
            cursor = size;
            write(new byte[newSize - size]);// 自带纠错功能
            cursor = saveCursor;
        } else {
            List<Integer> saveBI = new ArrayList<>(blockIndexes);
            List<Integer> saveBS = new ArrayList<>(blockIndexes);
            try {
                // 缩小的情况
                cursor = newSize;
                int blockIndex = checkCursorBlock();
                int preBytesNum = getPreBytesNum();
                int saveNum = newSize - preBytesNum;
                // 保存最前块中不需要被删除的数据
                Block lb = BlockManager.getLogicBlock(blockIndexes.get(blockIndex));
                if (lb == null) {
                    throw new ErrorCode(ErrorCode.LOGIC_BLOCK_READ_FAILED);
                }
                byte[] lbData = lb.read();
                byte[] saveBytes = Arrays.copyOfRange(lbData, 0, saveNum);
                // 删除尾块链
                Util.removeListTail(blockIndexes, blockIndex);
                Util.removeListTail(blockSizes, blockIndex);
                // 重新写入保存数据
                cursor = newSize - saveNum;
                write(saveBytes);
                cursor = Math.min(saveCursor, newSize);
            } catch (ErrorCode e) {
                System.out.println(e.getErrorText());
                cursor = saveCursor;
                blockIndexes = saveBI;
                blockSizes = saveBS;
            }
        }
    }

    //使用buffer的同学需要实现
    public void close() {
        // 实现于BufferFile中
    }

    // 工具函数 单纯拓展block写入数据
    protected boolean writeNewBlocks(byte[] b, int index) {
        int blockSize = BlockManager.MAX_SIZE;
        int doneSize = 0;
        int needSize = b.length;
        // 存储信息用于logic block申请失败时
        List<Integer> saveBI = new ArrayList<>(blockIndexes);
        List<Integer> saveBS = new ArrayList<>(blockSizes);
        try {
            while (needSize > 0) {
                int newSize = Math.min(blockSize, needSize);
                // 这次申请curSize的block
                byte[] bToWrite = Arrays.copyOfRange(b, doneSize, doneSize + newSize);
                Block nb = BlockManager.newLogicBlock(bToWrite);
                if (nb == null) {// 申请失败了!给出反应
                    throw new ErrorCode(ErrorCode.FILE_WRITE_FAILED);
                }
                blockIndexes.add(index, nb.getLogicBlockId());
                blockSizes.add(index, nb.getSize());
                index++;
                doneSize += newSize;
                needSize -= newSize;
            }
            return true;
        } catch (ErrorCode e) {
            System.out.println(e.getErrorText());
            // 复原file的meta信息
            blockIndexes = saveBI;
            blockSizes = saveBS;
            return false;
        }
    }

    public byte[] readAll() {
        // 逐个读出
        byte[][] b = new byte[blockIndexes.size()][];
        for (int i = 0; i < blockIndexes.size(); i++) {
            Block lb = BlockManager.getLogicBlock(blockIndexes.get(i));
            if (lb != null) {
                b[i] = lb.read();
            }
        }
        // 合并返回
        return Util.concatArray(b);
    }

    // 根据当前光标找到被指中的block(的index而非id)
    protected int checkCursorBlock() {
        int size = getSize();
        if (blockIndexes.size() == 0) return -1;
        if (cursor == 0) return 0;
        if (cursor == size) return blockIndexes.size() - 1;
        int curIndex = -1, curCursor = 0;
        do {
            curIndex++;
            curCursor += blockSizes.get(curIndex);
        } while (curCursor <= cursor);
        return curIndex;
    }

    protected int getPreBytesNum() {
        int size = getSize();
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
