package itba.edu.ar.bmp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BitMapInfo {
    private final BitMapInfoHeader bmiHeader;

    private BitMapInfo(ByteBuffer buf) {
        this.bmiHeader = BitMapInfoHeader.read(buf);
    }

    public BitMapInfoHeader getBmiHeader() {
        return bmiHeader;
    }

    public static BitMapInfo read(ByteBuffer buf) {
        return new BitMapInfo(buf);
    }

    public static void write(BitMapInfo header, ByteBuffer buf) {
        BitMapInfoHeader.write(header.bmiHeader, buf);
    }
}
