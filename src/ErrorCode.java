import java.util.HashMap;

public class ErrorCode extends RuntimeException {
    // 定义错误码
    public static final int CURSOR_LESS_THAN_ZERO=1;
    public static final int CURSOR_GREATER_THAN_SIZE=2;
    public static final int FILE_NOT_FOUND = 11;
    public static final int FILE_NAME_REPEATED = 12;
    public static final int FILE_ID_REPEATED = 13;
    public static final int LOGIC_BLOCK_NOT_FOUND = 21;
    public static final int EMPTY_LOGIC_BLOCK = 22;
    public static final int NO_FILE_MANAGER_AVAILABLE = 23;
    public static final int NO_BLOCK_MANAGER_AVAILABLE = 24;
    public static final int BLOCK_META_NOT_FOUND = 51;
    public static final int BLOCK_DATA_NOT_FOUND = 52;
    public static final int BLOCK_NOT_FOUND = 53;
    public static final int START_LACK_FM = 91;
    public static final int START_LACK_BM = 92;
    public static final int CANNOT_RECOVER_BLOCK = 99;
    public static final int CANNOT_GENERATE_MD5 = 100;
    private static final HashMap<Integer, String> ErrorCodeMap = new HashMap<>();

    private final int errorCode;

    static {
        ErrorCodeMap.put(CURSOR_LESS_THAN_ZERO, "Cannot move cursor to position less than 0.");
        ErrorCodeMap.put(CURSOR_GREATER_THAN_SIZE, "Cannot move cursor out of file.");
        ErrorCodeMap.put(FILE_NOT_FOUND, "File not found.");
        ErrorCodeMap.put(FILE_NAME_REPEATED, "File name repeated in one FileManager.");
        ErrorCodeMap.put(FILE_ID_REPEATED, "File id repeated in all FileManagers.");
        ErrorCodeMap.put(LOGIC_BLOCK_NOT_FOUND, "Logic block not found.");
        ErrorCodeMap.put(EMPTY_LOGIC_BLOCK, "Logic block does not connected to any block.");
        ErrorCodeMap.put(NO_FILE_MANAGER_AVAILABLE, "No any available FM.");
        ErrorCodeMap.put(NO_BLOCK_MANAGER_AVAILABLE, "No any available BM.");
        ErrorCodeMap.put(BLOCK_META_NOT_FOUND, "Block meta not found.");
        ErrorCodeMap.put(BLOCK_DATA_NOT_FOUND, "Block data not found.");
        ErrorCodeMap.put(BLOCK_NOT_FOUND, "Block in BM not found.");
        ErrorCodeMap.put(START_LACK_FM, "Dir 'FMS' not found, cannot recover system.");
        ErrorCodeMap.put(START_LACK_BM, "Dir 'BMS' not found, cannot recover system.");
        ErrorCodeMap.put(CANNOT_RECOVER_BLOCK, "All copies for a logic block damaged, cannot recover it.");
        ErrorCodeMap.put(CANNOT_GENERATE_MD5, "Cannot use MD5.");
    }

    public static String getErrorText(int errorCode) {
        return ErrorCodeMap.getOrDefault(errorCode, "No such error, please check ErrorCode.java");
    }

    public String getErrorText() {
        return getErrorText(errorCode);
    }

    public ErrorCode(int errorCode) {
        super(String.format("error code '%d' \"%s\"", errorCode, getErrorText(errorCode)));
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}