import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

class WordsFromFileClass {
    String word;
    int dim;
    FileReader currentFile;
    String mainString;

    WordsFromFileClass(String word, FileReader currentFile, String mainString) {
        this.currentFile = currentFile;
        this.dim = word.length();
        this.word = word;
        this.mainString = mainString;
    }
}

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

        Vector<Vector<WordsFromFileClass>> wordsFromFile = new Vector<>();
        MapWorkPool mapWork = new MapWorkPool(workers);//workers
        ReduceWorkPool reduceWork = new ReduceWorkPool(workers);
        HashMap<String, Vector<MapDictionary>> fragmentsTask = new HashMap<String, Vector<MapDictionary>>();
        HashMap<String, MapDictionary> dictionaryRes = new HashMap<String, MapDictionary>();
        Vector<MapWorker> mapWorkers = new Vector<MapWorker>();
        Vector<ReduceWorker> reduceWorkers = new Vector<>();
        String separators = ";:/?˜\\.,><‘\\[]\\{}\\(\\)!@#$%ˆ&-'+’=*”|\" \t\n\r\0";
        String delimitatori = new String(separators);
        int j;

        Vector<Integer> fibonacci = new Vector<Integer>();

        //citire cuvinte din fisiere
        for (i = 0; i < nrDocs; i++) {
            j = 0;
            fragmentsTask.put((files.get(i)).toString(), new Vector<MapDictionary>());
            dictionaryRes.put((files.get(i)).toString(),  new MapDictionary((files.get(i)).toString()));
            String mainString = new BufferedReader(new FileReader(files.get(i))).readLine();
            StringTokenizer auxiliary = new StringTokenizer(mainString, delimitatori);
            Vector<WordsFromFileClass> myVect = new Vector<>();
            while(auxiliary.hasMoreTokens()) {
                String word = auxiliary.nextToken();
                myVect.add(j, new WordsFromFileClass(word, new FileReader(files.get(i)), mainString));  // prop sparte in tokeni
                j++;
            }
            wordsFromFile.add(i, myVect);

            int offsetStart = 0;
            int finishOffset = offset;
            int k = 0;
            File auxFile = new File(files.get(i));

            while(offsetStart < auxFile.length()){
                // sparge continutul fisierului in fragmente si cream task-urile de tip MAP
                if(finishOffset < auxFile.length()) {
                    mapWork.addWork(new MapTask(files.get(i).toString(), offsetStart, finishOffset));
                } else {
                    mapWork.addWork(new MapTask(files.get(i).toString(), offsetStart, (int) (auxFile.length() - offsetStart)));
                }
                offsetStart += offset;
                finishOffset += offset;
            }
        }

        for(int k = 0; k < workers; k++) {
            MapWorker workerMap = new MapWorker(mapWork, fragmentsTask);
            mapWorkers.add(workerMap);
            workerMap.start();
        }
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
        Collections.sort(finalResults, new Comparator<Map.Entry<String, MapDictionary>>() {
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
