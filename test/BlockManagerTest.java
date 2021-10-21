import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BlockManagerTest {

    @Test
    public void saveAll() {
        BlockManager bm1 = new BlockManager();
        BlockManager bm2 = new BlockManager();
        BlockManager bm3 = new BlockManager();
        BlockManager bm4 = new BlockManager();
        BlockManager bm5 = new BlockManager();
        BlockManager.newLogicBlock(new byte[]{1});
        BlockManager.newEmptyLogicBlock(2);
        BlockManager.newEmptyLogicBlock(3);
        BlockManager.newEmptyLogicBlock(4);
        BlockManager.newLogicBlock(new byte[]{1, 2, 3, 4, 5, 6, 7});
        BlockManager.saveAll();
    }

    @Test
    public void startAll() {
        Map<Integer, BlockManager> map = BlockManager.startAll();
        Map<Integer, List<String>> logicBlocks = BlockManager.logicBlocks;
        for (int i = 0; i < 5; i++) {
            Block lb = BlockManager.getLogicBlock(i);
            System.out.println(Arrays.toString(lb.read()));
        }
        System.out.println(map);
        BlockManager.saveAll();
    }
}