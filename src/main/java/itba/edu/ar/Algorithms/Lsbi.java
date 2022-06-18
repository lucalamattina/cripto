package itba.edu.ar.Algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Arrays;

public class Lsbi {

    /**
     * Orden de los pasos:
     * 1) preparo msj = Tamaño real || datos archivo || extensión
     *
     * @param message msj
     * @param bmp     foto para esconder el msj
     * @return foto estanografada
     */

    @Override
    public byte[] embedding(Message message, byte[] bmp) throws NotEnoughSpaceException {
        byte[] msg = message.toByteArray();
        return embedding(msg, bmp);
    }

    @Override
    public Message extract(byte[] bmp) throws WrongLSBStegException {
        if (bmp == null) {
            throw new WrongLSBStegException("BMP is empty");
        }
        this.extractIndex = 6;

        int msgSize = MessageUtils.fromBigEndianBytes(decSize);

        if (msgSize < 0) {
            throw new WrongLSBStegException("Wrong LSB");
        }

        byte[] fileExtension = decryptExtension(bmp);

        return new Message.MessageBuilder()
                .withFileBytes(message)
                .withFileSize(size)
                .withFileExtension(fileExtension)
                .build();
    }

    private byte[] decryptExtension(byte[] bmp) {

        List<Byte> decryption = new ArrayList<>();
        int j = 0;

        int counterBit = 0;
        byte b = 0;
        boolean flag = true;

        while (flag) {
            if (this.extractIndex > bmp.length) {
                j++;
                this.extractIndex = 6 + j;
            }

            b = getLSBit(b, bmp, counterBit % 8);
            counterBit++;

            if (counterBit % 8 == 0) { //entonces junte 1 byte
                byte a = this.rc4.decrypt(new byte[]{b})[0];
                if (a == 0) {
                    flag = false;
                }
                decryption.add(a);
                b = 0;
                counterBit = 0;
            }

            this.extractIndex += this.hope;
        }
        return Bytes.toArray(decryption);
    }

    /**
     * funcion privada asi al menos puedo encontrar el tamaño del archivo
     *
     * @param size tamaño de la extracción
     * @param bmp  foto
     * @return el array de bytes en la estanografia
     */
    private byte[] decrypt(int size, byte[] bmp) {

        byte[] decryption = new byte[size / 8];
        int j = 0;
        int decIndex = 0;

        int counterBit = 0;
        byte b = 0;

        for (int i = 0; i < size; i++) {
            if (this.extractIndex > bmp.length) {
                j++;
                this.extractIndex = 6 + j;
            }

            b = getLSBit(b, bmp, counterBit % 8);
            counterBit++;

            if (counterBit % 8 == 0) { //entonces junte 1 byte
                decryption[decIndex] = b;
                b = 0;
                counterBit = 0;
                decIndex++;
            }
            this.extractIndex += this.hope;
        }
        return decryption;
    }

    private byte getLSBit(byte b, byte[] bmp, int bitPosition) {
        byte bmpByte = bmp[this.extractIndex];
        return (byte) ((b << 1) | (bmpByte & 1)); //todo verificar
    }

    @Override
    public byte[] embeddingCiphered(CipherMessage cipherMessage, byte[] bmp) throws NotEnoughSpaceException {
        return embedding(cipherMessage.toByteArray(), bmp);
    }

    @Override
    public CipherMessage extractCiphered(byte[] bmp) throws WrongLSBStegException {

        if (bmp == null) {
            throw new WrongLSBStegException("BMP is empty");
        }

        this.extractIndex = 6;

        int msgSize = MessageUtils.fromBigEndianBytes(decSize);

        if (msgSize < 0) {
            throw new WrongLSBStegException("Wrong LSB. Size read is negative");
        }

        byte[] encMessage = decrypt(msgSize, bmp);

        return new CipherMessage.CipherMessageBuilder()
                .withCipherSize(msgSize)
                .withCipherBytes(messageParts).build();

    }

    @Override
    public int getMaxSize(byte[] bmp) {
        return (bmp.length / 8) - 6;
    }

    private void setBitValue(byte[] arr, int pos, int value) {
        if (value == 1)
            arr[pos] |= 1;
        else
            arr[pos] &= 254;
    }

    private int getBitValue(byte b, int position) {
        return (b >> position & 1);
    }

    private int getBitValueFromArray(byte[] arr, int bit) {
        int index = bit / 8;
        int bitPosition = 7 - (bit % 8);

        return getBitValue(arr[index], bitPosition);
    }
}
