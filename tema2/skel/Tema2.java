import java.io.*;
import java.util.*;

class MyMap {
    FileReader currentFile;
    int offset;
    int dimension;
    int givenDim;
    String words;

    MyMap(FileReader currentFile, int offset, int dimension, String words, int givenDim) {
        this.currentFile = currentFile;
        this.offset = offset;
        this.dimension = dimension;
        this.words = words;
        this.givenDim = givenDim;
    }
}

class WordsFromFileClass {
    String word;
    int dim;
    FileReader currentFile;
    String mainString;

    WordsFromFileClass(String word, int dim, FileReader currentFile, String mainString) {
        this.currentFile = currentFile;
        this.dim = dim;
        this.word = word;
        this.mainString = mainString;
    }
}

public class Tema2 {

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }

        // citesc din linia de comanda
        int workers = Integer.parseInt(args[0]);
        BufferedReader input = new BufferedReader(new FileReader(args[1]));
        BufferedWriter output = new BufferedWriter(new FileWriter(args[2]));

        // citesc din fisierul de input
        int dimOcteti;
        int nrDocs;
        Vector<FileReader> files = new Vector<FileReader>();

        dimOcteti = Integer.parseInt(input.readLine());
        nrDocs = Integer.parseInt(input.readLine());
        String aux;
        int i = 0;
        while ( (aux = input.readLine()) != null) {
            files.add(i, new FileReader(aux));
            i++;
        }

        //citire cuvinte din fisiere
        Vector<Vector<WordsFromFileClass>> wordsFromFile = new Vector<>();
        String delimitatori = new String(";:/?˜.,><‘[]{\\}()!@#$%ˆ&- +’=*”\n\r\t");
//        Vector<Vector<MyMap>> mapVector =

        int j;
        for (i = 0; i < nrDocs; i++) {
            j = 0;
            String mainString = new BufferedReader(files.get(i)).readLine();
            StringTokenizer auxiliary = new StringTokenizer(mainString, delimitatori);
            Vector<WordsFromFileClass> myVect = new Vector<>();
            while(auxiliary.hasMoreTokens()) {
                String word = auxiliary.nextToken();
                myVect.add(j, new WordsFromFileClass(word, word.length(), files.get(i), mainString));
                j++;
            }
            wordsFromFile.add(i, myVect);

//            for (int k = 0; k < wordsFromFile.get(i).size(); k++) {
//                System.out.println(wordsFromFile.get(i).get(k).word+ " " + wordsFromFile.get(i).get(k).dim + " " +
//                        wordsFromFile.get(i).get(k).currentFile) ;
//            }
//            System.out.println();
        }

        input.close();
        output.close();
    }
}
