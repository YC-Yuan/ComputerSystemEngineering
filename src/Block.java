// 存储block meta信息, 总是根据meta中的路径寻找block data
public class Block {
    private final BlockManager bm;
    private final int size;
    private final int id;
    private final byte[] data;

    Block(BlockManager bm,int id,byte[] data,int size) {
        this.bm = bm;
        this.id = id;
        this.data = data;
        this.size = size;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

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
