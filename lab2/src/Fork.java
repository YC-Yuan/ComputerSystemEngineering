public class Fork extends LinkedLamportLock {
    private int id;

    Fork(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
