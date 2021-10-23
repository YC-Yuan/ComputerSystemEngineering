import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Tools {
    public static byte[] smartCat(int fileId) {
        CSEFile file = FileManager.getFile(fileId);
        if (file == null) {
            try {
                throw new ErrorCode(ErrorCode.CAT_UNRESOLVED_FID);
            } catch (ErrorCode e) {
                System.out.println(e.getErrorText());
                return new byte[0];
            }
        }
        return file.readAll();
    }

    public static CSEFile smartCopy(int fileId) {
        CSEFile file = FileManager.getFile(fileId);
        if (file == null) {
            try {
                throw new ErrorCode(ErrorCode.COPY_UNRESOLVED_FID);
            } catch (ErrorCode e) {
                System.out.println(e.getErrorText());
                return null;
            }
        }
        FileManager fileManager = file.getFileManager();
        return fileManager.copyFile(file);
    }

    public static void smartHex(int blockId) {
        Block lb = BlockManager.getLogicBlock(blockId);
        if (lb == null) {
            try {
                throw new ErrorCode(ErrorCode.HEX_UNRESOLVED_BID);
            } catch (ErrorCode e) {
                System.out.println(e.getErrorText());
            }
        } else {
            System.out.println(Util.byteToHex(lb.read()));
        }
    }

    public static void smartWrite(int offset, int where, int fileId) {
        CSEFile file = FileManager.getFile(fileId);
        if (file == null) {
            try {
                throw new ErrorCode(ErrorCode.WRITE_UNRESOLVED_FID);
            } catch (ErrorCode e) {
                System.out.println(e.getErrorText());
                return;
            }
        }
        Scanner sc = new Scanner(System.in);
        System.out.println("SmartWrite, please enter:");
        String input = sc.nextLine();
        file.move(offset, where);
        file.write(input.getBytes(StandardCharsets.UTF_8));
    }

    public static void smartLs() {
        StringBuilder sb = new StringBuilder();
        // FM层
        sb.append("-----FM layer, num of FMs:").append(FileManager.fms.size()).append("\n");
        for (FileManager fm : FileManager.fms.values()) {
            sb.append("FM-").append(fm.getId()).append(" ");
        }
        sb.append("\n");
        // File
        sb.append("-----File layer, num of Files:").append(FileManager.files.size()).append("\n");
        for (FileManager fm : FileManager.fms.values()) {
            for (CSEFile file : fm.fileNames.values()) {
                sb.append("FM:").append(fm.getId())
                        .append("-FileName:").append(file.getFileName())
                        .append("-FileId:").append(file.getFileId())
                        .append("-LogicBlocks:").append(file.getLogicBlocks()).append("\n");
            }
        }
        // logic block
        sb.append("-----Logic block layer, num of LogicBlocks:").append(BlockManager.getLogicBlocks().size()).append("\n");
        sb.append("logic block id- [physical block info(BmId-BlockId)]\n");
        for (Map.Entry<Integer, List<String>> lb : BlockManager.getLogicBlocks().entrySet()) {
            sb.append("Logic block:").append(lb.getKey()).append("-physical blocks:").append(lb.getValue()).append("\n");
        }
        // BM
        sb.append("-----BM layer, num of BMs:").append(BlockManager.bms.size()).append("\n");
        for (BlockManager bm : BlockManager.bms.values()) {
            sb.append("BM-").append(bm.getId()).append(" ");
        }
        sb.append("\n");
        // physical block
        sb.append("-----Block layer, num of Blocks:").append(FileManager.files.size()).append("\n");
        for (BlockManager bm : BlockManager.bms.values()) {
            for (Block b : bm.blocks.values()) {
                sb.append("BM:").append(bm.getId())
                        .append("-BlockId:").append(b.getId()).append("\n");
            }
        }
        sb.append("----- smart ls end -----\n");
        System.out.println(sb);
    }
}
