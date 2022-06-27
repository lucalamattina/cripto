package itba.edu.ar.Algorithms;


import itba.edu.ar.Utils.Encryptor;
import itba.edu.ar.Utils.Message;
import itba.edu.ar.Utils.Tools;
import com.google.common.primitives.Bytes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lsbi {

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

        hide(fullMsj, editedBmp, startIndex, componentTypes);

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
                msgByte = setValuesToMessage(msgIndex ,current, msgByte, componentTypes, i);
                msgIndex = (msgIndex + 1) % 8;

                if (msgIndex == 0){
                    fullMsg.add(msgByte);
                    msgByte = (byte) 0;

                }
            }
        }

        byte[] extraction =  Bytes.toArray(fullMsg);
        byte[] size;
        int messageSize = 0;

        size = Arrays.copyOf(extraction, 4);
        int length = Tools.recoverBigEndianBytes(size);

        byte[] msgBytes = Arrays.copyOfRange(extraction, 4, length + 4);

        byte[] extension = Arrays.copyOfRange(extraction, length + 4, length + 16);

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

            byte current = bmp[i];

            if(i < 4){
                componentTypes.set(i, getBitValue(current, 0) );
            }else {

            msgByte = setValuesToMessage(msgIndex ,current, msgByte, componentTypes, i);
            msgIndex = (msgIndex + 1) % 8;

            if (msgIndex == 0){
                fullMsg.add(msgByte);
                msgByte = (byte) 0;

            }
            }
        }
        byte[] extraction =  Bytes.toArray(fullMsg);
        byte[] messageSize = Arrays.copyOf(extraction, 4);
        int length = Tools.recoverBigEndianBytes(messageSize);



        byte[] message = Arrays.copyOfRange(extraction, 4, length + 4);

        return message;
    }

    private static byte setValuesToMessage(int messageIndex, byte toDecrypt, byte message, List<Integer> componentTypes, int pos) {
        int lsb1 = getBitValue(toDecrypt, 0);
        int lsb2 = getBitValue(toDecrypt, 1);
        int lsb3 = getBitValue(toDecrypt, 2);

        if(lsb3 == 0 && lsb2 == 0 && componentTypes.get(0) > 0){
            lsb1 = (lsb1 - 1) * - 1; //to invert bit value
            return setSpecificBitValue(message, messageIndex , lsb1,pos);

        }
        else if(lsb3 == 0 && lsb2 == 1 && componentTypes.get(1) > 0){
            lsb1 = (lsb1 - 1) * - 1; //to invert bit value
            return setSpecificBitValue(message, messageIndex, lsb1,pos);
        }
        else if(lsb3 == 1 && lsb2 == 0 && componentTypes.get(2) > 0){
            lsb1 = (lsb1 - 1) * - 1; //to invert bit value
            return setSpecificBitValue(message, messageIndex, lsb1,pos);
        }
        else if(lsb3 == 1 && lsb2 == 1 && componentTypes.get(3) > 0){
            lsb1 = (lsb1 - 1) * - 1; //to invert bit value
            return setSpecificBitValue(message, messageIndex, lsb1, pos);
        }
        return setSpecificBitValue(message, messageIndex, lsb1, pos);
    }

    private static byte setSpecificBitValue(byte arr, int n, int value, int i) {
        int bitpos = 7 - (n%8);
        byte ret = arr;
        if (value == 1){
            ret |= 1 << bitpos;
        }
        else{
            ret &= ~(1 << bitpos);
        }
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


    private static void setBitValue(byte[] arr, int pos, int value) {
        if (value == 1)
            arr[pos] |= 1;
        else
            arr[pos] &= (255 - (1));
    }

    private static boolean canEncrypt(int messageSize, byte[] bmp) {
        int bitsToWrite = messageSize * 8;
        return bmp.length >= bitsToWrite;
    }

}
