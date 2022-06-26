package itba.edu.ar.bmp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Bmp {

    private final BitMapFileHeader fileHeader;
    private final BitMapInfo infoHeader;
    private byte[] pixelData;

    private Bmp(byte[] bytes) {
        ByteBuffer buf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN); // bmp usa little endian
        this.fileHeader = BitMapFileHeader.read(buf);
        this.infoHeader = BitMapInfo.read(buf);

        if (fileHeader.getBfType() != 19778) {
            throw new InvalidBmpFile("The file type must be BM");
        }
        if (fileHeader.getBfSize() != bytes.length) {
            throw new InvalidBmpFile("The size, in bytes, of the .bmp file does not match the size specified in fileHeader");
        }
        if (fileHeader.getBfSize() - fileHeader.getBfOffBits() != infoHeader.getBmiHeader().getBiSizeImage()) {
            throw new InvalidBmpFile("The header offset specified in fileHeader of the .bmp file is invalid");
        }
        if (infoHeader.getBmiHeader().getBiBitCount() != 24) {
            throw new InvalidBmpFile("The .bmp file must match 24 bits-per-pixel.");
        }
        if (infoHeader.getBmiHeader().getBiCompression() != 0) {
            throw new InvalidBmpFile("The .bmp file should not be compressed");
        }
        int pixelDataSize = bytes.length - BitMapFileHeader.SIZE - BitMapInfoHeader.SIZE;
        this.pixelData = new byte[pixelDataSize];
        buf.get(this.pixelData);
    }

    private Bmp(BitMapFileHeader fileHeader, BitMapInfo infoHeader, byte[] pixelData) {
        this.fileHeader = fileHeader;
        this.infoHeader = infoHeader;
        this.pixelData = pixelData;
    }

    public BitMapFileHeader getFileHeader() {
        return fileHeader;
    }
    public BitMapInfo getInfoHeader() {
        return infoHeader;
    }
    public byte[] getPixelData() {
        return Arrays.copyOf(pixelData, pixelData.length);
    }


    public static Bmp read(String pathToFile) throws IOException {
        return Bmp.read(Paths.get(pathToFile));
    }

    public static Bmp read(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return new Bmp(bytes);
    }

    public static void write(Bmp bmp, File file) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(bmp.fileHeader.getBfSize()).order(ByteOrder.LITTLE_ENDIAN);

        BitMapFileHeader.write(bmp.fileHeader, buffer);
        BitMapInfo.write(bmp.infoHeader, buffer);
        buffer.put(bmp.pixelData);

        ((Buffer)buffer).flip();
        FileChannel channel = new FileOutputStream(file).getChannel();
        channel.write(buffer);
        channel.close();
    }
    public static Bmp write(BitMapFileHeader fileHeader, BitMapInfo infoHeader, byte[] image, File file) throws IOException {
        Bmp bmp = new Bmp(fileHeader, infoHeader, image);
        write(bmp, file);
        return bmp;
    }

    public static Bmp write(BitMapFileHeader fileHeader, BitMapInfo infoHeader, byte[] image, String pathToFile) throws IOException {
        return write(fileHeader, infoHeader, image, new File(pathToFile));
    }

}