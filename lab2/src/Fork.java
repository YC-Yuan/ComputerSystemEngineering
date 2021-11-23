public class Fork extends MapLamportLock {
    private int id;

    Fork(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
