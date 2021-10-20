import java.util.HashMap;

public class ErrorCode extends RuntimeException {
    // 定义错误码
    public static final int FILE_NOT_FOUND = 11;
    public static final int FILE_NAME_REPEATED = 12;
    public static final int FILE_ID_REPEATED = 13;
    public static final int LOGIC_BLOCK_NOT_FOUND = 21;
    public static final int START_LACK_FM = 91;
    public static final int START_LACK_BM = 92;
    private static final HashMap<Integer,String> ErrorCodeMap = new HashMap<>();

    private final int errorCode;

    static {
        ErrorCodeMap.put(FILE_NOT_FOUND,"File not found.");
        ErrorCodeMap.put(FILE_NAME_REPEATED,"File name repeated in one FileManager.");
        ErrorCodeMap.put(FILE_ID_REPEATED,"File id repeated in all FileManagers.");
        ErrorCodeMap.put(LOGIC_BLOCK_NOT_FOUND,"Logic block not found.");
        ErrorCodeMap.put(START_LACK_FM,"Dir 'FMS' not found, cannot recover system.");
        ErrorCodeMap.put(START_LACK_BM,"Dir 'BMS' not found, cannot recover system.");
    }

    public static String getErrorText(int errorCode) {
        return ErrorCodeMap.getOrDefault(errorCode,"No such error, please check ErrorCode.java");
    }

    public ErrorCode(int errorCode) {
        super(String.format("error code '%d' \"%s\"",errorCode,getErrorText(errorCode)));
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}