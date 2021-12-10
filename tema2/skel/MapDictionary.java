import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class MapDictionary {
    final HashMap<Integer, Integer> dictionary;  // lungime si numarul de aparitii
    final Vector<String> maxWords;
    final Vector<Integer> indices;
    int maxValue;
    final String fileName;
    double rang;

    MapDictionary(String fileName){
        this.dictionary = new HashMap<>();
        this.maxWords = new Vector<>();
        this.indices = new Vector<>();
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
            dictionary.put(maxValue, 1);
        } else if (maxValue < myWord.length()) {
            dictionary.put(maxValue, maxWords.size());
            maxValue = myWord.length();
            maxWords.removeAllElements();
            maxWords.add(myWord);
            indices.removeAllElements();
            indices.add(i);
            dictionary.put(maxValue, maxWords.size());
        } else if (maxValue == myWord.length()) {
            maxWords.add(myWord);
            indices.add(i);
            dictionary.put(maxValue, maxWords.size());
        } else {
            // adaug la pozitia respectiva aparitia cuvantului
            dictionary.merge(myWord.length(), 1, Integer::sum);
        }
    }

    public void addMapDictionary(MapDictionary newDictionary) {
        if(maxWords.size() == 0) {
            maxWords.addAll(newDictionary.maxWords);
            indices.addAll(newDictionary.indices);
            maxValue = newDictionary.maxValue;
        } else if (newDictionary.maxValue > maxValue) {
            maxWords.removeAllElements();
            indices.removeAllElements();
            indices.addAll(newDictionary.indices);
            maxWords.addAll(newDictionary.maxWords);
            maxValue = newDictionary.maxValue;
        } else if (newDictionary.maxValue == maxValue) {
            for (int i = 0; i < newDictionary.maxWords.size(); i++) {
                if (!maxWords.contains(newDictionary.maxWords.get(i))) {
                    maxWords.add(newDictionary.maxWords.get(i));
                    indices.add(newDictionary.indices.get(i));
                }
                if (maxWords.contains(newDictionary.maxWords.get(i))&& !indices.contains(newDictionary.indices.get(i))) {
                    maxWords.add(newDictionary.maxWords.get(i));
                    indices.add(newDictionary.indices.get(i));
                }
            }
        }

        for(Map.Entry<Integer, Integer> item : newDictionary.dictionary.entrySet()){
            if(dictionary.get(item.getKey()) == null) {
                if(item.getKey() == maxValue)
                    dictionary.put(maxValue, maxWords.size());
                else
                    dictionary.put(item.getKey(), item.getValue());
            } else {
                if(item.getKey() == maxValue)
                    dictionary.put(maxValue, maxWords.size());
                else
                    dictionary.put(item.getKey(), dictionary.get(item.getKey()) + item.getValue());
            }
        }
    }
}
