import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BlockManager {
    // BMs公用
    private static final int DUPLICATION_NUM = 3;
    static Map<Integer, BlockManager> bms = new HashMap<>();

    // BM自身信息
    static int countBM = 0;
    private static final String rootPath = "./BMs/";
    private final String savePath;
    private final int id;

    // 所有BM公用的逻辑block池
    static int countLogicBlock = 0;
    static Map<Integer, Block> logicBlocks = new HashMap<>();

    // BM独立的物理blocks索引
    int countBlock = 0;
    Map<Integer, Block> blocks = new HashMap<>();

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


    public Block getBlock(int index) {
        return blocks.get(index);
    }

    public Block newBlock(byte[] b) {
        int blockId = countBlock++;
        Block block = new Block(this, blockId, b, b.length);
        blocks.put(blockId, block);
        return block;
    }

    public Block newEmptyBlock(int blockSize) {
        int blockId = countBlock++;
        Block block = new Block(this, blockId, new byte[blockSize], blockSize);
        blocks.put(blockId, block);
        return block;
    }

    public int getId() {
        return id;
    }
}
