import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

public class MapWorker extends Thread{
    private MapWorkPool mapWork;
    private HashMap<String, Vector<MapDictionary>> dictionary;  // lungime si numarul aparitiilor

    MapWorker(MapWorkPool mapWork, HashMap<String, Vector<MapDictionary>> dictionary) {
        this.mapWork = mapWork;
        this.dictionary = dictionary;
    }
    //////////////////////////
    public boolean insideWord( char letter ) {
        return (letter >= 'A' && letter <= 'Z') || (letter >= '0' && letter <= '9')
                || (letter >= 'a' && letter <= 'z');
    }
    //////////////////////////////////////////////
    public String readFragment(MapTask task) throws IOException {
        String fragment;
        RandomAccessFile file = new RandomAccessFile(task.getCurrentFile(), "r");
        file.seek(task.getOffset());
        byte[] fragments = new byte[task.getDimension()];
        file.read(fragments);
        fragment = new String(fragments);   // caractere din fragment
        //////////////////////////////////////
        // case before fragment
        if( insideWord(fragment.charAt(0)) && task.offset > 0 ) {
            file.seek(task.offset-1);
            fragments = new byte[1];
            file.read( fragments );
            if( insideWord( (char)fragments[0] ) ) {
                int index = 1;
                while( insideWord( fragment.charAt( index ) ) ) {
                    index ++;
                }
                fragment = fragment.substring( index );
            }
        }

        // de verificat aici
        //case after fragment
        if( insideWord(fragment.charAt(fragment.length()-1)) ) {
            file.seek(task.offset + task.getDimension());
            fragments = new byte[50];
            if( file.read(fragments) > 0  ) ;
            String stringToAppend = new String(fragments);
            if( insideWord(stringToAppend.charAt(0)) ) {
                int index = 1;
                while( insideWord( stringToAppend.charAt(index) ) ) {
                    index ++;
                }
                fragment += stringToAppend.substring(0, index);
            }
        }
        // TODO nu cred ca e bine pus aici dictionarul
        //dictionary.put(fragment.length(), dictionary.get(fragment.length()) + 1); // adaug in dictionar lungimea gasita
        file.close();

        return fragment.trim();
    }

    public void processTask( MapTask task ) throws IOException {
        String fragment = readFragment(task);
        StringTokenizer token = new StringTokenizer(fragment, " ;:/?~\\.,><~`[]{}()!@#$%^&-_+'=*\"|\t\n\r");
        MapDictionary solutionOfTask = new MapDictionary(task.getCurrentFile().toString());
        while( token.hasMoreTokens() ) {
            String currentWord = token.nextToken();
            if( currentWord.length() > 0 ) {
                solutionOfTask.addWordInDic(currentWord);
            }
        }
        synchronized (dictionary) {
            Vector<MapDictionary> previousSolutions = dictionary.get(task.getCurrentFile().toString());
            previousSolutions.add(solutionOfTask);
            dictionary.put(task.getCurrentFile().toString(), previousSolutions);
        }
    }

    public void run () {
        // System.out.println("Thread-ul worker " + this.getName() + " a pornit...");
        while(true) {
            MapTask task = mapWork.getWork();
            if( task == null )
                break;
            try {
                processTask(task);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("Thread-ul worker " + this.getName() + " s-a terminat...");
    }
    ////////////////////////////////////
}
