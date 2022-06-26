package itba.edu.ar.Algorithms;

//import org.graalvm.compiler.bytecode.Bytes;

import itba.edu.ar.Utils.Encryptor;
import itba.edu.ar.Utils.Message;
import itba.edu.ar.Utils.Tools;
import com.google.common.primitives.Bytes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lsbi {

    //TODO startIndex is good


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
        componentTypes.add(2,0);
        componentTypes.add(3,0);

        int startIndex = 4;

        byte[] bigEndianSize = Tools.makeBigEndian(message.getIntFileSize());

        byte[] fullMsj = message.unpackMessage();

        //startIndex += bigEndianSize.length * 8;
       // hide(message.getFileBytes(), editedBmp, startIndex, componentTypes);}

        hide(fullMsj, editedBmp, startIndex, componentTypes);

      //  byte[] fileExtension = message.getFileExtension().getBytes();

        //hide(fileExtension, editedBmp, startIndex, componentTypes);


       // startIndex += fileExtension.length * 8;

        System.out.println("AFTER SIZE START = " + startIndex);

        //hide(new byte[1], editedBmp, startIndex);


        int index = 0;
        for (int element: componentTypes) {

            if(element > 0){
                setBitValue(editedBmp, index++, 1);
            }
            else{
                setBitValue(editedBmp, index++, 0);
            }
        }

        System.out.println("EDITED BMP LAGRGO");
        System.out.println(editedBmp.length);
        System.out.println(editedBmp.length / 8);


        for (int i = 0; i < 36; i++) {
            System.out.println( String.format("%8s", Integer.toBinaryString(editedBmp[i] & 0xFF)).replace(' ', '0'));
        }

        return editedBmp;


    }



    private static void hide(byte[] messageToEncrypt, byte[] editedBmp, int startIndex, List<Integer> componentTypes) {
        int bmpIndex = startIndex ;
        int count = 0;

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
            count = messageIndex;
        }
        System.out.println("COMPONENTS " +  componentTypes);
        System.out.println("msgIndex "+ count);
        System.out.println("bmpIndex "+ bmpIndex);

        bmpIndex = startIndex;

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

   /* private static void hide(byte[] messageToEncrypt, byte[] bmp, int startIndex) {
        int bmpIndex = startIndex;

        for (int messageIndex = 0; messageIndex < messageToEncrypt.length * 8; bmpIndex++) {
            int bitValue = getBitValueFromArray(messageToEncrypt, messageIndex);
            setBitValue(bmp, bmpIndex, 0, bitValue);
            messageIndex++;
        }
    }*/


   /* public static Message extract(byte[] bmp) throws WrongLSBStegException {
        if (bmp == null)
            throw new WrongLSBStegException();

        //int messageLength = getMessageLength(bmp);

        //int messageStartByte = SIZE_LENGTH + 4;
        //int messageEndByte = messageLength * 8;

        byte[] decryptedMessage = reveal(bmp, 4);
        //byte[] extension = revealExtension(bmp, messageStartByte + messageEndByte);
        return new Message(decryptedMessage, Tools.makeBigEndian(messageLength), extension);
    }*/
    public static Message extract(byte[] bmp) throws WrongLSBStegException {
        if (bmp == null)
            throw new WrongLSBStegException();

        List<Byte> fullMsg = new ArrayList<>();

        //[00,01,10,11]
        List<Integer> componentTypes = new ArrayList<>();
        componentTypes.add(0,0);componentTypes.add(1,0);componentTypes.add(2,0);componentTypes.add(3,0);

        byte msgByte = (byte)0;
        int msgIndex = 0;

        for (int i = 0; i < bmp.length; i++) {

            byte current = bmp[i];


                if(i < 4){
                componentTypes.set(i, getBitValue(current, 0) );
            }else {

                msgByte = setValuesToMessage(msgIndex ,current, msgByte, componentTypes);
                msgIndex = (msgIndex + 1) % 8;
               // System.out.println("INDEX = " + msgIndex);

                if (msgIndex == 0){
                    //System.out.println("msgByte ADD " + String.format("%8s", Integer.toBinaryString(msgByte & 0xFF)).replace(' ', '0'));
                    fullMsg.add(msgByte);
                    msgByte = (byte) 0;

                }
            }
            //System.out.println( "Iteration = " + i);
        }

        byte[] extraction =  Bytes.toArray(fullMsg);
        byte[] size;
        int messageSize = 0;


        size = Arrays.copyOf(extraction, 4);
        int length = Tools.recoverBigEndianBytes(size);
        System.out.println("LENGTH " + length);

        byte[] msgBytes = Arrays.copyOfRange(extraction, 4, length + 4);

        byte[] extension = Arrays.copyOfRange(extraction, length + 4, length + 10);

        return new Message(msgBytes, Tools.makeBigEndian(messageSize), extension);
    }


    public static byte[] embeddingCiphered(Encryptor cipherMessage, byte[] bmp) throws NotEnoughSpaceException {
        byte[] bytesToEncrypt = cipherMessage.getBytes();
        if (!canEncrypt(bytesToEncrypt.length, bmp)) {
            throw new NotEnoughSpaceException();
        }

        byte[] editedBmp = bmp.clone();

        //[00,01,10,11]
        List<Integer> componentTypes = new ArrayList<>();
        componentTypes.add(0,0);
        componentTypes.add(1,0);
        componentTypes.add(2,0);
        componentTypes.add(3,0);

        hide(cipherMessage.getCipherSize(), editedBmp, 4, componentTypes);
        hide(cipherMessage.getBytes(), editedBmp, 36, componentTypes);

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


    public static byte[] extractCiphered(byte[] bmp) throws WrongLSBStegException {
        if (bmp == null)
            throw new WrongLSBStegException();

        List<Byte> fullMsg = new ArrayList<>();

        //[00,01,10,11]
        List<Integer> componentTypes = new ArrayList<>();
        componentTypes.add(0,0);componentTypes.add(1,0);componentTypes.add(2,0);componentTypes.add(3,0);


        byte msgByte = (byte)0;
        int msgIndex = 0;

        for (int i = 0; i < bmp.length; i++) {


            if (i >= 40 && i < 48 ){
                System.out.println( String.format("%8s", Integer.toBinaryString(bmp[i] & 0xFF)).replace(' ', '0'));
            }

            byte current = bmp[i];

            if(i < 4){
                componentTypes.set(i, getBitValue(current, 0) );
            }else {

            msgByte = setValuesToMessage(msgIndex ,current, msgByte, componentTypes);
            msgIndex = (msgIndex + 1) % 8;

            if (msgIndex == 0){
                fullMsg.add(msgByte);
                msgByte = (byte) 0;

            }
            }
        }
        byte[] extraction =  Bytes.toArray(fullMsg);

/*

        byte[] extraction =  Bytes.toArray(fullMsg);
        byte[] size = new byte[4];
        byte[] message = new byte[0];
        int messageSize = 0;
        int j = 0;
        boolean end = false;

        System.out.println(extraction.length);

        for (int i = 0; i < extraction.length && !end; i++) {
            if (i<4){
                size[i] = extraction[i];

            }else if (i == 4) {
                messageSize = (ByteBuffer.wrap(size).getInt())/8;
                message = new byte[messageSize];
            }else {

                if (j < messageSize){
                    message[j] = extraction[i];
                    j++;
                }else {
                    end = true;
                }
            }

        }
 */
        byte[] messageSize = Arrays.copyOf(extraction, 4);
        int length = Tools.recoverBigEndianBytes(messageSize);


        System.out.println("LENGTH " + length);

        byte[] message = Arrays.copyOfRange(extraction, 4, length + 4);

        for (int i = 0; i < 4; i++) {
            System.out.println( String.format("%8s", Integer.toBinaryString(message[i] & 0xFF)).replace(' ', '0'));
        }
        System.out.println("MESSSAGE SIZE IN EXTRACT " + message.length);
        return message;
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
   /* private static byte[] reveal(byte[] toDecrypt) {
        //byte[] reader = new byte[toDecrypt.length];
        int readerIndex = 0;
        //[00,01,10,11]
        List<Integer> componentTypes = new ArrayList<>();
        componentTypes.add(0,0);
        componentTypes.add(1,0);
        componentTypes.add(2,0);
        componentTypes.add(3,0);

        for (int decryptIndex = 0; decryptIndex < 4; decryptIndex++) {
            componentTypes.set(decryptIndex, getBitValue(toDecrypt[decryptIndex], 0) );
        }

        byte[] sizeSave = new byte[4];
        int msgStart = (32 + 4) * 8;

        for (int decryptIndex = 4; readerIndex < msgStart; decryptIndex++) {
            int lsb1 = getBitValue(toDecrypt[decryptIndex], 0);
            setSpecificBitValue(sizeSave, readerIndex, lsb1);
            readerIndex++;
        }
        int msgLength = ByteBuffer.wrap(sizeSave).getInt();

        for (int decryptIndex = msgStart; readerIndex < msgLength * 8; decryptIndex++) {
            int lsb1 = getBitValue(toDecrypt[decryptIndex], 0);
            setSpecificBitValue(reader, readerIndex, lsb1);
            readerIndex++;
        }
        readerIndex = 0;
        for (int decryptIndex = startByte; readerIndex < size * 8; decryptIndex++) {
            setValuesToMessage(readerIndex, toDecrypt[decryptIndex], reader, componentTypes);
            readerIndex++;
        }
        return reader;
    }*/

    /*private static int getMessageLength(byte[] toDecypt) {

        byte[] size = reveal(toDecypt, SIZE_LENGTH, 4);
        System.out.println("the size is = " + new String(size));

        return ByteBuffer.wrap(size).getInt();

    }*/

    private static int setValuesToMessageList(int messageIndex, byte toDecrypt, List<Byte> message) {
        if (getBitValue(toDecrypt, 0) > 0) {
            turnBitOn(message, messageIndex);
        }
        messageIndex++;
        return messageIndex;
    }

    private static byte setValuesToMessage(int messageIndex, byte toDecrypt, byte message, List<Integer> componentTypes) {
        int lsb1 = getBitValue(toDecrypt, 0);
        int lsb2 = getBitValue(toDecrypt, 1);
        int lsb3 = getBitValue(toDecrypt, 2);

        if(lsb3 == 0 && lsb2 == 0 && componentTypes.get(0) > 0){
            lsb1 = (lsb1 - 1) * - 1; //to invert bit value
            return setSpecificBitValue(message, messageIndex, lsb1);
        }
        else if(lsb3 == 0 && lsb2 == 1 && componentTypes.get(1) > 0){
            lsb1 = (lsb1 - 1) * - 1; //to invert bit value
            return setSpecificBitValue(message, messageIndex, lsb1);
        }
        else if(lsb3 == 1 && lsb2 == 0 && componentTypes.get(2) > 0){
            lsb1 = (lsb1 - 1) * - 1; //to invert bit value
            return setSpecificBitValue(message, messageIndex, lsb1);
        }
        else if(lsb3 == 1 && lsb2 == 1 && componentTypes.get(3) > 0){
            lsb1 = (lsb1 - 1) * - 1; //to invert bit value
            return setSpecificBitValue(message, messageIndex, lsb1);
        }
        return message ;
    }

    private static void setSpecificBitValueArrays(byte[] arr, int n, int value) {
        int bitpos = 7 - (n%8);
        //System.out.println(bitpos);
        if (value == 1)
            arr[n/8] |= 1 << bitpos;
        else
            arr[n/8] &= ~(1 << bitpos);
    }

    private static byte setSpecificBitValue(byte arr, int n, int value) {
        int bitpos = 7 - (n%8);
        byte ret = arr;
        //System.out.println(bitpos);
        if (value == 1)
            ret |= 1 << bitpos;
        else
            ret &= ~(1 << bitpos);
        return ret;
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
