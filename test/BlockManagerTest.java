import org.junit.Test;

import static org.junit.Assert.*;

public class BlockManagerTest {

    @Test
    public void saveAll() {
        BlockManager bm1 = new BlockManager();
        BlockManager bm2 = new BlockManager();
        BlockManager bm3 = new BlockManager();
        bm1.newLogicBlock(new byte[1]);
        bm1.newLogicBlock(new byte[1]);
        bm2.newLogicBlock(new byte[1]);
        BlockManager.saveAll();
    }

    @Test
    public void startAll() {
    }
}