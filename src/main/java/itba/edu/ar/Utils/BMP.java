package itba.edu.ar.Utils;

import java.lang.reflect.Array;
import java.util.Arrays;

public class BMP {
    private byte[] fileSize;
    private byte[] fileBytes;
    private byte[] fileExtension;

    public BMP(byte[] fileSize, byte[] fileBytes, byte[] fileExtension) {
        this.fileSize = fileSize;
        this.fileBytes = fileBytes;
        this.fileExtension = fileExtension;
    }

    public byte[] getFileSize() {
        return fileSize;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public byte[] getFileExtension() {
        return fileExtension;
    }
}

