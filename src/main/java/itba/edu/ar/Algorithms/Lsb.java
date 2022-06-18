package itba.edu.ar.Algorithms;

public class Lsb {
    byte[] embedding(Message message, byte[] bmp) throws NotEnoughSpaceException;
    Message extract(byte[] bmp) throws WrongLSBStegException;
    byte[] embeddingCiphered(CipherMessage cipherMessage, byte[] bmp) throws NotEnoughSpaceException;
    CipherMessage extractCiphered(byte[] bmp) throws WrongLSBStegException;
    int getMaxSize(byte[] bmp);
}
