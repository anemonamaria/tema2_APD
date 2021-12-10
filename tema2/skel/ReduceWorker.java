import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ReduceWorker extends Thread{
    private final ReduceWorkPool reduceWorkPool;
    private final HashMap<String, MapDictionary> dictionaryHashMap;

    ReduceWorker(ReduceWorkPool reduceWorkPool, HashMap<String, MapDictionary> dictionaryHashMap) {
        this.reduceWorkPool = reduceWorkPool;
        this.dictionaryHashMap = dictionaryHashMap;
    }

    public double calcRang(Vector<Integer> fib, MapDictionary mapDictionary) {
        float sum = 0;
        double rang = 0;
        int totalWords = 0;
        for(Map.Entry<Integer, Integer> item : mapDictionary.getDictionary().entrySet()) {
            if (item.getKey() == mapDictionary.getMaxValue()) {
                sum = sum + fib.get(mapDictionary.getMaxValue()) * mapDictionary.getMaxWords().size();
                totalWords += mapDictionary.getMaxWords().size();
//                System.out.println(" max  fib (" + mapDictionary.getMaxValue() + " ) * " +  mapDictionary.getMaxWords().size()  + "  fisierul " + mapDictionary.getFileName()
//                        + " cuv " + mapDictionary.getMaxWords() + " indici " + mapDictionary.getIndices() );
            } else {
                sum = sum + fib.get(item.getKey()) * item.getValue();
                if(item.getValue() != 0)
                    totalWords += item.getValue();
//                System.out.println("normal fib (" + item.getKey() + " ) * " +  item.getValue()  + "  fisierul " + mapDictionary.getFileName() + " cuv " + mapDictionary.getMaxWords()
//                        + " indici " + mapDictionary.getIndices() );

            }
        }
        rang = sum / totalWords;
        String.format("%.2f", rang);
//        System.out.println("rang " +rang + " total w "+ totalWords + " din fisierul " + mapDictionary.getFileName()  );

        if (rang * 100.0 % 5 == 0)
            return rang;
        else
            return Math.round(rang * 100.0) / 100.0;

    }

    public void processReduceTask(ReduceTask task) {
        MapDictionary myDic = new MapDictionary(task.getFileName().toString());
        //etapa de combinare
        for(MapDictionary item : task.getMapResults()) {
            myDic.addMapDictionary(item);
        }
        dictionaryHashMap.put(task.getFileName().toString(), myDic);

        Vector<Integer> fibonacci = new Vector<Integer>();
        fibonacci.add(1); fibonacci.add(1);
        for(int k = 2; k <= 100; k ++) {
            fibonacci.add(fibonacci.get(k-2) + fibonacci.get(k-1));
        }
        myDic.setRang(calcRang(fibonacci, myDic));

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
