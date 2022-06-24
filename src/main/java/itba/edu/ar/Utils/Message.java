package itba.edu.ar.Utils;

public class Message {
    private byte[] fileSize;
    private byte[] fileBytes;
    private byte[] fileExtension;

    public Message(byte[] fileBytes, byte[] fileSize, byte[] fileExtension) {
        this.fileBytes = fileBytes;
        this.fileSize = fileSize;
        this.fileExtension = fileExtension;
    }

    public byte[] getFileSize() {
        return fileSize;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public String getFileExtension() {
        return Tools.recoverNullTerminatedBytes(this.fileExtension);
    }

    public byte[] makeByteArray() {
        int length = fileSize.length + fileBytes.length + fileExtension.length;
        byte[] str = new byte[length];

        System.arraycopy(fileSize, 0, str, 0, fileSize.length);
        System.arraycopy(fileBytes, 0, str, fileSize.length, fileBytes.length);
        System.arraycopy(fileExtension, 0, str, fileSize.length + fileBytes.length, fileExtension.length);

        return str;
    }
}

