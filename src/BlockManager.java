import java.io.*;
import java.util.*;

public class BlockManager {
    // BMs公用
    private static final int DUPLICATION_NUM = 3;
    static Map<Integer,BlockManager> bms = new HashMap<>();

    // BM自身信息
    static int countBM = 0;
    private static final String rootPath = "./BMs/";
    private final String savePath;
    private static final String mapRootPath = "./Maps/";
    private static final String logicBlockMapPath = "./Maps/logicBlockMap.txt";
    private final int id;

    // 所有BM公用的逻辑block池
    static int countLogicBlock = 0;
    // String格式为"int-int",即BM编号中的Block编号
    static Map<Integer,List<String>> logicBlocks = new HashMap<>();

    // BM独立的物理blocks索引
    int countBlock = 0;
    Map<Integer,Block> blocks = new HashMap<>();

    public BlockManager() {
        id = countBM++;
        savePath = rootPath + id + "/";
        // 自动添加到管理池
        bms.put(id,this);
    }

    // 用于存储meta的信息
    public static void saveAll() {
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
            for (Map.Entry<Integer,List<String>> lb : logicBlocks.entrySet()) {
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
        for (BlockManager bm : bms.values()) {
            bm.save();
        }
    }

    // 持久化，创建目录结构并把block存成meta和data
    public void save() {
        // 目录结构
        File dir = new File(savePath);
        if (!dir.exists()) {
            boolean mkdir = dir.mkdirs();
        }
        // 对Block诸个创建.meta和.data
        for (Block b : blocks.values()) {
            String metaPath = savePath + b.getId() + ".meta";
            String dataPath = savePath + b.getId() + ".data";
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

    // 持久化：从目录结构读取block的meta信息，进行数据检测后恢复Block对象
    public static Map<Integer,BlockManager> startAll() {
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
                    countLogicBlock = Math.max(countLogicBlock,lbId);
                    List<String> blockInfos = new ArrayList<>();
                    for (int i1 = 0; i1 < pbNum; i1++) {
                        // 对每个备份：记录备份位置信息
                        blockInfos.add(sc.nextInt() + "-" + sc.nextInt());
                    }


                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        File file = new File(rootPath);
        File[] fl = file.listFiles();
        if (fl == null) {
            throw new ErrorCode(ErrorCode.START_LACK_BM);
        }
        // 每个文件夹对应一个BM
        for (File f : fl) {


        }
        return null;
    }

    // FM向logic block申请读取
    public static Block getLogicBlock(int index) {
        if (!logicBlocks.containsKey(index)) {
            // TODO logic block不存在
            throw new ErrorCode(1);
        }
        // list中存储了多个物理块信息
        List<String> sl = logicBlocks.get(index);
        // 以随机顺序访问直到找到合适块
        Collections.shuffle(sl);
        for (String str : sl) {
            String[] split = str.split("-");
            int bmId = Integer.parseInt(split[0]);
            int blockId = Integer.parseInt(split[1]);
            // 访问对应Block 查看数据是否完好

        }
        return null;
    }

    private Block getBlock(int id) {
        if (!blocks.containsKey(id)) {
            // TODO BM没存储对应block
            throw new ErrorCode(1);
        }
        return blocks.get(id);
    }

    public Block newLogicBlock(byte[] b) {
        int logicBlockId = countLogicBlock++;
        List<String> blockInfos = new ArrayList<>();
        Block block = newBlock(b);
        String blockInfo = id + "-" + block.getId();
        blockInfos.add(blockInfo);
        logicBlocks.put(logicBlockId,blockInfos);
        return null;
    }

    public static Block newEmptyLogicBlock() {
        return null;
    }

    private Block newBlock(byte[] b) {
        int blockId = countBlock++;
        Block block = new Block(this,blockId,b,b.length);
        blocks.put(blockId,block);
        return block;
    }

    private Block newEmptyBlock(int blockSize) {
        int blockId = countBlock++;
        Block block = new Block(this,blockId,new byte[blockSize],blockSize);
        blocks.put(blockId,block);
        return block;
    }


    public int getId() {
        return id;
    }
}
