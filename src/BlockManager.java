import java.io.*;
import java.util.*;

import static java.lang.System.exit;

public class BlockManager {
    // BMs公用
    public static final int MAX_SIZE = 2;
    private static final int DUPLICATION_NUM = 3;
    static Map<Integer, BlockManager> bms = new HashMap<>();
    static int countLogicBlock = 0;
    // String格式为"int-int",即BM编号中的Block编号
    static Map<Integer, List<String>> logicBlocks = new HashMap<>();

    public static Map<Integer, List<String>> getLogicBlocks() {
        return logicBlocks;
    }

    // BM自身信息
    static int countBM = 0;
    private static final String rootPath = "./BMs/";
    private static final String mapRootPath = "./Maps/";
    private static final String logicBlockMapPath = "./Maps/logicBlockMap.txt";
    private final int id;

    public int getId() {
        return id;
    }

    // BM独立的物理blocks索引
    int countBlock = 0;
    Map<Integer, Block> blocks = new HashMap<>();

    public BlockManager() {
        id = countBM++;
        bms.put(id, this);
    }

    private BlockManager(int id) {
        this.id = id;
        countBM = Math.max(countBM, id + 1);
        bms.put(id, this);
    }

    // 运行途中重新载入,需要重置数据
    private static void init() {
        bms = new HashMap<>();
        countBM = 0;
        countLogicBlock = 0;
        logicBlocks = new HashMap<>();
    }

    // 用于存储meta的信息
    public static void saveAll() {
        saveLogicBlockMap();
        for (BlockManager bm : bms.values()) {
            bm.save();
        }
    }

    // 持久化：从目录结构读取block的meta信息，进行数据检测后恢复Block对象
    public static Map<Integer, BlockManager> startAll() {
        init();
        loadLogicBlockMap();
        loadLogicBlocks();// 自动修复错误block
        saveAll();// 修复后自动改正文件内容
        return bms;
    }

    // FM向logic block申请读取
    public static Block getLogicBlock(int index) {
        try {
            if (!logicBlocks.containsKey(index)) {
                throw new ErrorCode(ErrorCode.LOGIC_BLOCK_NOT_FOUND);
            }
            // list中存储了多个物理块信息
            List<String> sl = logicBlocks.get(index);
            // 以随机顺序访问直到找到合适块
            Collections.shuffle(sl);
            for (String str : sl) {
                String[] split = str.split("-");
                int bmId = Integer.parseInt(split[0]);
                int blockId = Integer.parseInt(split[1]);
                BlockManager bm = bms.get(bmId);
                return bm.getBlock(blockId);
            }
            throw new ErrorCode(ErrorCode.EMPTY_LOGIC_BLOCK);
        } catch (ErrorCode errorCode) {
            System.out.println(errorCode.getErrorText());
            return null;
        }
    }

    // 申请logic block自动备份
    public static Block newLogicBlock(byte[] b) {
        // 找备份数量个BM,新建块并添加索引
        int logicBlockId = countLogicBlock++;
        List<String> blockInfos = new ArrayList<>();
        // 打乱后访问前几个BM
        Collection<BlockManager> bmc = bms.values();
        if (bmc.size() <= 0) {
            try {
                throw new ErrorCode(ErrorCode.NO_BLOCK_MANAGER_AVAILABLE);
            } catch (ErrorCode e) {
                System.out.println(e.getErrorText());
                return null;
            }
        }
        List<BlockManager> bml = new ArrayList<>(bmc);
        Collections.shuffle(bml);
        Block block = null;
        int duplicateNum = Math.min(DUPLICATION_NUM, bml.size());
        for (int i = 0; i < duplicateNum; i++) {
            BlockManager bm = bml.get(i);
            block = bm.newBlock(logicBlockId, b);
            String blockInfo = bm.getId() + "-" + block.getId();
            // 维护logic block层信息
            blockInfos.add(blockInfo);
        }
        logicBlocks.put(logicBlockId, blockInfos);
        return block;
    }

    public static Block newEmptyLogicBlock(int blockSize) {
        return newLogicBlock(new byte[blockSize]);
    }

    private Block getBlock(int id) {
        if (!blocks.containsKey(id)) {
            throw new ErrorCode(ErrorCode.BLOCK_NOT_FOUND);
        }
        return blocks.get(id);
    }

    // 维护BM内部的block索引
    private Block newBlock(int logicBlockId, byte[] b) {
        int blockId = countBlock++;
        Block block = new Block(this, blockId, logicBlockId, b, b.length);
        blocks.put(blockId, block);
        return block;
    }

    private Block newEmptyBlock(int logicBlockId, int blockSize) {
        return newBlock(logicBlockId, new byte[blockSize]);
    }

    // 工具函数 从文件结构中读取block, 需要在载入BM和
    private static Block loadBlock(int bmId, int blockId) {
        BlockManager bm = bms.get(bmId);
        File meta = new File(getBmBlockMeta(bmId, blockId));
        File data = new File(getBmBlockData(bmId, blockId));
        try {
            if (!meta.exists()) {
                throw new ErrorCode(ErrorCode.BLOCK_META_NOT_FOUND);
            }
            if (!data.exists()) {
                throw new ErrorCode(ErrorCode.BLOCK_DATA_NOT_FOUND);
            }
        } catch (ErrorCode e) {
            System.out.println(e.getErrorText());
            return null;
        }
        try {
            Scanner sc = new Scanner(meta);
            sc.nextInt();// 跳过blockId
            int lbId = sc.nextInt();
            int size = sc.nextInt();
            sc.nextLine();
            String md5 = sc.nextLine();
            byte[] byteData = Util.toByteArray(data);
            // 检验md5
            String dataMd5 = Util.toMd5(byteData);
            if (md5.equals(dataMd5)) {// block完好!
                return new Block(bm, blockId, lbId, byteData, byteData.length);
            } else {// TODO block损坏!需要修复
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void indexBlock(Block block) {
        countBlock = Math.max(countBlock, block.getId() + 1);
        blocks.put(block.getId(), block);
    }

    private static void saveLogicBlockMap() {
        // 持久化logic对应关系信息
        File dir = new File(mapRootPath);
        if (!dir.exists()) {
            boolean mkdir = dir.mkdirs();
        }
        try {
            FileWriter fw;
            fw = new FileWriter(logicBlockMapPath);
            StringBuilder sb = new StringBuilder();
            // 第一个数字：logicBlock个数
            sb.append(logicBlocks.size()).append("\n");
            // 每两行一组：第一行为id和备份数量 第二行为多个int int编号
            for (Map.Entry<Integer, List<String>> lb : logicBlocks.entrySet()) {
                sb.append(lb.getKey()).append(" ").append(lb.getValue().size()).append("\n");
                List<String> list = lb.getValue();
                for (String s : list) {
                    String[] split = s.split("-");
                    int bmId = Integer.parseInt(split[0]);
                    int blockId = Integer.parseInt(split[1]);
                    sb.append(bmId).append(" ").append(blockId).append(" ");
                }
                sb.append("\n");
            }
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 单BM持久化，创建目录结构并把block存成meta和data
    private void save() {
        createDirs();// 创建目录结构
        // 对Block诸个创建.meta和.data
        for (Block b : blocks.values()) {
            String metaPath = getBlockMetaPath(b.getId());
            String dataPath = getBlockDataPath(b.getId());
            try {
                FileWriter fw;
                fw = new FileWriter(metaPath);
                fw.write(b.toString());
                fw.close();
                FileOutputStream fos = new FileOutputStream(dataPath);
                fos.write(b.read());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createDirs() {
        // 目录结构
        File dir = new File(getBmPath());
        File metaDir = new File(getBlockMetaDir());
        File dataDir = new File(getBlockDataDir());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!metaDir.exists()) {
            metaDir.mkdirs();
        }
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    private static void loadLogicBlockMap() {
        File lbInfo = new File(logicBlockMapPath);
        if (lbInfo.exists()) {
            // 需要先恢复LogicBlock信息
            try {
                Scanner sc = new Scanner(new FileReader(lbInfo));
                int lbNum = 0;
                if (sc.hasNextInt()) {
                    lbNum = sc.nextInt();
                }
                for (int i = 0; i < lbNum; i++) {
                    // 解析第一行：逻辑block编号与备份数量
                    int lbId = sc.nextInt();
                    int pbNum = sc.nextInt();
                    countLogicBlock = Math.max(countLogicBlock, lbId + 1);
                    List<String> blockInfos = new ArrayList<>();
                    for (int i1 = 0; i1 < pbNum; i1++) {
                        // 解析第二行:对每个备份,记录备份位置信息
                        blockInfos.add(sc.nextInt() + "-" + sc.nextInt());
                    }
                    logicBlocks.put(lbId, blockInfos);// 重新载入
                }
            } catch (FileNotFoundException ignored) {

            }
        }
    }

    private static void loadLogicBlocks() {
        // 即便没有logic block 也要载入BM的！
        File bmF = new File(rootPath);
        if (bmF.exists()) {
            File[] fs = bmF.listFiles();
            if (fs != null) {
                for (File f : fs) {
                    int bmId = Integer.parseInt(f.getName());
                    // 保证BM载入
                    if (!bms.containsKey(bmId)) {
                        bms.put(bmId, new BlockManager(bmId));
                    }
                }
            }
        }
        // 按照logicBlockMap中记载信息逐个读取 并修复
        for (Map.Entry<Integer, List<String>> entry : logicBlocks.entrySet()) {
            // 对某个logic block
            int lbId = entry.getKey();
            List<String> pbInfos = entry.getValue();
            // 为损坏修复准备的一些变量
            boolean breakdown = false;
            Block coverBlock = null;
            Stack<String> breakInfos = new Stack<>();
            // 第一遍:载入完好block,记录损坏block
            for (String info : pbInfos) {
                String[] split = info.split("-");
                int bmId = Integer.parseInt(split[0]);
                int blockId = Integer.parseInt(split[1]);
                // 保证BM载入
                if (!bms.containsKey(bmId)) {
                    bms.put(bmId, new BlockManager(bmId));
                }
                BlockManager bm = bms.get(bmId);// 由于重新载入,此处一定能找到bm
                Block block = loadBlock(bmId, blockId);
                if (block == null) {
                    breakdown = true;
                    breakInfos.add(info);
                } else {
                    coverBlock = block;
                    bm.indexBlock(block);
                }
            }
            // 第二遍:修复损坏block
            if (breakdown) {
                if (coverBlock == null) {
                    // 损坏发生且备份不足以修复 无法启动
                    throw new ErrorCode(ErrorCode.CANNOT_RECOVER_BLOCK);
                } else {// 执行修复,用完好block数据填充损坏block
                    for (String info1 : breakInfos) {
                        String[] split = info1.split("-");
                        int bmId = Integer.parseInt(split[0]);
                        int blockId = Integer.parseInt(split[1]);
                        BlockManager bm = bms.get(bmId);// 由于重新载入,此处一定能找到bm
                        Block block = new Block(bm, blockId, lbId, coverBlock.read(), coverBlock.getSize());
                        bm.indexBlock(block);
                    }
                }
            }
        }
    }

    private static String getBmBlockMeta(int bmId, int blId) {
        return rootPath + bmId + "/meta/" + blId + ".meta";
    }

    private static String getBmBlockData(int bmId, int blId) {
        return rootPath + bmId + "/data/" + blId + ".data";
    }

    private String getBmPath() {// BM所属文件夹
        return rootPath + id + "/";
    }

    private String getBlockMetaDir() {// BM存meta的文件夹
        return getBmPath() + "meta/";
    }

    private String getBlockMetaPath(int blockId) {// BM的meta文件地址
        return getBlockMetaDir() + blockId + ".meta";
    }

    private String getBlockDataDir() {
        return getBmPath() + "data/";
    }

    private String getBlockDataPath(int blockId) {
        return getBlockDataDir() + blockId + ".data";
    }
}
