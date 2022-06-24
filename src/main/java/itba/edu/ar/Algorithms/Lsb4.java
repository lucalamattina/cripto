package itba.edu.ar.Algorithms;

public class Lsb4 {
/*
    // Cantidad de bits reservados al tamaño
    private static final int SIZE_LENGTH = 32;

    public byte[] embedding(Message message, byte[] bmp, int lsbs) throws NotEnoughSpaceException {
        int messageSize = message.getFileSize();
        if (!canEncrypt(messageSize, bmp)) {
            throw new NotEnoughSpaceException("BMP file is too small for the message");
        }

        byte[] editedBmp = bmp.clone();

        int startIndex = 0;
        byte[] bigEndianSize = MessageUtils.toBigEndianBytes(message.getFileSize());
        encrypt(bigEndianSize, editedBmp, startIndex, lsbs);
        startIndex += bigEndianSize.length * 8 / lsbs;

        encrypt(message.getFileBytes(), editedBmp, startIndex, lsbs);
        startIndex += message.getFileBytes().length * 8 / lsbs;

        byte[] fileExtension = message.getFileExtension().getBytes();
        encrypt(fileExtension, editedBmp, startIndex, lsbs);
        startIndex += fileExtension.length * 8 / lsbs;

        encryptNull(editedBmp, startIndex, lsbs);
        return editedBmp;
    }


    private void encryptNull(byte[] bmp, int startIndex, int lsbs) {
        encrypt(new byte[1], bmp, startIndex, lsbs);
    }

    private void encrypt(byte[] messageToEncrypt, byte[] bmp, int startIndex, int lsbs) {
        int bmpIndex = startIndex;
        for (int messageIndex = 0; messageIndex < messageToEncrypt.length * 8; bmpIndex++) {
            for (int i = lsbs - 1; i >= 0; i--) {
                int bitValue = getBitValueFromArray(messageToEncrypt, messageIndex);
                setBitValue(bmp, bmpIndex, i, bitValue);
                messageIndex++;
            }
        }
    }


    public Message extract(byte[] bmp, int lsbs) throws WrongLSBStegException {
        if (bmp == null)
            throw new WrongLSBStegException("Empty bpm");

        int messageLength = getMessageLength(bmp);
        if(messageLength < 0){
            throw new WrongLSBStegException("Wrong LSB. Size read is negative");
        }

        int messageStartByte = SIZE_LENGTH / lsbs;
        int messageEndByte = messageLength * 8 / lsbs;
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


    public CipherMessage extractCiphered(byte[] bmp, int lsbs) throws WrongLSBStegException {
        if (bmp == null)
            throw new WrongLSBStegException("Bmp empty");

        int messageLength = getMessageLength(bmp);

        if(messageLength < 0){
            throw new WrongLSBStegException("Wrong LSB. Size read is negative");
        }

        int messageStartByte = SIZE_LENGTH / lsbs;
        byte[] decryptedMessage = decrypt(bmp, messageLength, messageStartByte);
        return new CipherMessage.CipherMessageBuilder()
                .withCipherSize(messageLength)
                .withCipherBytes(decryptedMessage)
                .build();
    }


    public int getMaxSize(byte[] bmp, int lsbs) {
        return bmp.length  * lsbs / 8;
    }

    private byte[] decryptExtension(byte[] toDecrypt, int startByte, int lsbs) {
        List<Byte> byteList = new ArrayList<>();
        int readerIndex = 0;
        boolean lastIsNotNull = true;
        for (int decryptIndex = startByte; lastIsNotNull; decryptIndex++) {
            if((readerIndex / 8) + 1 > byteList.size())
                byteList.add((byte)0);

            readerIndex = setValuesToMessageList(readerIndex, toDecrypt[decryptIndex], byteList, lsbs);

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
            readerIndex = setValuesToMessage(readerIndex, toDecrypt[decryptIndex], reader, 4);
        }
        return reader;
    }

    private byte[] decrypt(byte[] toDecrypt, int size) {
        return decrypt(toDecrypt, size, 0);
    }

    private int getMessageLength(byte[] toDecypt, int lsbs) {
        byte[] size = decrypt(toDecypt, SIZE_LENGTH / lsbs);
        return ByteBuffer.wrap(size).getInt();
    }

    private int setValuesToMessageList(int messageIndex, byte toDecrypt, List<Byte> message, int lsbs) {
        for (int i = lsbs - 1; i >= 0; i--) {
            if (getBitValue(toDecrypt, i) > 0) {
                turnBitOn(message, messageIndex);
            }
            messageIndex++;
        }
        return messageIndex;
    }

    private int setValuesToMessage(int messageIndex, byte toDecrypt, byte[] message, int lsbs) {
        for (int i = lsbs - 1; i >= 0; i--) {
            if (getBitValue(toDecrypt, i) > 0) {
                turnBitOn(message, messageIndex);
            }
            messageIndex++;
        }
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

    private boolean canEncrypt(int messageSize, byte[] bmp, int lsbs) {
        int bitsToWrite = messageSize * 8;
        return bmp.length * lsbs >= bitsToWrite;
    }
*/
}
