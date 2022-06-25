package itba.edu.ar;

import itba.edu.ar.Algorithms.Lsb1;
import itba.edu.ar.Algorithms.Lsb4;
import itba.edu.ar.Algorithms.Lsbi;
import itba.edu.ar.Utils.*;
import itba.edu.ar.bmp.*;
import org.kohsuke.args4j.CmdLineException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class StegoBMP {

    StegAlgorithms algorithm;
    String fileName;
    String outFile;
    Message messageFile;
    Encryptor encryptedMessage;
    boolean encrypted;

    public StegoBMP(StegAlgorithms algorithm, String inFileName, String outFile, boolean encrypted ) {
        this.algorithm = algorithm;
        this.fileName = inFileName;
        this.encrypted = encrypted;
        this.outFile = outFile;
    }

    public void steg(String holderFile){


        Bmp holderBmp;
        try {
            holderBmp = Bmp.read(holderFile);
            byte[] outBmpData;

            if (!encrypted) {
                switch (algorithm) {
                    case LSB1:
                        outBmpData = Lsb1.embedding(messageFile, holderBmp.getPixelData());
                        break;
                    case LSB4:
                        outBmpData = Lsb4.embedding(messageFile, holderBmp.getPixelData());
                        break;
                    case LSBI:
                        outBmpData = Lsbi.embedding(messageFile, holderBmp.getPixelData());
                        break;
                    default:
                        throw new CmdLineException("wrong algorithm", new Throwable());
                }
            }else {



                switch (algorithm) {
                    case LSB1:
                        System.out.println("cripted " + encryptedMessage.getBytes().length);
                        outBmpData = Lsb1.embeddingCiphered(encryptedMessage, holderBmp.getPixelData());
                        break;
                    case LSB4:
                        System.out.println("cripted " + encryptedMessage.getBytes().length);
                        outBmpData = Lsb4.embeddingCiphered(encryptedMessage, holderBmp.getPixelData());
                        break;
                    case LSBI:
                        outBmpData = Lsbi.embeddingCiphered(encryptedMessage, holderBmp.getPixelData());
                        break;
                    default:
                        throw new CmdLineException("wrong algorithm", new Throwable());

                }
            }

            Bmp.write(holderBmp.getFileHeader(), holderBmp.getInfoHeader(), outBmpData, this.outFile );

        }catch (Exception e){
            e.printStackTrace();
        }



    }

    public Message deSteg(String holderFile){
        Message result;
        Bmp holderBmp;
        try {
            holderBmp = Bmp.read(holderFile);
            switch (algorithm) {
                case LSB1:
                    result = Lsb1.extract(holderBmp.getPixelData());
                    break;
                case LSB4:
                    result = Lsb4.extract(holderBmp.getPixelData());
                    break;
                case LSBI:
                    result = Lsbi.extract(holderBmp.getPixelData());
                    break;
                default:
                    throw new CmdLineException("wrong algorithm", new Throwable());
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public byte[] cryptedDeSteg(String holderFile, Algorithm encryptionAlg , Modes mode) {
        byte [] result;

        Bmp holderBmp;
        try {
            holderBmp = Bmp.read(holderFile);
            switch (algorithm) {
                case LSB1:
                    result = Lsb1.extractCiphered(holderBmp.getPixelData());
                    break;
                case LSB4:
                    result = Lsb4.extractCiphered(holderBmp.getPixelData());
                    break;
                case LSBI:
                    result = Lsbi.extractCiphered(holderBmp.getPixelData());
                    break;
                default:
                    throw new CmdLineException("wrong algorithm", new Throwable());
            }
            encryptedMessage = new Encryptor(result, encryptionAlg, mode );
            return result;
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public void encrypt(String password , Algorithm algorithm, Modes mode) {
            encryptedMessage = new Encryptor(messageFile, password, algorithm, mode);
    }

    public Message decrypt(String password) {

        byte[] decryptedMsg = encryptedMessage.getMessage(password);

        byte[] msgSize = Arrays.copyOf(decryptedMsg, 4);
        int length = Tools.recoverBigEndianBytes(msgSize);
        byte[] msgBytes = Arrays.copyOfRange(decryptedMsg, 4, length + 4);
        byte[] extension = Arrays.copyOfRange(decryptedMsg, length + 4, decryptedMsg.length);

        return new Message(msgBytes,msgSize,extension);

    }

    public void readMessage(){
        try {
            String extension;
            if(fileName.contains("."))
                extension = fileName.substring(fileName.lastIndexOf("."));
            else
                extension = "";

            byte[] fileExtension = Tools.makeNullTerminatedBytes(extension);



            byte[] fileBytes = Files.readAllBytes(Paths.get(fileName));

            byte[] size = Tools.makeBigEndian(fileBytes.length);

            this.messageFile = new Message(fileBytes,size,fileExtension);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Message getMessageFile() {
        return messageFile;
    }

    public Encryptor getEncryptedMessage() {
        return encryptedMessage;
    }


}
