package itba.edu.ar.bmp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BitMapInfoHeader {
    static final int SIZE = 40;
    private final int biSize;
    private final int biWidth;
    private final int biHeight;
    private final short biPlanes;
    private final short biBitCount;
    private final int biCompression;
    private final int biSizeImage;
    private final int biXPelsPerMeter;
    private final int biYPelsPerMeter;
    private final int biClrUsed;
    private final int biClrImportant;

    private BitMapInfoHeader(ByteBuffer buf) {
        this.biSize = buf.getInt();
        this.biWidth = buf.getInt();
        this.biHeight = buf.getInt();
        this.biPlanes = buf.getShort();
        this.biBitCount = buf.getShort();
        this.biCompression = buf.getInt();
        this.biSizeImage = buf.getInt();
        this.biXPelsPerMeter = buf.getInt();
        this.biYPelsPerMeter = buf.getInt();
        this.biClrUsed = buf.getInt();
        this.biClrImportant = buf.getInt();
    }

    public short getBiBitCount() {
        return biBitCount;
    }

    public int getBiCompression() {
        return biCompression;
    }

    public int getBiSizeImage() {
        return biSizeImage;
    }

    public static BitMapInfoHeader read(ByteBuffer buf) {
        return new BitMapInfoHeader(buf);
    }

    public static void write(BitMapInfoHeader header, ByteBuffer buf) {
        buf.putInt(header.biSize);
        buf.putInt(header.biWidth);
        buf.putInt(header.biHeight);
        buf.putShort(header.biPlanes);
        buf.putShort(header.biBitCount);
        buf.putInt(header.biCompression);
        buf.putInt(header.biSizeImage);
        buf.putInt(header.biXPelsPerMeter);
        buf.putInt(header.biYPelsPerMeter);
        buf.putInt(header.biClrUsed);
        buf.putInt(header.biClrImportant);
    }
}
