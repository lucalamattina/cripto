package itba.edu.ar.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Tools {

    public static byte[] bigEndian(int size){
        ByteBuffer b = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
        b.putInt(size);
        return b.array();
    }
}
