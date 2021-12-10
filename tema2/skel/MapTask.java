import java.io.FileReader;

public class MapTask {
    String currentFileName;
    int offset;  // index
    int dimension;  // offset dim


    MapTask(String currentFile, int offset, int dimension) {
        this.currentFileName = currentFile;
        this.offset = offset;
        this.dimension = dimension;
    }

    public String getCurrentFile() {
        return currentFileName;
    }

    public int getDimension() {
        return dimension;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return "MapTask{" +
                "currentFile=" + currentFileName +
                ", offset=" + offset +
                ", dimension=" + dimension +
                + '\'' + '}';
    }
}
