import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BlockManager {
    // BMs公用
    static Map<Integer,BlockManager> bms = new HashMap<>();

    // BM自身信息
    static int countBM = 0;
    private static final String rootPath = "./BMs/";
    private final String savePath;
    private final int id;

    // 所有BM公用的逻辑block池

    public BlockManager() {
        id = countBM++;
        savePath = rootPath + id + "/";
        // 自动添加到管理池
        BlockPool.addBlockManager(this);
    }

    // 用于存储meta的信息
    public void saveAll() {

    }

    // 持久化存储fm
    public void save() {

    }

    ArrayList<Block> blocks = new ArrayList<>();

    public Block getBlock(int index) {
        return blocks.get(index);
    }

    public Block newBlock(byte[] b) {
        Block block = new Block(this,blocks.size(),b,b.length);
        blocks.add(block);
        return block;
    }

    public Block newEmptyBlock(int blockSize) {
        Block block = new Block(this,blocks.size(),new byte[blockSize],blockSize);
        blocks.add(block);
        return block;
    }

    public int getId() {
        return id;
    }
}
