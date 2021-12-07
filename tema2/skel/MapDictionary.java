import java.util.HashMap;
import java.util.Vector;

public class MapDictionary {
    private HashMap<Integer, Integer> dictionary;  // lungime si numarul de aparitii
    private Vector<String> maxWords;
    private int maxValue;
    private String fileName;

    MapDictionary(String fileName){
        this.dictionary = new HashMap<Integer, Integer>();
        this.maxWords = new Vector<String>();
        this.maxValue = 0;
        this.fileName = fileName;
    }

    MapDictionary(HashMap<Integer, Integer> dictionary, Vector<String> maxWords, int maxValue, String fileName) {
        this.dictionary = dictionary;
        this.maxWords = maxWords;
        this.maxValue = maxValue;
        this.fileName = fileName;
    }

    // adaug cuvinte in dictionar
    public void addWordInDic(String myWord) {
        if (maxValue < myWord.length()) {
            maxValue = myWord.length();
            maxWords.removeAllElements();
            maxWords.add(myWord);
            dictionary.put(myWord.length(), 1);
        } else if (maxValue == myWord.length()) {
            maxWords.add(myWord);
            dictionary.put(myWord.length(), maxWords.size());
        } else {
            // adaug la pozitia respectiva aparitia cuvantului
            if (dictionary.get(myWord.length()) != null)
                dictionary.put(myWord.length(), dictionary.get(myWord.length()) + 1);
            else
                dictionary.put(myWord.length(), 1);

        }
    }

    public Vector<String> getMaxWords() {
        return maxWords;
    }

    public HashMap<Integer, Integer> getDictionary() {
        return dictionary;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return "MapDictionary{" +
                "dictionary=" + dictionary +
                ", maxWords=" + maxWords +
                ", maxValue=" + maxValue +
                '}';
    }

    public int getMaxValue() {
        return maxValue;
    }
}
