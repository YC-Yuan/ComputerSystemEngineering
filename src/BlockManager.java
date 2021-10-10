import java.util.ArrayList;

public class BlockManager {
    ArrayList<Block> blocks = new ArrayList<>();

    public Block getBlock(int index) {
        return blocks.get(index);
    }

    public Block newBlock(byte[] b) {
        Block block = new Block(this, blocks.size(), b, b.length);
        blocks.add(block);
        return block;
    }

    public Block newEmptyBlock(int blockSize) {
        Block block = new Block(this, blocks.size(), new byte[blockSize], blockSize);
        blocks.add(block);
        return block;
    }
}
