import java.util.*;

public class BlockPool {
    // 参数配置-逻辑block备份份数
    private final int DUPLICATION_NUM = 3;

    // 统一管理所有BM,作为File层与Block层之间的接口,将block提供给file使用
    private static final HashMap<Integer,BlockManager> bms = new HashMap<>();
    // String表示为"int int",含义是bm编号-bm中的blockIndex
    private static final HashMap<Integer,ArrayList<String>> blockIndexes = new HashMap<>();

    public static void addBlockManager(BlockManager bm) {
        if (bms.containsKey(bm.getId())) {
            // TODO 报错,重复添加了BM(不应该出现,但不排除怪事发生)
        }
        else {
            bms.put(bm.getId(),bm);
        }
    }

    // 根据逻辑block index获取一个物理block
    Block getBlock(int index) {
        // 逻辑block是否存在
        if (blockIndexes.containsKey(index)) {
            // 存在此block
            ArrayList<String> realBlocks = blockIndexes.get(index);
            // 以随机顺序尝试访问,直到找到完好的block
            String blockInfo = realBlocks.get((int) (Math.random() * realBlocks.size()));
            String[] infos = blockInfo.split(" ");
            int bmIndex = Integer.parseInt(infos[0]);
            int blockIndex = Integer.parseInt(infos[1]);
            Block block = bms.get(bmIndex).getBlock(blockIndex);
            // TODO: 此处需要考虑物理block损坏后根据备份进行修复
            return block;
        }
        else {
            // 逻辑block根本就不存在,直接报错
            throw new ErrorCode(ErrorCode.LOGIC_BLOCK_NOT_FOUND);
        }
    }

    // 按参数配置进行备份
    Block newBlock(byte[] b) {
        return newBlock(b,DUPLICATION_NUM);
    }

    private String getLogicInfo(int bmIndex,int blockIndex) {
        return bmIndex + " " + blockIndex;
    }

    // 申请新的逻辑block,向duplication个服务器做备份
    Block newBlock(byte[] b,int duplication) {
        if (bms.size() == 0) {
            // TODO 根本没有BM,报错并提醒创建
        }
        // 随机锁定需要访问的BM
        List<Integer> bmIndexes = new LinkedList<>();
        for (int i = 0; i < bms.size(); i++) {
            bmIndexes.add(i);
        }
        if (bmIndexes.size() < duplication) {
            // TODO 可用服务器不足以完成备份要求,应做出警告提示而后执行
        }
        else if (bmIndexes.size() > duplication) {
            // 可用服务器过多,随机选取duplication个
            Collections.shuffle(bmIndexes);
            bmIndexes.subList(0,3);
        }
        Block block;
        ArrayList<String> lbInfos = new ArrayList<>();
        for (Integer bmIndex : bmIndexes) {
            BlockManager bm = bms.get(bmIndex);
            // TODO 考虑单个服务器创建错误时的处理
            block = bm.newBlock(b);
            lbInfos.add(getLogicInfo(bmIndex,block.getId()));
        }
        blockIndexes.put(bms.size() + 1,lbInfos);
        return null;
    }
}
