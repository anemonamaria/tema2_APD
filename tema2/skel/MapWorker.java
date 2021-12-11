import java.io.IOException;
import java.util.Vector;
import java.util.StringTokenizer;
import java.io.RandomAccessFile;
import java.util.HashMap;



class MapTask {
    String currentFileName;
    int offset;
    int offsetDimension;

    MapTask(String currentFile, int offset, int dimension) {
        this.currentFileName = currentFile;
        this.offset = offset;
        this.offsetDimension = dimension;
    }
}

public class MapWorker extends Thread {
    private final HashMap<String, Vector<MapDictionary>> dictionary;  // lungimea si numarul aparitiilor
    private final WorkPool mapWork;

    MapWorker(HashMap<String, Vector<MapDictionary>> dictionary, WorkPool mapWork) {
        this.dictionary = dictionary;
        this.mapWork = mapWork;
    }

    public int separator(char letter) {
        if (letter >= 'A') if (letter <= 'Z') return 1;
        if (letter >= '0') if (letter <= '9') return 1;
        if (letter >= 'a') if (letter <= 'z') return 1;
        return 0;
    }

    public String processFrag(MapTask task) throws IOException {
        RandomAccessFile file = new RandomAccessFile(task.currentFileName, "r");
        file.seek(task.offset);
        byte[] toProcess = new byte[task.offsetDimension];
        file.read(toProcess);
        int index;
        StringBuilder processed = new StringBuilder(new String(toProcess));
        // inainte de fragment
        char currentCharacter = processed.charAt(0);
        if (task.offset - 1 >= 0)
            if (separator(currentCharacter) == 1) {
                file.seek(task.offset - 1);
                toProcess = new byte[1];
                file.read(toProcess);
                index = 0;
                if (separator((char) toProcess[0]) == 1)
                    do {
                        index++;  // numaram cate caractere exista inainte de fragment
                        if(index >= processed.length())
                            break;
                        currentCharacter = processed.charAt(index);
                    } while (separator(currentCharacter) == 1);
                processed = new StringBuilder(processed.substring(index));
            }

        //dupa fragment
        if(processed.length() - 1 >= 0)
            currentCharacter = processed.charAt(processed.length()-1);
            if (separator(currentCharacter) == 1) {
                file.seek(task.offset + task.offsetDimension);
                toProcess = new byte[task.offsetDimension + 1];
                file.read(toProcess);
                String addToFragment = new String(toProcess);
                currentCharacter = addToFragment.charAt(0);
                index = 0;
                if (separator(currentCharacter) == 1)
                    do {
                        index++; // numaram cate caractere exista dupa fragment
                        processed.append(currentCharacter);
                        currentCharacter = addToFragment.charAt(index);
                    } while (separator(currentCharacter) == 1);
            }
        file.close();

        return processed.toString();
    }

    public void processTask(MapTask task) throws IOException {
        String delimitatori = " ;:/?~\\.,><`[]{}()!@#$%^&-_+'=*\"|\t\n\r";
        StringTokenizer token = new StringTokenizer(processFrag(task), delimitatori);
        MapDictionary processedTask = new MapDictionary(task.currentFileName);
        int offset = task.offset;
        while (true) {
            if(token.hasMoreTokens()) {
                String word = token.nextToken();
                processedTask.addWordInDic(word, offset);  // adaugam in dictionar detaliile despre cuvantul curent
                offset = offset + word.length();
            } else
                break;
        }
        synchronized (dictionary) {
            String name = task.currentFileName;
            Vector<MapDictionary> dicSol = dictionary.get(name);
            dicSol.add(processedTask);
            dictionary.put(name, dicSol);
        }
    }

    public void run() {
        while(true) {
            MapTask task = mapWork.getMapWork();
            if(task == null)
                break;
            try {
                processTask(task);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

