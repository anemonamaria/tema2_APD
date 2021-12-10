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
    int k;

    MapWorker(MapWorkPool mapWork, HashMap<String, Vector<MapDictionary>> dictionary, int k) {
        this.mapWork = mapWork;
        this.dictionary = dictionary;
        this.k = k;
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
//        System.out.println(task.getDimension() + "  worker " + k);

        byte[] fragments = new byte[task.getDimension()];
        file.read(fragments);
        fragment = new String(fragments);   // caractere din fragment
        //////////////////////////////////////
        // case before fragment
//        System.out.println(fragment + "  worker " + k);

        if( insideWord(fragment.charAt(0)) && task.offset > 0 ) {
            file.seek(task.offset-1);
            fragments = new byte[1];
            file.read(fragments);
            if(insideWord((char)fragments[0])) {
                int index = 1;
//                System.out.println(fragment);

                while(index < fragment.length() && insideWord(fragment.charAt(index))) {
                    index++;
                }
                fragment = fragment.substring(index);
//                System.out.println(fragment + " laaa");
            }
        }

        // de verificat aici
        //case after fragment
        if(fragment.length() > 0 && insideWord(fragment.charAt(fragment.length()-1))) {
            file.seek(task.offset + task.getDimension());
            fragments = new byte[50];
            if(file.read(fragments) > 0 ) ;
            String stringToAppend = new String(fragments);
            if(insideWord(stringToAppend.charAt(0))) {
                int index = 1;
//                System.out.println(fragment);

                while(insideWord(stringToAppend.charAt(index))) {
                    index ++;
                }
                fragment += stringToAppend.substring(0, index);
//                System.out.println(fragment);

            }
        }
        file.close();

        return fragment.trim();
    }

    public void processTask( MapTask task ) throws IOException {
        String fragment = readFragment(task);
        StringTokenizer token = new StringTokenizer(fragment, " ;:/?~\\.,><~`[]{}()!@#$%^&-_+'=*\"|\t\n\r");
        MapDictionary solutionOfTask = new MapDictionary(task.getCurrentFile().toString());
        int idx = 0;
        int offset = task.offset;
        while( token.hasMoreTokens() ) {
            String currentWord = token.nextToken();

            if( currentWord.length() > 0 ) {
//               System.out.println(offset + " " + currentWord + "  worker " + k);
                solutionOfTask.addWordInDic(currentWord, offset);
            }
            offset += currentWord.length();
            idx ++;
        }
        synchronized (dictionary) {
            Vector<MapDictionary> previousSolutions = dictionary.get(task.getCurrentFile().toString());
            previousSolutions.add(solutionOfTask);
            dictionary.put(task.getCurrentFile().toString(), previousSolutions);
        }
    }

    public void run () {
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
    }
    ////////////////////////////////////
}
