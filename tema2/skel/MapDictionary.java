import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

//import static java.lang.Math.round;
class WordsAndIndex {
    public String word;
    public int index;

    WordsAndIndex(String word, int index) {
        this.word = word;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getWord() {
        return word;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setWord(String word) {
        this.word = word;
    }
}


public class MapDictionary {
    private HashMap<Integer, Integer> dictionary;  // lungime si numarul de aparitii
    private Vector<String> maxWords;
    private Vector<Integer> indices;
    private int maxValue;
    private String fileName;
    private double rang;

    MapDictionary(String fileName){
        this.dictionary = new HashMap<Integer, Integer>();
        this.maxWords = new Vector<String>();
        this.indices = new Vector<Integer>();
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
    public void addWordInDic(String myWord, int i) {
        if (maxValue < myWord.length()) {
            maxValue = myWord.length();
            maxWords.removeAllElements();
            maxWords.add(myWord);
            indices.add(i);
            dictionary.put(myWord.length(), maxWords.size());
        } else if (maxValue == myWord.length()) {
            maxWords.add(myWord);
            dictionary.put(myWord.length(), maxWords.size());  // trece in3 cu -1  pica celelalte, fara aproape trece in2 dar nu e aproximat corect
        } else {
            // adaug la pozitia respectiva aparitia cuvantului
            dictionary.merge(myWord.length(), 1, Integer::sum);

        }
    }
    // TODO VEZI CA AICI NU SE ADAUGA OK NUMARUL DE APARITII PENTRU CUVINTELE MAXIMALE... CUMVA SUNT MAI MULTE APARITII DECAT EXISTA
    // de aici porneste greseala in calcularea rangului
    public int addMapDictionary(MapDictionary newdic) {
        if(maxWords.size() == 0 && !newdic.getMaxWords().isEmpty()) {
            maxWords.addAll(newdic.getMaxWords());
            indices.removeAllElements();
            indices.addAll(newdic.indices);
            maxValue = newdic.maxValue;
        } else if (!newdic.getMaxWords().isEmpty() && !maxWords.isEmpty() && newdic.maxValue > maxValue) {
            maxWords.removeAllElements();
            indices.removeAllElements();
            indices.addAll(newdic.indices);
            maxWords.addAll(newdic.getMaxWords());
            maxValue = newdic.maxValue;
        } else if (!newdic.getMaxWords().isEmpty() && !maxWords.isEmpty() && newdic.maxValue == maxValue) {
            for (int i = 0; i < newdic.getMaxWords().size(); i++) {
                if (!maxWords.contains(newdic.getMaxWords().get(i))) {
                    maxWords.add(newdic.getMaxWords().get(i));
                    indices.add(i);
                }
                if (maxWords.contains(newdic.getMaxWords().get(i)) && !indices.contains(i)) {
                    maxWords.add(newdic.getMaxWords().get(i));
                    indices.add(i);
                }
            }
        } else if (maxWords.isEmpty() && !newdic.getMaxWords().isEmpty()) {
            maxWords = new Vector<>();
            indices.removeAllElements();
            indices.addAll(newdic.indices);
            maxWords.addAll(newdic.getMaxWords());
            maxValue = newdic.getMaxWords().get(0).length();
        }

        for(Map.Entry<Integer, Integer> item : newdic.getDictionary().entrySet()){
            Integer prevValue = dictionary.get(item.getKey());
//            if(newdic.fileName.equals("tests/files/in3"))
//                 System.out.println(prevValue + " prevvalue");

            if(prevValue == null) {
                dictionary.put(item.getKey(), item.getValue());
            } else {
                dictionary.put(item.getKey(), prevValue + item.getValue());   // trece in1 cu -1, pica celelalte
            }
        }
        return maxValue;
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

    public void setRang(double rang) {
        this.rang = rang;
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
