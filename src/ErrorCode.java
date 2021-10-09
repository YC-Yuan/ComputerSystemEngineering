import java.util.HashMap;

public class ErrorCode extends RuntimeException {
    // 定义错误码
    public static final int FILE_NOT_FOUND = 11;
    public static final int FILE_CREATION_REPEATED = 12;
    private static final HashMap<Integer, String> ErrorCodeMap = new HashMap<>();

    private final int errorCode;

    static {
        ErrorCodeMap.put(FILE_NOT_FOUND, "File not found.");
        ErrorCodeMap.put(FILE_CREATION_REPEATED, "File cannot be created repeatedly.");
    }

    public static String getErrorText(int errorCode) {
        return ErrorCodeMap.getOrDefault(errorCode, "No such error, please check ErrorCode.java");
    }

    public ErrorCode(int errorCode) {
        super(String.format("error code '%d' \"%s\"", errorCode, getErrorText(errorCode)));
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}