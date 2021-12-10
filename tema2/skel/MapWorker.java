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

class MapWorkPool {
    Vector<MapTask> mapTasks;
    int w8Workers = 0;
    int workers;
    int ready;

    MapWorkPool(int nrOfThreads) {
        this.mapTasks = new Vector<>();
        this.workers = nrOfThreads;
        this.ready = 0;
    }

    synchronized void addWork(MapTask mapTask) {
        mapTasks.add(mapTask);
        this.notify();
    }

    public synchronized MapTask getWork(){
        if(mapTasks.size() == 0) {
            w8Workers++;
            if(w8Workers == workers) {
                ready = 1;
                notifyAll();
                return null;
            } else {
                while (ready == 0 && mapTasks.isEmpty()) {
                    try {
                        this.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if(ready == 1)
                    return null;
                else w8Workers --;
            }
        }
        return mapTasks.remove(0);
    }
}

public class MapWorker extends Thread{
    private final MapWorkPool mapWork;
    private final HashMap<String, Vector<MapDictionary>> dictionary;  // lungimea si numarul aparitiilor

    MapWorker(MapWorkPool mapWork, HashMap<String, Vector<MapDictionary>> dictionary) {
        this.mapWork = mapWork;
        this.dictionary = dictionary;
    }

    public boolean insideWord( char letter ) {
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
        if (insideWord(fragment.charAt(0)))
            if (task.offset > 0) {
                file.seek(task.offset - 1);
                fragments = new byte[1];
                file.read(fragments);
                if (insideWord((char) fragments[0])) {
                    int index = 0;
                    do
                        index++;
                    while (index < fragment.length() && insideWord(fragment.charAt(index)));
                    fragment = fragment.substring(index);
                }
            }

        //dupa fragment
        if(fragment.length() > 0) {
            if (insideWord(fragment.charAt(fragment.length()-1))) {
                file.seek(task.offset + task.offsetDimension);
                fragments = new byte[50];
                file.read(fragments);
                String stringToAppend = new String(fragments);
                if (insideWord(stringToAppend.charAt(0))) {
                    int index = 0;
                    do
                        index++;
                    while (insideWord(stringToAppend.charAt(index)));
                    fragment = fragment + stringToAppend.substring(0, index);
                }
            }
        }
        file.close();

        return fragment.trim();
    }

    public void processTask( MapTask task ) throws IOException {
        StringTokenizer token = new StringTokenizer(readFragment(task), " ;:/?~\\.,><`[]{}()!@#$%^&-_+'=*\"|\t\n\r");
        MapDictionary solutionOfTask = new MapDictionary(task.currentFileName);
        int offset = task.offset;
        while( token.hasMoreTokens() ) {
            String currentWord = token.nextToken();

            if( currentWord.length() > 0 ) {
                solutionOfTask.addWordInDic(currentWord, offset);
            }
            offset = offset + currentWord.length();
        }
        synchronized (dictionary) {
            Vector<MapDictionary> previousSolutions = dictionary.get(task.currentFileName);
            previousSolutions.add(solutionOfTask);
            dictionary.put(task.currentFileName, previousSolutions);
        }
    }

    public void run () {
        while(true) {
            MapTask task = mapWork.getWork();
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

