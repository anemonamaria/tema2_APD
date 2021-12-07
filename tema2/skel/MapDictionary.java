import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

//import static java.lang.Math.round;

public class MapDictionary {
    private HashMap<Integer, Integer> dictionary;  // lungime si numarul de aparitii
    private Vector<String> maxWords;
    private int maxValue;
    private String fileName;
    private double rang;

    MapDictionary(String fileName){
        this.dictionary = new HashMap<Integer, Integer>();
        this.maxWords = new Vector<String>();
        this.maxValue = 0;
        this.fileName = fileName;
        rang = 0;
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

    public void addMapDictionary(MapDictionary newdic) {
        if(maxWords.size() == 0) {
            maxWords.addAll(newdic.getMaxWords());
        } else if (newdic.getMaxWords().get(0).length() > maxWords.get(0).length()) {
            maxWords.clear();
            maxWords.addAll(newdic.getMaxWords());
        } else if (newdic.getMaxWords().get(0).length() == maxWords.get(0).length()) {
            for (int i = 0; i < newdic.getMaxWords().size(); i++) {
                if (!maxWords.contains(newdic.getMaxWords().get(i))) {
                    maxWords.add(newdic.getMaxWords().get(i));
                }
            }
        }

        for(Map.Entry<Integer, Integer> item : newdic.getDictionary().entrySet()){
            Integer prevValue = dictionary.get(item.getKey());
            if(prevValue == null) {
                dictionary.put(item.getKey(), item.getValue());
            } else {
                dictionary.put(item.getKey(), prevValue + item.getValue());
            }
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
                ", fileName='" + fileName + '\'' +
                ", rang=" + rang +
                '}';
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void calcRang(Vector<Integer> fib) {
        float sum = 0;
        int totalWords = 0;
        for(Map.Entry<Integer, Integer> item : dictionary.entrySet()) {
            sum = sum + fib.get(item.getKey() + 1) * item.getValue();
            totalWords += item.getValue();
        }

        DecimalFormat myFormat = new DecimalFormat(("#.##"));
        myFormat.setRoundingMode(RoundingMode.DOWN);
        rang = round(sum / totalWords, 2);
    }

    public double round(double value, int places) {
        if(places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.DOWN);
        return bd.doubleValue();
    }

    public double getRang() {
        return rang;
    }
}
