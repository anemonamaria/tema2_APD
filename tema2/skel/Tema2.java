import java.io.*;
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
        HashMap<String, Vector<MapDictionary>> fragmentsTask = new HashMap<String, Vector<MapDictionary>>();
        Vector<MapWorker> mapWorkers = new Vector<MapWorker>();
        String separators = ";:/?~\\.,><`[]{}()!@#$%^&-_+'=*\"| \t\r\n";
        String delimitatori = new String(separators);
        int j;

        //citire cuvinte din fisiere
        for (i = 0; i < nrDocs; i++) {
            j = 0;
            fragmentsTask.put((files.get(i)).toString(), new Vector<MapDictionary>());
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

        for(i = 0; i < workers; i++) {
            MapWorker workerMap = new MapWorker(mapWork, fragmentsTask);
            mapWorkers.add(workerMap);
            workerMap.start();
        }
        for( i = 0; i < workers; i++ ) {
            mapWorkers.get(i).join();
        }


        input.close();
        output.close();
    }
}
