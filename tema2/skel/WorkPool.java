import java.util.Vector;

// avem o clasa pe care o vom folosi pentru ambele tipuri de task-uri
class WorkPool {
    Vector<MapTask> mapTasks = new Vector<>();
    Vector<ReduceTask> reduceTasks= new Vector<>();
    int w8Workers = 0;
    int workers;
    int ready = 0;

    WorkPool(int nrOfThreads) {
        this.workers = nrOfThreads;
    }

    synchronized void putMapWork(MapTask mapTask) {
        mapTasks.add(mapTask);
        this.notify();
    }

    synchronized void putReduceWork(ReduceTask reduceTask) {
        reduceTasks.add(reduceTask);
        this.notify();
    }

    public synchronized MapTask getMapWork(){
        if(mapTasks.size() == 0) {
            w8Workers++;  // niciun task, niciun worker
            if(w8Workers == workers) {
                ready = 1;  // Map terminat, anuntam celelalte thread-uri
                notifyAll();
                return null;
            } else {
                while (ready == 0 && mapTasks.isEmpty()) {
                    try {
                        this.wait();  // asteapta
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if(ready == 1)  //finish
                    return null;
                else w8Workers --;
            }
        }
        return mapTasks.remove(0);
    }

    public synchronized ReduceTask getReduceWork() {
        if (reduceTasks.size() == 0) {
            w8Workers++;
            if(w8Workers == workers) {
                ready = 1;
                notifyAll();
                return null;
            } else {
                while (ready == 0 && reduceTasks.size() == 0) {
                    try {
                        this.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(ready == 1)
                    return null;
                w8Workers--;
            }
        }
        return reduceTasks.remove(0);
    }
}