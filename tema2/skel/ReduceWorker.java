import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

class ReduceTask {
    String fileName;
    Vector<MapDictionary> mapResults;

    ReduceTask(String fileName, Vector<MapDictionary> mapResults) {
        this.fileName = fileName;
        this.mapResults = mapResults;
    }
}

public class ReduceWorker extends Thread{
    final WorkPool reduceWorkPool;
    final HashMap<String, MapDictionary> dictionaryHashMap;

    ReduceWorker(WorkPool reduceWorkPool, HashMap<String, MapDictionary> dictionaryHashMap) {
        this.reduceWorkPool = reduceWorkPool;
        this.dictionaryHashMap = dictionaryHashMap;
    }

    public double calcRang(Vector<Integer> fib, MapDictionary mapDictionary) {
        float sum = 0;
        double rang = 0;
        int totalWords = 0;
        // calculez rangul cu formula din enunt
        for(Map.Entry<Integer, Integer> item : mapDictionary.dictionary.entrySet()) {
            if (item.getKey() == mapDictionary.maxValue) {
                sum = sum + fib.get(mapDictionary.maxValue) * mapDictionary.maxWords.size();
                totalWords = totalWords + mapDictionary.maxWords.size();
            } else {
                sum = sum + fib.get(item.getKey()) * item.getValue();
                if(item.getValue() != 0)
                    totalWords = totalWords + item.getValue();
            }
        }
        rang = sum / totalWords;
        if (rang * 100.0 % 5 == 0)
            return rang;
        else
            return Math.round(rang * 100.0) / 100.0;

    }

    public void processReduceTask(ReduceTask task) {
        MapDictionary myDic = new MapDictionary(task.fileName.toString());
        //etapa de combinare
        for(MapDictionary item : task.mapResults) {
            myDic.addMapDictionary(item); // etapa de combinare a dictionarelor partiale
        }
        dictionaryHashMap.put(task.fileName, myDic);

        Vector<Integer> fibonacci = new Vector<>();
        fibonacci.add(1); fibonacci.add(1);
        for(int k = 2; k <= 100; k ++) {
            fibonacci.add(fibonacci.get(k-2) + fibonacci.get(k-1));
        }
        myDic.rang = calcRang(fibonacci, myDic);  // etapa de procesare a calcului rangului
    }

    public void run() {
        while (true) {
            ReduceTask task = reduceWorkPool.getReduceWork();
            if(task == null)
                break;
            processReduceTask(task);
        }
    }
}

