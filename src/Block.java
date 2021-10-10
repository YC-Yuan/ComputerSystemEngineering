public class Block {
    private final BlockManager bm;
    private final int size;
    private final int index;

    private final byte[] data;

    Block(BlockManager bm, int index, byte[] data, int size) {
        this.bm = bm;
        this.index = index;
        this.data = data;
        this.size = size;
    }

    int getIndex() {
        return index;
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
