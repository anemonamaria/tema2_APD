import java.io.FileReader;

public class MapTask {
    FileReader currentFileName;
    int offset;  // index
    int dimension;  // offset dim


    MapTask(FileReader currentFile, int offset, int dimension) {
        this.currentFileName = currentFile;
        this.offset = offset;
        this.dimension = dimension;
    }

    public FileReader getCurrentFile() {
        return currentFileName;
    }

    public int getDimension() {
        return dimension;
    }

    public int getOffset() {
        return offset;
    }

    public void setCurrentFile(FileReader currentFile) {
        this.currentFileName = currentFile;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void setOffset(int offset) {
        this.offset = offset;
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
