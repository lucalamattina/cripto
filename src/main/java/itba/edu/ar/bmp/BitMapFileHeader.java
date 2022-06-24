package itba.edu.ar.bmp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BitMapFileHeader {
    static final int SIZE = 14;
    private final short bfType;
    private final int bfSize;
    private final short bfReserved1;
    private final short bfReserved2;
    private final int bfOffBits;

    private BitMapFileHeader(ByteBuffer buf) {
        this.bfType = buf.getShort();
        this.bfSize = buf.getInt();
        this.bfReserved1 = buf.getShort();
        this.bfReserved2 = buf.getShort();
        this.bfOffBits = buf.getInt();
    }
    public short getBfType() {
        return bfType;
    }
    public int getBfSize() {
        return bfSize;
    }
    public int getBfOffBits() {
        return bfOffBits;
    }
    public static BitMapFileHeader read(ByteBuffer buf) {
        return new BitMapFileHeader(buf);
    }
    public static void write(BitMapFileHeader header, ByteBuffer buf) {
        buf.putShort(header.bfType);
        buf.putInt(header.bfSize);
        buf.putShort(header.bfReserved1);
        buf.putShort(header.bfReserved2);
        buf.putInt(header.bfOffBits);
    }
}
