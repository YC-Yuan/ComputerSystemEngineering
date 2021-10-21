import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
    public static byte[] toByteArray(File f) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length())) {
            BufferedInputStream in = null;
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String toMd5(byte[] b) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            md5.update(b);
            return new BigInteger(1, md5.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        throw new ErrorCode(ErrorCode.CANNOT_GENERATE_MD5);
    }

    public static byte[] concatArray(byte[] a, byte[] b, byte[] c) {
        byte[] ans = new byte[a.length + b.length + c.length];
        System.arraycopy(a, 0, ans, 0, a.length);
        System.arraycopy(b, 0, ans, a.length, b.length);
        System.arraycopy(c, 0, ans, a.length + b.length, c.length);
        return ans;
    }
}
