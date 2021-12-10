import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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
        int offset;
        int nrDocs;
        Vector<String> files = new Vector<String>();

        offset = Integer.parseInt(input.readLine());
        nrDocs = Integer.parseInt(input.readLine());
        String aux;
        int i = 0;
        while ( (aux = input.readLine()) != null) {
            files.add(i, aux);
            i++;
        }

        MapWorkPool mapWork = new MapWorkPool(workers);//workers
        ReduceWorkPool reduceWork = new ReduceWorkPool(workers);
        HashMap<String, Vector<MapDictionary>> fragmentsTask = new HashMap<String, Vector<MapDictionary>>();
        HashMap<String, MapDictionary> dictionaryRes = new HashMap<String, MapDictionary>();
        Vector<MapWorker> mapWorkers = new Vector<MapWorker>();
        Vector<ReduceWorker> reduceWorkers = new Vector<>();
        String separators = ";:/?˜\\.,><‘\\[]\\{}\\(\\)!@#$%ˆ&-'+’=*”|\" \t\n\r\0";
        int j;

        //citire cuvinte din fisiere
        for (i = 0; i < nrDocs; i++) {
            j = 0;
            fragmentsTask.put((files.get(i)).toString(), new Vector<MapDictionary>());
            dictionaryRes.put((files.get(i)).toString(),  new MapDictionary((files.get(i)).toString()));
            String mainString = new BufferedReader(new FileReader(files.get(i))).readLine();
            StringTokenizer auxiliary = new StringTokenizer(mainString, separators);
            while(auxiliary.hasMoreTokens()) {
                String word = auxiliary.nextToken();
                j++;
            }

            int offsetStart = 0;
            int finishOffset = offset;
            int k = 0;
            File auxFile = new File(files.get(i));

            while(offsetStart < auxFile.length()){
                // sparge continutul fisierului in fragmente si cream task-urile de tip MAP
                if(finishOffset < auxFile.length()) {
                    mapWork.addWork(new MapTask(files.get(i), offsetStart, finishOffset - offsetStart));   // TODO aici era fara - offsetStart
                } else {
                    mapWork.addWork(new MapTask(files.get(i), offsetStart, (int) (auxFile.length() - offsetStart)));
                }
                offsetStart += offset;
                finishOffset += offset;
            }
        }

        for(int k = 0; k < workers; k++) {
            MapWorker workerMap = new MapWorker(mapWork, fragmentsTask, k );
            mapWorkers.add(workerMap);
            workerMap.start();
        }
//        System.out.println(workers);
        for( int k = 0; k < workers; k++ ) {
            mapWorkers.get(k).join();
        }

        for(Map.Entry<String, Vector<MapDictionary>> item : fragmentsTask.entrySet()) {
            reduceWork.putWork(new ReduceTask(item.getKey(), item.getValue()));
        }

        for(int k = 0; k < workers; k++) {
            ReduceWorker workerReduce = new ReduceWorker(reduceWork, dictionaryRes);
            reduceWorkers.add(workerReduce);
            workerReduce.start();
        }

        for(int k = 0; k < workers; k++) {
            reduceWorkers.get(k).join();
        }

        ArrayList<Map.Entry<String, MapDictionary>> finalResults = new ArrayList<Map.Entry<String, MapDictionary>>(dictionaryRes.entrySet());
        finalResults.sort(new Comparator<Map.Entry<String, MapDictionary>>() {
            @Override
            public int compare(Map.Entry<String, MapDictionary> o1, Map.Entry<String, MapDictionary> o2) {
                if (o1.getValue().getRang() < o2.getValue().getRang()) {
                    return 1;
                } else if (o1.getValue().getRang() > o2.getValue().getRang()) {
                    return -1;
                } else {
                    return o1.getKey().compareTo(o2.getKey());
                }
            }
        });

        for(Map.Entry<String, MapDictionary> item : finalResults) {
            DecimalFormat myFormat = new DecimalFormat("#.00");
            myFormat.setRoundingMode(RoundingMode.DOWN);
            output.write(item.getKey().substring(12) + "," + myFormat.format(item.getValue().getRang()) + ","
                    + item.getValue().getMaxValue() + "," + item.getValue().getMaxWords().size() + "\n"); //+ ", " + item.getValue().getMaxWords().toString()+
        }

        input.close();
        output.close();
    }
}
