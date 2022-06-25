package itba.edu.ar.Utils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class Tools {

    public static byte[] makeBigEndian(int size){
        ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(size);

        return buffer.array();
    }
    public static int recoverBigEndianBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
        buffer.put(bytes);
        ((Buffer)buffer).rewind();
        return buffer.getInt();
    }

    public static byte[] makeNullTerminatedBytes(String str) {
        byte[] stringBytes = str.getBytes(StandardCharsets.ISO_8859_1);
        byte[] nullTerminatedBytes = new byte[stringBytes.length + 1];
        System.arraycopy(stringBytes, 0, nullTerminatedBytes, 0, stringBytes.length);

        return nullTerminatedBytes;
    }

    public static String recoverNullTerminatedBytes(byte[] nullTerminatedBytes) {

        for (int i = 0; i < nullTerminatedBytes.length;i++) {
            if(nullTerminatedBytes[i] == '\0') {
                return new String(nullTerminatedBytes, 0, i, StandardCharsets.ISO_8859_1);
            }
        }
        return new String(nullTerminatedBytes, 0, nullTerminatedBytes.length, StandardCharsets.ISO_8859_1);
    }

    public static String hexStringFromBytes(byte[] b) {
        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder hex = new StringBuilder();
        int msb;
        int lsb;
        int j;
        for (j = 0; j < b.length; j++) {
            msb = ((int) b[j] & 0x000000FF) / 16;
            lsb = ((int) b[j] & 0x000000FF) % 16;
            hex.append(hexChars[msb]).append(hexChars[lsb]);
        }
        return (hex.toString());
    }
}
