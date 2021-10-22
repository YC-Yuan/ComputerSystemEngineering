import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class UtilTest {

    @Test
    public void concatArray() {
        byte[][] b = new byte[3][];
        b[0] = new byte[] {1,2,3};
        b[1] = new byte[] {6,5,4};
        b[2] = new byte[] {7,8,9};
        System.out.println(Arrays.toString(Util.concatArray(b)));
    }

    @Test
    public void removeTail() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        Util.removeListTail(list,3);
        System.out.println(list);
    }
}