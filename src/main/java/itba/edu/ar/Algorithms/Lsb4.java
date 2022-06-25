package itba.edu.ar.Algorithms;

import com.google.common.primitives.Bytes;
import itba.edu.ar.Utils.Encryptor;
import itba.edu.ar.Utils.Message;
import itba.edu.ar.Utils.Tools;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Lsb4 {

    // Cantidad de bits reservados al tamaño
    private static final int SIZE_LENGTH = 32;

    public static byte[] embedding(Message message, byte[] bmp) throws NotEnoughSpaceException {
        int messageSize = message.getIntFileSize();
        if (!canEncrypt(messageSize, bmp)) {
            throw new NotEnoughSpaceException();
        }

        byte[] editedBmp = bmp.clone();

        int startIndex = 0;
        byte[] bigEndianSize = Tools.makeBigEndian(message.getIntFileSize());
        hide(bigEndianSize, editedBmp, startIndex);
        startIndex += bigEndianSize.length * 8 / 4;

        hide(message.getFileBytes(), editedBmp, startIndex);
        startIndex += message.getFileBytes().length * 8 / 4;

        byte[] fileExtension = message.getFileExtension().getBytes();
        hide(fileExtension, editedBmp, startIndex);
        startIndex += fileExtension.length * 8 / 4;

        hide(new byte[1], editedBmp, startIndex);
        return editedBmp;
    }

    private static void hide(byte[] messageToHide, byte[] bmp, int startIndex) {
        int bmpIndex = startIndex;
        for (int messageIndex = 0; messageIndex < messageToHide.length * 8; bmpIndex++) {
            for (int i = 3; i >= 0; i--) {
                int bitValue = getBitValueFromArray(messageToHide, messageIndex);
                setBitValue(bmp, bmpIndex, i, bitValue);
                messageIndex++;
            }
        }
    }


    public static Message extract(byte[] bmp) throws WrongLSBStegException {
        if (bmp == null)
            throw new WrongLSBStegException();

        int messageLength = getMessageLength(bmp);
        if(messageLength < 0){
            throw new WrongLSBStegException();
        }

        int messageStartByte = SIZE_LENGTH / 4;
        int messageEndByte = messageLength * 8 / 4;
        byte[] decryptedMessage = reveal(bmp, messageLength, messageStartByte);
        byte[] extension = revealExtension(bmp, messageStartByte + messageEndByte);
        return new Message(decryptedMessage, Tools.makeBigEndian(messageLength), extension);
    }


    public static byte[] embeddingCiphered(Encryptor cipherMessage, byte[] bmp) throws NotEnoughSpaceException {
        byte[] bytesToEncrypt = cipherMessage.getBytes();
        if (!canEncrypt(bytesToEncrypt.length, bmp)) {
            throw new NotEnoughSpaceException();
        }

        byte[] editedBmp = bmp.clone();
        hide(cipherMessage.getCipherSize(), editedBmp, 0);
        hide(cipherMessage.getBytes(), editedBmp, 8);

        return editedBmp;
    }


    public static byte[] extractCiphered(byte[] bmp) throws WrongLSBStegException {
        if (bmp == null)
            throw new WrongLSBStegException();

        int messageLength = getMessageLength(bmp);

        if(messageLength < 0){
            throw new WrongLSBStegException();
        }

        return reveal(bmp, messageLength, SIZE_LENGTH / 4);
    }


    public static int getMaxSize(byte[] bmp) {
        return bmp.length  * 4 / 8;
    }

    private static byte[] revealExtension(byte[] toDecrypt, int startByte) {
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
    private static byte[] reveal(byte[] toDecrypt, int size, int startByte) {
        byte[] reader = new byte[size];
        int readerIndex = 0;
        for (int decryptIndex = startByte; readerIndex / 8 < size; decryptIndex++) {
            readerIndex = setValuesToMessage(readerIndex, toDecrypt[decryptIndex], reader);
        }
        return reader;
    }

    private static int getMessageLength(byte[] toDecypt) {
        byte[] size = reveal(toDecypt, SIZE_LENGTH / 4, 0);
        return ByteBuffer.wrap(size).getInt();
    }

    private static int setValuesToMessageList(int messageIndex, byte toDecrypt, List<Byte> message) {
        for (int i = 3; i >= 0; i--) {
            if (getBitValue(toDecrypt, i) > 0) {
                turnBitOn(message, messageIndex);
            }
            messageIndex++;
        }
        return messageIndex;
    }

    private static int setValuesToMessage(int messageIndex, byte toDecrypt, byte[] message) {
        for (int i = 3; i >= 0; i--) {
            if (getBitValue(toDecrypt, i) > 0) {
                turnBitOn(message, messageIndex);
            }
            messageIndex++;
        }
        return messageIndex;
    }

    private static int getBitValue(byte b, int position) {
        return (b >> position & 1);
    }

    private static int getBitValueFromArray(byte[] arr, int bit) {
        int index = bit / 8;
        int bitPosition = 7 - (bit % 8);

        return getBitValue(arr[index], bitPosition);
    }

    private static void turnBitOn(List<Byte> arr, int pos) {
        int index = pos / 8;
        int bitPosition = 7 - (pos % 8);

        Byte b = (byte)(arr.get(index) | (1 << bitPosition));
        arr.set(index, b);
    }

    private static void turnBitOn(byte[] arr, int pos) {
        int index = pos / 8;
        int bitPosition = 7 - (pos % 8);

        arr[index] |= (1 << bitPosition);
    }

    private static void setBitValue(byte[] arr, int pos, int shifting, int value) {
        if (value == 1)
            arr[pos] |= 1 << shifting;
        else
            arr[pos] &= (255 - (1 << shifting));
    }

    private static boolean canEncrypt(int messageSize, byte[] bmp) {
        int bitsToWrite = messageSize * 8;
        return bmp.length * 4 >= bitsToWrite;
    }

}
