import java.util.Vector;

public class MapWorkPool {
    Vector<MapTask> tasks;

    MapWorkPool() {
        this.tasks = new Vector<>();
    }

    void addWork(MapTask mapTask) {
        this.tasks.add(mapTask);
    }
}
