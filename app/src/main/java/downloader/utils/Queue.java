package downloader.utils;

import java.util.ArrayList;

public class Queue {

    private static Queue instance;
    private ArrayList<QueueObject> list;

    private Queue() {
        list = new ArrayList<>();
    }

    public static Queue getInstance() {
        if (instance != null && instance.list != null) {
            return instance;
        } else {
            instance = new Queue();
            return instance;
        }
    }

    public void add(QueueObject object) {
        list.add(object);
    }

    public void remove(int id) {
        for (QueueObject object : list.toArray(new QueueObject[0])) {
            if (id == object.getId()) {
                list.remove(object);
            }
        }
    }

    public int getTotalQueues() {
        return list.size();
    }

    public QueueObject[] getAvailableQueues() {
        return list.toArray(new QueueObject[0]);
    }
}
