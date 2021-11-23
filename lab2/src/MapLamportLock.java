import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapLamportLock implements MyLock {
    static class ThreadInfo {
        int id;
        int number;
        boolean choosing;
    }

    Map<Integer,ThreadInfo> map = new ConcurrentHashMap<>();

    private int getMaxNumber() {
        int ans = 0;
        for (ThreadInfo info : map.values()) {
            ans = Math.max(info.number,ans);
        }
        return ans;
    }

    @Override
    public void lock() {
        Thread currentThread = Thread.currentThread();
        int id = (int) currentThread.getId();
        ThreadInfo info = new ThreadInfo();
        if (map.containsKey(id)) {
            info = map.get(id);
        }
        else {
            info.id = id;
            map.put(id,info);
            info.choosing = true;
            info.number = 1 + getMaxNumber();
            info.choosing = false;
        }
        for (ThreadInfo other : map.values()) {
            if (other.id != info.id) {
                while (other.choosing) {
                    try {
                        Thread.sleep(0,1);// 让出线程 傻子不会自己触发切换
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while ((other.number != 0) &&
                        ((other.number < info.number) ||
                                (other.number == info.number && other.id < info.id))) {
                    try {
                        Thread.sleep(0,1);// 让出线程 傻子不会自己触发切换
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void unLock() {
        Thread currentThread = Thread.currentThread();
        int id = (int) currentThread.getId();
        map.get(id).number = 0;
        map.remove(id);
    }

}
