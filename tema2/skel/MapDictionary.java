import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

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

    // adaug cuvinte in dictionar
    public void addWordInDic(String myWord, int i) {
        if(maxWords.size() == 0) {
            maxWords.add(myWord);
            indices.add(i);
            maxValue = myWord.length();
            dictionary.put(myWord.length(), 1);
        } else if (maxValue < myWord.length()) {
            dictionary.put(maxValue, maxWords.size());
            maxValue = myWord.length();
            maxWords.removeAllElements();
            maxWords.add(myWord);
            indices.removeAllElements();
            indices.add(i);
            dictionary.put(myWord.length(), maxWords.size());
        } else if (maxValue == myWord.length()) {
            maxWords.add(myWord);
            indices.add(i);
            dictionary.put(maxValue, maxWords.size());  // trece in3 cu -1  pica celelalte, fara aproape trece in2 dar nu e aproximat corect
        } else {
            // adaug la pozitia respectiva aparitia cuvantului
            dictionary.merge(myWord.length(), 1, Integer::sum);

        }
//        System.out.println("indice " + i +  " cuvant " + myWord);
    }
    // TODO VEZI CA AICI CE SE INTAMPLA
    public void addMapDictionary(MapDictionary newdic) {
        if(maxWords.size() == 0) {
            maxWords.addAll(newdic.getMaxWords());
            //indices.removeAllElements();
            indices.addAll(newdic.indices);
            maxValue = newdic.maxValue;
        } else if (newdic.maxValue > maxValue) {
            maxWords.removeAllElements();
            indices.removeAllElements();
            indices.addAll(newdic.indices);
            maxWords.addAll(newdic.getMaxWords());
            maxValue = newdic.maxValue;
        } else if (newdic.maxValue == maxValue) {
            for (int i = 0; i < newdic.getMaxWords().size(); i++) {
                if (!maxWords.contains(newdic.getMaxWords().get(i))) {
                    maxWords.add(newdic.getMaxWords().get(i));
                    indices.add(newdic.indices.get(i));
                }
                if (maxWords.contains(newdic.getMaxWords().get(i))&& !indices.contains(newdic.indices.get(i))) {  //
                    maxWords.add(newdic.getMaxWords().get(i));
                    indices.add(newdic.indices.get(i));
                }
            }
        }

        for(Map.Entry<Integer, Integer> item : newdic.getDictionary().entrySet()){
            if(dictionary.get(item.getKey()) == null) {
                if(item.getKey() == maxValue)
                    dictionary.put(maxValue, maxWords.size());
                else
                    dictionary.put(item.getKey(), item.getValue());
            } else {
                if(item.getKey() == maxValue)
                    dictionary.put(maxValue, maxWords.size());
                else
                    dictionary.put(item.getKey(), dictionary.get(item.getKey()) + item.getValue());   // trece in1 cu -1, pica celelalte
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

    public double getRang() {
        return rang;
    }

    public Vector<Integer> getIndices() {
        return indices;
    }
}
