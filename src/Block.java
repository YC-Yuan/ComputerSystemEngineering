import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// 存储block meta信息, 总是根据meta中的路径寻找block data
public class Block {
    private final BlockManager bm;
    private final int size;
    private final int id;
    private final byte[] data;

    Block(BlockManager bm, int id, byte[] data, int size) {
        this.bm = bm;
        this.id = id;
        this.data = data;
        this.size = size;
    }

    // block meta信息: id+size+block数据md5
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append("\n");
        sb.append(size).append("\n");
        String dataMd5Code = "";
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            md5.update(data);
            dataMd5Code = new BigInteger(1, md5.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        sb.append(dataMd5Code).append("\n");
        return sb.toString();
    }

    int getId() {
        return id;
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
