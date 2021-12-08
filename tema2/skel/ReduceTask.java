import java.util.Vector;

public class ReduceTask {
    private String fileName;
    private Vector<MapDictionary> mapResults;
    private Vector<String> words;

    ReduceTask(String fileName, Vector<MapDictionary> mapResults) {
        this.fileName = fileName;
        this.mapResults = mapResults;
    }

    public Vector<MapDictionary> getMapResults() {
        return mapResults;
    }

    public String getFileName() {
        return fileName;
    }
}
