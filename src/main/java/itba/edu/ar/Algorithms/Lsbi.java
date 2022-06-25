package itba.edu.ar.Algorithms;

//import org.graalvm.compiler.bytecode.Bytes;

import itba.edu.ar.Utils.Encryptor;
import itba.edu.ar.Utils.Message;
import itba.edu.ar.Utils.Tools;
import com.google.common.primitives.Bytes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Lsbi {

    // Cantidad de bits reservados al tamaño
    private static final int SIZE_LENGTH = 32;

    public static byte[] embedding(Message message, byte[] bmp) throws NotEnoughSpaceException {
        int messageSize = message.getIntFileSize();
        if (!canEncrypt(messageSize, bmp)) {
            throw new NotEnoughSpaceException();
        }

        byte[] editedBmp = bmp.clone();

        //[00,01,10,11]
        List<Integer> componentTypes = new ArrayList<>();
        componentTypes.add(0,0);
        componentTypes.add(1,0);
        componentTypes.add(2,0);;
        componentTypes.add(3,0);

        int startIndex = 4;
        byte[] bigEndianSize = Tools.makeBigEndian(message.getIntFileSize());
        hide(bigEndianSize, editedBmp, startIndex);
        startIndex += bigEndianSize.length * 8;

        hide(message.getFileBytes(), editedBmp, startIndex, componentTypes);
        startIndex += message.getFileBytes().length * 8;

        byte[] fileExtension = message.getFileExtension().getBytes();
        hide(fileExtension, editedBmp, startIndex);
        startIndex += fileExtension.length * 8;

        hide(new byte[1], editedBmp, startIndex);
        int index = 0;
        for (int element: componentTypes) {
            if(element > 0){
                setBitValue(editedBmp, index++, 1);
            }
            else{
                setBitValue(editedBmp, index++, 0);
            }
        }
        return editedBmp;
    }

    private static void hide(byte[] messageToEncrypt, byte[] editedBmp, int startIndex, List<Integer> componentTypes) {
        int bmpIndex = startIndex ;
        for (int messageIndex = 0; messageIndex < messageToEncrypt.length * 8; bmpIndex++) {
            int lsb1 = getBitValue(editedBmp[bmpIndex], 0);
            int lsb2 = getBitValue(editedBmp[bmpIndex], 1);
            int lsb3 = getBitValue(editedBmp[bmpIndex], 2);
            int bitValue = getBitValueFromArray(messageToEncrypt, messageIndex);
            int aux;
            if(lsb3 == 0 && lsb2 == 0){
                aux = componentTypes.get(0);
                if(lsb1 == bitValue){
                    aux--;
                }
                else{
                    aux++;
                }
                componentTypes.set(0, aux);
            }
            else if(lsb3 == 0 && lsb2 == 1){
                aux = componentTypes.get(1);
                if(lsb1 == bitValue){
                    aux--;
                }
                else{
                    aux++;
                }
                componentTypes.set(1, aux);
            }
            else if(lsb3 == 1 && lsb2 == 0){
                aux = componentTypes.get(2);
                if(lsb1 == bitValue){
                    aux--;
                }
                else{
                    aux++;
                }
                componentTypes.set(2, aux);
            }
            else if(lsb3 == 1 && lsb2 == 1){
                aux = componentTypes.get(3);
                if(lsb1 == bitValue){
                    aux--;
                }
                else{
                    aux++;
                }
                componentTypes.set(3, aux);
            }
            setBitValue(editedBmp, bmpIndex, bitValue);
            messageIndex++;
        }
        bmpIndex = startIndex + 4;
        for (int messageIndex = 0; messageIndex < messageToEncrypt.length * 8; bmpIndex++) {
            int lsb1 = getBitValue(editedBmp[bmpIndex], 0);
            int lsb2 = getBitValue(editedBmp[bmpIndex], 1);
            int lsb3 = getBitValue(editedBmp[bmpIndex], 2);
            if(lsb3 == 0 && lsb2 == 0 && componentTypes.get(0) > 0){
                lsb1 = (lsb1 - 1) * - 1; //to invert bit value
                setBitValue(editedBmp, bmpIndex, lsb1);
            }
            else if(lsb3 == 0 && lsb2 == 1 && componentTypes.get(1) > 0){
                lsb1 = (lsb1 - 1) * - 1; //to invert bit value
                setBitValue(editedBmp, bmpIndex, lsb1);
            }
            else if(lsb3 == 1 && lsb2 == 0 && componentTypes.get(2) > 0){
                lsb1 = (lsb1 - 1) * - 1; //to invert bit value
                setBitValue(editedBmp, bmpIndex, lsb1);
            }
            else if(lsb3 == 1 && lsb2 == 1 && componentTypes.get(3) > 0){
                lsb1 = (lsb1 - 1) * - 1; //to invert bit value
                setBitValue(editedBmp, bmpIndex, lsb1);
            }
            messageIndex++;
        }
    }

    private static void hide(byte[] messageToEncrypt, byte[] bmp, int startIndex) {
        int bmpIndex = startIndex;
        for (int messageIndex = 0; messageIndex < messageToEncrypt.length * 8; bmpIndex++) {
            int bitValue = getBitValueFromArray(messageToEncrypt, messageIndex);
            setBitValue(bmp, bmpIndex, 0, bitValue);
            messageIndex++;
        }
    }


    public static Message extract(byte[] bmp) throws WrongLSBStegException {
        if (bmp == null)
            throw new WrongLSBStegException();

        int messageLength = getMessageLength(bmp);
        if(messageLength < 0){
            throw new WrongLSBStegException();
        }

        int messageStartByte = SIZE_LENGTH;
        int messageEndByte = messageLength * 8;
        byte[] decryptedMessage = reveal(bmp, messageLength, messageStartByte);
        byte[] extension = revealExtension(bmp, messageStartByte + messageEndByte);
        return new Message(Tools.makeBigEndian(messageLength), decryptedMessage, extension);
    }


    public byte[] embeddingCiphered(Encryptor cipherMessage, byte[] bmp) throws NotEnoughSpaceException {
        byte[] bytesToEncrypt = cipherMessage.getBytes();
        if (!canEncrypt(bytesToEncrypt.length, bmp)) {
            throw new NotEnoughSpaceException();
        }

        byte[] editedBmp = bmp.clone();
        hide(cipherMessage.getBytes(), editedBmp, 0); //TODO: ESTO TALVEZ NO FUNCIONE

        return editedBmp;
    }


    public static byte[] extractCiphered(byte[] bmp) throws WrongLSBStegException {
        if (bmp == null)
            throw new WrongLSBStegException();

        int messageLength = getMessageLength(bmp);

        if(messageLength < 0){
            throw new WrongLSBStegException();
        }

        return reveal(bmp, messageLength, SIZE_LENGTH);
    }


    public static int getMaxSize(byte[] bmp) {
        return bmp.length / 8;
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
        //[00,01,10,11]
        List<Integer> componentTypes = new ArrayList<>();
        componentTypes.add(0,0);
        componentTypes.add(1,0);
        componentTypes.add(2,0);
        componentTypes.add(3,0);
        int count = 0;
        for (int decryptIndex = startByte; decryptIndex < startByte + 4; decryptIndex++) {
            componentTypes.set(count, getBitValue(toDecrypt[decryptIndex], 0) );
            count++;
        }
        for (int decryptIndex = startByte + 4; readerIndex < size * 8; decryptIndex++) {
            int lsb1 = getBitValue(toDecrypt[decryptIndex], 0);
            setSpecificBitValue(reader, readerIndex, lsb1);
            readerIndex++;
        }
        readerIndex = 0;
        for (int decryptIndex = startByte + 4; readerIndex < size * 8; decryptIndex++) {
            setValuesToMessage(readerIndex, toDecrypt[decryptIndex], reader, componentTypes);
            readerIndex++;
        }
        return reader;
    }

    private static int getMessageLength(byte[] toDecypt) {
        byte[] size = reveal(toDecypt, SIZE_LENGTH, 0);
        return ByteBuffer.wrap(size).getInt();
    }

    private static int setValuesToMessageList(int messageIndex, byte toDecrypt, List<Byte> message) {
        if (getBitValue(toDecrypt, 0) > 0) {
            turnBitOn(message, messageIndex);
        }
        messageIndex++;
        return messageIndex;
    }

    private static void setValuesToMessage(int messageIndex, byte toDecrypt, byte[] message, List<Integer> componentTypes) {
        int lsb1 = getBitValue(toDecrypt, 0);
        int lsb2 = getBitValue(toDecrypt, 1);
        int lsb3 = getBitValue(toDecrypt, 2);
        if(lsb3 == 0 && lsb2 == 0 && componentTypes.get(0) > 0){
            lsb1 = (lsb1 - 1) * - 1; //to invert bit value
            setSpecificBitValue(message, messageIndex, lsb1);
        }
        else if(lsb3 == 0 && lsb2 == 1 && componentTypes.get(1) > 0){
            lsb1 = (lsb1 - 1) * - 1; //to invert bit value
            setSpecificBitValue(message, messageIndex, lsb1);
        }
        else if(lsb3 == 1 && lsb2 == 0 && componentTypes.get(2) > 0){
            lsb1 = (lsb1 - 1) * - 1; //to invert bit value
            setSpecificBitValue(message, messageIndex, lsb1);
        }
        else if(lsb3 == 1 && lsb2 == 1 && componentTypes.get(3) > 0){
            lsb1 = (lsb1 - 1) * - 1; //to invert bit value
            setSpecificBitValue(message, messageIndex, lsb1);
        }
    }

    private static void setSpecificBitValue(byte[] arr, int bitpos, int value) {
        bitpos = 7 - bitpos;
        if (value == 1)
            arr[bitpos/8] |= 1 << bitpos;
        else
            arr[bitpos/8] &= ~(1 << bitpos);
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

    private static void setBitValue(byte[] arr, int pos, int value) {
        if (value == 1)
            arr[pos] |= 1;
        else
            arr[pos] &= (255 - (1));
    }

    private static void setBitValue(byte[] arr, int pos, int shifting, int value) {
        if (value == 1)
            arr[pos] |= 1 << shifting;
        else
            arr[pos] &= (255 - (1 << shifting));
    }

    private static boolean canEncrypt(int messageSize, byte[] bmp) {
        int bitsToWrite = messageSize * 8;
        return bmp.length >= bitsToWrite;
    }

}
