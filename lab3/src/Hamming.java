public class Hamming {
    public static void main(String[] args) {
        Hamming h=new Hamming();
        System.out.println(h.hammingDistance(1, 3));
    }

    public int hammingDistance(int x, int y) {
        int z = x ^ y;
        int num = 0;
        while (z > 0) {
            z = z & (z - 1);
            num++;
        }
        return num;
    }
}
