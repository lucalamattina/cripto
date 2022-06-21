package itba.edu.ar;

import itba.edu.ar.Utils.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class StegoBMP {

    StegAlgorithms algorithm;
    String fileName;
    Message messageFile;
    Encryptor encryptedMessage;

    public StegoBMP(StegAlgorithms algorithm, String inFileName ) {
        this.algorithm = algorithm;
        this.fileName = inFileName;

    }

    public void steg(){

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

    public void encrypt(String password , Algorithm algorithm, Modes mode) {
            encryptedMessage = new Encryptor(messageFile, password, algorithm, mode);
    }

    public Message decrypt(String password) {

        byte[] decryptedMsg = encryptedMessage.getMessage(password);

        for (byte b : decryptedMsg) {
            System.out.println(b);
        }


        byte[] msgSize = Arrays.copyOf(decryptedMsg, 4);
        System.out.println("antes");

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
