package itba.edu.ar;

import itba.edu.ar.Utils.BMP;
import itba.edu.ar.Utils.StegAlgorithms;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StegoBMP {

    StegAlgorithms algorithm;
    String fileName;
    BMP BMPFile;

    public StegoBMP(StegAlgorithms algorithm, String inFileName ) {
        this.algorithm = algorithm;
        this.fileName = inFileName;

    }

    public void encrypt(){
        try {
            String extension;
            if(fileName.contains("."))
                extension = fileName.substring(fileName.lastIndexOf("."));
            else
                extension = "";
            byte[] bytes = extension.getBytes(StandardCharsets.ISO_8859_1);
            byte[] fileExtension = new byte[bytes.length + 1];
            System.arraycopy(bytes, 0, fileExtension, 0, bytes.length);

            byte[] fileBytes = Files.readAllBytes(Paths.get(fileName));

            ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
            buffer.putInt(fileBytes.length);
            byte[] size = buffer.array();


            this.BMPFile = new BMP(fileBytes,size,fileExtension);

        } catch (IOException e) {
            e.printStackTrace();
        }

        switch (algorithm) {
            case LSB1:
                break;
            case LSB4:
                break;
            case LSBI:
                break;
            default:
        }
    }

}
