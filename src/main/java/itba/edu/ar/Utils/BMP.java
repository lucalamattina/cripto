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

    public byte[] toByteArray() {
        int length = fileSize.length + fileBytes.length + fileExtension.length;
        byte[] str = new byte[length];

        System.arraycopy(fileSize, 0, str, 0, fileSize.length);
        System.arraycopy(fileBytes, 0, str, fileSize.length, fileBytes.length);
        System.arraycopy(fileExtension, 0, str, fileSize.length + fileBytes.length, fileExtension.length);

        return str;
    }
}

