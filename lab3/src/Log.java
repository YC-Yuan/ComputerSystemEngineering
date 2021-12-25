public class Log {
    public static final int START = 0;
    public static final int WRITE = 1;
    public static final int COMMIT = 2;
    public static final int ABORT = 3;

    int tid;
    int opt;
    int index;
    char old;
    char value;

    public Log(int tid, int opt) {
        this.tid = tid;
        this.opt = opt;
    }

    public boolean isAbort() {
        return opt == ABORT;
    }

    public boolean isWrite() {
        return opt == WRITE;
    }

    public boolean isCommit() {
        return opt == COMMIT;
    }

    public Log(int tid, int opt, int index, char old, char value) {
        this.tid = tid;
        this.opt = opt;
        this.index = index;
        this.old = old;
        this.value = value;
    }

    public Log(String text) {
        String[] s = text.split(" ");
        try {
            this.tid = Integer.parseInt(s[0]);
            this.opt = Integer.parseInt(s[1]);
            if (opt == WRITE) {
                this.index = Integer.parseInt(s[2]);
                this.old = s[3].charAt(0);
                this.value = s[4].charAt(0);
            }
        } catch (Exception e) {
            System.out.println("解析Log出错,格式不符合要求:" + text);
            e.printStackTrace();
        }
    }

    public String toString() {
        if (opt == WRITE) {
            return tid + " " + opt + " " + index + " " + old + " " + value;
        } else {
            return tid + " " + opt;
        }
    }
}