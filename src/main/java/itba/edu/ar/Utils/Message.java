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

    public int getIntFileSize() {
        return fileBytes.length;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public String getFileExtension() {
        return Tools.recoverNullTerminatedBytes(this.fileExtension);
    }

    public byte[] unpackMessage(){
        int size = this.fileSize.length + this.fileBytes.length + this.fileExtension.length + 1;
        byte[] toRet = new byte[size];
        int i= 0;

        for (byte b : fileSize) {
            toRet[i] = b;
            System.out.println(i);
            i++;
        }
        for (byte fileByte : fileBytes) {
            toRet[i] = fileByte;
            i++;
        }
        for (byte b : fileExtension) {
            toRet[i] = b;
            i++;
        }
        byte[] b = new byte[1];
        for (byte by : b) {
            toRet[i] = by;
            i++;
        }
        if (i > size)
            System.out.println("ERROR UNPACKMESSAGE");

        return toRet;
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

