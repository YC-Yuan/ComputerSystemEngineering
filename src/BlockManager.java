import java.util.ArrayList;

public class BlockManager {
    ArrayList<Block> blocks = new ArrayList<>();

    Block getBlock(int index) {
        return blocks.get(index);
    }

    Block newBlock(byte[] b) {
        Block block = new Block(this, blocks.size(), 0);
        blocks.add(block);
        return block;
    }

    Block newEmptyBlock(int blockSize) {
        Block block = new Block(this, blocks.size(), 0);
        blocks.add(block);
        return block;
    }
}
