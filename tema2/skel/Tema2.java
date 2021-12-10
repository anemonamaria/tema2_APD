import java.io.*;
import java.util.*;


public class Tema2 {

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }

        // citesc din linia de comanda
        int workers = Integer.parseInt(args[0]);
        BufferedReader input = new BufferedReader(new FileReader(args[1]));
        BufferedWriter output = new BufferedWriter(new FileWriter(args[2]));

        // citesc din fisierul de input
        Vector<String> files = new Vector<String>();
        int offset = Integer.parseInt(input.readLine());
        int nrDocs = Integer.parseInt(input.readLine());
        String aux;
        int i = 0;

        while ( (aux = input.readLine()) != null) {
            files.add(i, aux);
            i++;
        }

        // declar workerii pe care ii voi folosi
        MapWorkPool mapWork = new MapWorkPool(workers);
        ReduceWorkPool reduceWork = new ReduceWorkPool(workers);
        HashMap<String, Vector<MapDictionary>> fragmentsDic = new HashMap<>();
        HashMap<String, MapDictionary> dictionaryRes = new HashMap<>();
        Vector<MapWorker> mapWorkers = new Vector<>();
        Vector<ReduceWorker> reduceWorkers = new Vector<>();
        String separators = ";:/?˜\\.,><‘\\[]\\{}\\(\\)!@#$%ˆ&-'+’=*”|\" \t\n\r\0";

        //parcurg fisierele de intrare
        for (i = 0; i < nrDocs; i++) {
            fragmentsDic.put((files.get(i)), new Vector<>());
            dictionaryRes.put((files.get(i)),  new MapDictionary((files.get(i))));

            int offsetStart = 0;
            int finishOffset = offset;

            while(offsetStart < (new File(files.get(i))).length()){
                // sparge continutul fisierului in fragmente si facem task-urile de tip MAP
                if(finishOffset < (new File(files.get(i))).length())
                    mapWork.addWork(new MapTask(files.get(i), offsetStart, finishOffset - offsetStart));
                else
                    mapWork.addWork(new MapTask(files.get(i), offsetStart, (int) ((new File(files.get(i))).length() - offsetStart)));
                offsetStart += offset;
                finishOffset += offset;
            }
        }

        for(int k = 0; k < workers; k++) {
            MapWorker workerMap = new MapWorker(mapWork, fragmentsDic);
            mapWorkers.add(workerMap);
            workerMap.start();
        }
        for( int k = 0; k < workers; k++ ) {
            mapWorkers.get(k).join();
        }

        for(Map.Entry<String, Vector<MapDictionary>> item : fragmentsDic.entrySet()) {
            ReduceTask auxReduceTask = new ReduceTask(item.getKey(), item.getValue());
            reduceWork.addWork(auxReduceTask);
        }

        for(int k = 0; k < workers; k++) {
            ReduceWorker workerReduce = new ReduceWorker(reduceWork, dictionaryRes);
            reduceWorkers.add(workerReduce);
            workerReduce.start();
        }

        for(int k = 0; k < workers; k++) {
            reduceWorkers.get(k).join();
        }

        // sortez fisierele
        Vector<Map.Entry<String, MapDictionary>> finalResults = new Vector<>(dictionaryRes.entrySet());
        finalResults.sort((o1, o2) -> {
            if (o1.getValue().rang < o2.getValue().rang) {
                return 1;
            } else if (o1.getValue().rang> o2.getValue().rang) {
                return -1;
            } else {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        // afisez in output rezultatul
        for(Map.Entry<String, MapDictionary> item : finalResults) {
            output.write(item.getKey().substring(12) + "," + item.getValue().rang+ ","
                    + item.getValue().maxValue + "," + item.getValue().maxWords.size() + "\n");
        }

        input.close();
        output.close();
    }
}
