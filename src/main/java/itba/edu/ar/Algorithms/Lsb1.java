package itba.edu.ar.Algorithms;

import itba.edu.ar.Utils.Tools;
//import org.graalvm.compiler.bytecode.Bytes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Lsb1 {
/*
    // Cantidad de bits reservados al tamaño
    private static final int SIZE_LENGTH = 32;

    public byte[] embedding(Message message, byte[] bmp) throws NotEnoughSpaceException {
        int messageSize = message.getFileSize();
        if (!canEncrypt(messageSize, bmp)) {
            throw new NotEnoughSpaceException("BMP file is too small for the message");
        }

        byte[] editedBmp = bmp.clone();

        int startIndex = 0;
        byte[] bigEndianSize = MessageUtils.toBigEndianBytes(message.getFileSize());
        encrypt(bigEndianSize, editedBmp, startIndex);
        startIndex += bigEndianSize.length * 8;

        encrypt(message.getFileBytes(), editedBmp, startIndex);
        startIndex += message.getFileBytes().length * 8;

        byte[] fileExtension = message.getFileExtension().getBytes();
        encrypt(fileExtension, editedBmp, startIndex);
        startIndex += fileExtension.length * 8;

        encrypt(new byte[1], bmp, startIndex);
        return editedBmp;
    }

    private void encrypt(byte[] messageToEncrypt, byte[] bmp, int startIndex) {
        int bmpIndex = startIndex;
        for (int messageIndex = 0; messageIndex < messageToEncrypt.length * 8; bmpIndex++) {
            int bitValue = getBitValueFromArray(messageToEncrypt, messageIndex);
            setBitValue(bmp, bmpIndex, 0, bitValue);
            messageIndex++;
        }
    }


    public Message extract(byte[] bmp) throws WrongLSBStegException {
        if (bmp == null)
            throw new WrongLSBStegException("Empty bpm");

        int messageLength = getMessageLength(bmp);
        if(messageLength < 0){
            throw new WrongLSBStegException("Wrong LSB. Size read is negative");
        }

        int messageStartByte = SIZE_LENGTH;
        int messageEndByte = messageLength * 8;
        byte[] decryptedMessage = decrypt(bmp, messageLength, messageStartByte);
        byte[] extension = decryptExtension(bmp, messageStartByte + messageEndByte);
        return new Message.MessageBuilder()
                .withFileSize(messageLength)
                .withFileBytes(decryptedMessage)
                .withFileExtension(extension)
                .build();
    }


    public byte[] embeddingCiphered(CipherMessage cipherMessage, byte[] bmp) throws NotEnoughSpaceException {
        byte[] bytesToEncrypt = cipherMessage.toByteArray();
        if (!canEncrypt(bytesToEncrypt.length, bmp)) {
            throw new NotEnoughSpaceException("BMP file is too small for the message");
        }

        byte[] editedBmp = bmp.clone();
        encrypt(cipherMessage.toByteArray(), editedBmp, 0);

        return editedBmp;
    }


    public CipherMessage extractCiphered(byte[] bmp) throws WrongLSBStegException {
        if (bmp == null)
            throw new WrongLSBStegException("Bmp empty");

        int messageLength = getMessageLength(bmp);

        if(messageLength < 0){
            throw new WrongLSBStegException("Wrong LSB. Size read is negative");
        }

        int messageStartByte = SIZE_LENGTH;
        byte[] decryptedMessage = decrypt(bmp, messageLength, messageStartByte);
        return new CipherMessage.CipherMessageBuilder()
                .withCipherSize(messageLength)
                .withCipherBytes(decryptedMessage)
                .build();
    }

    private byte[] decryptExtension(byte[] toDecrypt, int startByte) {
        List<Byte> byteList = new ArrayList<>();
        int readerIndex = 0;
        boolean lastIsNotNull = true;
        for (int decryptIndex = startByte; lastIsNotNull; decryptIndex++) {
            if((readerIndex / 8) + 1 > byteList.size())
                byteList.add((byte)0);

            readerIndex = setValuesToMessageList(readerIndex, toDecrypt[decryptIndex], byteList);

            if(readerIndex % 8 == 0) {
                if (byteList.size() > 0 && byteList.get(byteList.size() - 1) == 0)
                    lastIsNotNull = false;
            }
        }
        return Bytes.toArray(byteList);
    }

    // Size en bytes
    private byte[] decrypt(byte[] toDecrypt, int size, int startByte) {
        byte[] reader = new byte[size];
        int readerIndex = 0;
        for (int decryptIndex = startByte; readerIndex / 8 < size; decryptIndex++) {
            readerIndex = setValuesToMessage(readerIndex, toDecrypt[decryptIndex], reader);
        }
        return reader;
    }

    private byte[] decrypt(byte[] toDecrypt, int size) {
        return decrypt(toDecrypt, size, 0);
    }

    private int getMessageLength(byte[] toDecypt) {
        byte[] size = decrypt(toDecypt, SIZE_LENGTH);
        return ByteBuffer.wrap(size).getInt();
    }

    private int setValuesToMessageList(int messageIndex, byte toDecrypt, List<Byte> message) {
        if (getBitValue(toDecrypt, 0) > 0) {
            turnBitOn(message, messageIndex);
        }
        messageIndex++;
        return messageIndex;
    }

    private int setValuesToMessage(int messageIndex, byte toDecrypt, byte[] message) {
        if (getBitValue(toDecrypt, 0) > 0) {
            turnBitOn(message, messageIndex);
        }
        messageIndex++;
        return messageIndex;
    }

    private int getBitValue(byte b, int position) {
        return (b >> position & 1);
    }

    private int getBitValueFromArray(byte[] arr, int bit) {
        int index = bit / 8;
        int bitPosition = 7 - (bit % 8);

        return getBitValue(arr[index], bitPosition);
    }

    private void turnBitOn(List<Byte> arr, int pos) {
        int index = pos / 8;
        int bitPosition = 7 - (pos % 8);

        Byte b = (byte)(arr.get(index) | (1 << bitPosition));
        arr.set(index, b);
    }

    private void turnBitOn(byte[] arr, int pos) {
        int index = pos / 8;
        int bitPosition = 7 - (pos % 8);

        arr[index] |= (1 << bitPosition);
    }

    private void setBitValue(byte[] arr, int pos, int shifting, int value) {
        if (value == 1)
            arr[pos] |= 1 << shifting;
        else
            arr[pos] &= (255 - (1 << shifting));
    }

    private boolean canEncrypt(int messageSize, byte[] bmp) {
        int bitsToWrite = messageSize * 8;
        return bmp.length >= bitsToWrite;
    }
*/
}
