import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

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

public class MapWorker extends Thread{
    private final WorkPool mapWork;
    private final HashMap<String, Vector<MapDictionary>> dictionary;  // lungimea si numarul aparitiilor

    MapWorker(WorkPool mapWork, HashMap<String, Vector<MapDictionary>> dictionary) {
        this.mapWork = mapWork;
        this.dictionary = dictionary;
    }

    public boolean separator( char letter ) {
        if (letter >= 'A') if (letter <= 'Z') return true;
        if (letter >= '0') if (letter <= '9') return true;
        if (letter >= 'a') if (letter <= 'z') return true;
        return false;
    }

    public String readFragment(MapTask task) throws IOException {
        RandomAccessFile file = new RandomAccessFile(task.currentFileName, "r");
        file.seek(task.offset);

        byte[] fragments = new byte[task.offsetDimension];
        file.read(fragments);
        String fragment = new String(fragments);
        // inainte de fragment
        if (separator(fragment.charAt(0)))
            if (task.offset > 0) {
                file.seek(task.offset - 1);
                fragments = new byte[1];
                file.read(fragments);
                if (separator((char) fragments[0])) {
                    int index = 0;
                    do
                        index++;  // numaram cate caractere exista inainte de fragment
                    while (index < fragment.length() && separator(fragment.charAt(index)));
                    fragment = fragment.substring(index);
                }
            }

        //dupa fragment
        if(fragment.length() > 0) {
            if (separator(fragment.charAt(fragment.length()-1))) {
                file.seek(task.offset + task.offsetDimension);
                fragments = new byte[task.offsetDimension + 1];
                file.read(fragments);
                String addToFragment = new String(fragments);
                if (separator(addToFragment.charAt(0))) {
                    int index = 0;
                    do
                        index++; // numaram cate caractere exista dupa fragment
                    while (separator(addToFragment.charAt(index)));
                    fragment = fragment + addToFragment.substring(0, index);
                }
            }
        }
        file.close();

        return fragment.trim();
    }

    public void processTask( MapTask task ) throws IOException {
        StringTokenizer token = new StringTokenizer(readFragment(task), " ;:/?~\\.,><`[]{}()!@#$%^&-_+'=*\"|\t\n\r");
        MapDictionary processedTask = new MapDictionary(task.currentFileName);
        int offset = task.offset;
        while (token.hasMoreTokens()) {
            String word = token.nextToken();

            if (word.length() > 0) {
                processedTask.addWordInDic(word, offset);  // adaugam in dictionar detaliile despre cuvantul curent
            }
            offset = offset + word.length();
        }
        synchronized (dictionary) {
            Vector<MapDictionary> dicSol = dictionary.get(task.currentFileName);
            dicSol.add(processedTask);
            dictionary.put(task.currentFileName, dicSol);
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

