import java.util.LinkedList;
import java.util.Vector;

public class MapWorkPool {
    LinkedList<MapTask> tasks;
    int waitingThreads = 0;   //////////
    int nrOfThreads; ////
    public boolean ready = false;

    MapWorkPool(int nrOfThreads) {
        this.tasks = new LinkedList<MapTask>();
        this.nrOfThreads = nrOfThreads;
    }

    synchronized void addWork(MapTask mapTask) {
        tasks.add(mapTask);
        this.notify();
    }

    ////////////////////////
    public synchronized MapTask getWork(){
        if(tasks.size() == 0) {
            waitingThreads++;
            if(waitingThreads == nrOfThreads) {
                ready = true;
                notifyAll();
                return null;
            } else {
                while (!ready && tasks.size() == 0) {
                    try {
                        this.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if(ready)
                    return null;
                waitingThreads --;
            }
        }
        return tasks.remove();
    }

    ////////////////////////
}
