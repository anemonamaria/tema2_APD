import com.sun.jdi.connect.spi.TransportService;

import java.util.LinkedList;

public class ReduceWorkPool {
    LinkedList<ReduceTask> tasks;
    int nrOfThreads;
    int waitingThreads = 0;
    boolean ready = false;

    public ReduceWorkPool(int nrOfThreads) {
        this.nrOfThreads = nrOfThreads;
        tasks = new LinkedList<ReduceTask>();
    }

    public synchronized ReduceTask getWork() {
        if (tasks.size() == 0) {
            waitingThreads++;
            if(waitingThreads == nrOfThreads) {
                ready = true;
                notifyAll();
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
                waitingThreads--;
            }
        }
        return tasks.remove();
    }

    synchronized void putWork(ReduceTask task) {
        tasks.add(task);
        this.notify();
    }
}
