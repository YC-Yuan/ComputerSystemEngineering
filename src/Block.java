public class Block {
    private final BlockManager bm;
    private final int size;
    private final int index;

    Block(BlockManager bm, int index, int size) {
        this.bm = bm;
        this.index = index;
        this.size = size;
    }

    int getIndex() {
        return index;
    }


    BlockManager getBlockManager() {
        return bm;
    }


    byte[] read() {
        return null;
    }


    int getSize() {
        return size;
    }
}
