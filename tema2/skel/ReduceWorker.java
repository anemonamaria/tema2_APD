import java.util.HashMap;

public class ReduceWorker extends Thread{
    private ReduceWorkPool reduceWorkPool;
    private HashMap<String, MapDictionary> dictionaryHashMap;

    ReduceWorker(ReduceWorkPool reduceWorkPool, HashMap<String, MapDictionary> dictionaryHashMap) {
        this.reduceWorkPool = reduceWorkPool;
        this.dictionaryHashMap = dictionaryHashMap;
    }

    public void processReduceTask(ReduceTask task) {
        MapDictionary myDic = new MapDictionary(task.getFileName().toString());
        for(MapDictionary item : task.getMapResults()) {
            myDic.addMapDictionary(item);
        }
        dictionaryHashMap.put(task.getFileName().toString(), myDic);
    }

    public void run() {
        while (true) {
            ReduceTask task = reduceWorkPool.getWork();
            if(task == null)
                break;
            processReduceTask(task);
        }
    }
}
