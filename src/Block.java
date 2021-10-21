import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// 存储block meta信息, 总是根据meta中的路径寻找block data
public class Block {
    private final BlockManager bm;
    private final int id;
    private final int logicBlockId;
    private final int size;
    private final byte[] data;


    Block(BlockManager bm, int id, int logicBlockId, byte[] data, int size) {
        this.bm = bm;
        this.id = id;
        this.logicBlockId = logicBlockId;
        this.size = size;
        this.data = data;
    }


    // block meta信息: id+size+block数据md5
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append("\n");
        sb.append(logicBlockId).append("\n");
        sb.append(size).append("\n");
        String dataMd5Code = Util.toMd5(data);
        sb.append(dataMd5Code).append("\n");
        return sb.toString();
    }

    int getId() {
        return id;
    }

    int getLogicBlockId() {
        return logicBlockId;
    }


    BlockManager getBlockManager() {
        return bm;
    }


    byte[] read() {
        return data;
    }


    int getSize() {
        return size;
    }
}
