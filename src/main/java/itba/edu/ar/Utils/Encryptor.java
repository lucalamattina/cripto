package itba.edu.ar.Utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

public class Encryptor {
    private final static int COUNT = 1;
    private final Algorithm algorithm;
    private final Modes mode;
    private final EncriptionPadding padding;

    private byte[] size;
    private byte[] bytes;

    public byte[] getCipherSize() {
        return size;
    }

    public byte[] getCipherBytes() {
        return bytes;
    }

    public Encryptor(BMP message , String password , Algorithm algorithm, Modes mode) {

        this.algorithm = algorithm;
        this.mode = mode;

        this.size = cipher.simetricEncript(message.toByteArray(), password);
        //necesito algoritmo modo y padding


        this.bytes = Tools.bigEndian(bytes.length);
    }

    public String getTransformation() {
        return algorithm.getRepresentation() + "/" + mode.getRepresentation() + "/" + padding.getRepresentation();
    }

    private byte[] cipher(int i, byte[] bytes, String password) throws Exception {
        Cipher cipher = Cipher.getInstance(getTransformation());

        /*
            The size of the IV depends on the mode, but typically it is the same size as the block size
        */

        byte[] passwordBytes = (password).getBytes(StandardCharsets.UTF_8);
        byte[][] keyAndIv = EVPBytesToKeyAndIv(COUNT, passwordBytes, algorithm.getKeySize(), algorithm.getBlockSize());
        byte[] key = keyAndIv[0];
        byte[] iv = keyAndIv[1];

        SecretKey secretKey = new SecretKeySpec(key, algorithm.getRepresentation());
        switch (mode) {
            case CBC:
            case CFB:
            case OFB:
                cipher.init(i, secretKey, new IvParameterSpec(iv));
                break;
            case ECB:
                cipher.init(i, secretKey);
                break;
        }
        return cipher.doFinal(bytes);
    }

    public byte[] simetricEncript(byte[] bytes, String password) throws Exception {
        return cipher(Cipher.ENCRYPT_MODE, bytes, password);
    }

    public byte[] simetricDecript(byte[] bytes, String password) throws Exception {
        return cipher(Cipher.DECRYPT_MODE, bytes, password);
    }

    /*
        Para generar clave e iv a partir de una password, se puede utilizar la función EVP_BytesToKey().

        int EVP_BytesToKey (const EVP_CIPHER *type, EVP_MD *md, const unsigned char *salt, const unsigned char *data, int datal, int count, unsigned char *key, unsigned char *iv)

        EVP_BytesToKey() devuelve la longitud de la clave correspondiente al algoritmo de cifrado especificado en el
        argumento type.
        Para obtener la clave y el vector de inicialización, a partir del password indicado en el parámetro data,
        de longitud datal, la función realiza un algoritmo que involucra una o más iteraciones
        (según el valor del argumento count) de transformación en las que un algoritmo de hash especificado en el
        argumento md va obteniendo una clave del tamaño deseado. El parámetro salt es opcional (puede ser NULL).
        El algoritmo es:

        md_buf = H (data + salt)
        MIENTRAS cierto HACER
            PARA i = 1 HASTA count-1 HACER
                md_buf = H (md_buf)
            FINPARA

            SI key no completa ENTONCES
                key = bytes de md_buf (son las restantes, mantener un indice de las ya vistas)
            FINSI

            SI iv no completo ENTONCES
                iv = bytes de md_buf (son las restantes, mantener un indice de las ya vistas)
            FINSI

            SI iv Y key completos ENTONCES
                SALIR del MIENTRAS
            SINO
                md_buf = H (md_buf + data + salt)
            FINSI
        FIN MIENTRAS
     */
    /*
        Funcion equivalente a EVP_BytesToKey() de openSSL para generara una key y iv a partir de una password.
        Usamos como funcion de hash SHA-256, ya que es la que pide la catedra.
        No incluimos slat, ya que asi lo pide la catedra.
    */
    private byte[][] EVPBytesToKeyAndIv(int iterations, byte[] data, int keySize, int ivSize) throws NoSuchAlgorithmException {
        byte[] mdBuff = Hashing.sha256(data);
        byte[] key = new byte[keySize];
        int keyIndex = 0;
        byte[] iv = new byte[ivSize];
        int ivIndex = 0;
        while (true) {
            for (int i = 1; i < iterations; i++) {
                mdBuff = Hashing.sha256(mdBuff);
            }
            int mdBuffIndex = 0;
            if (keyIndex < key.length) {
                for (; (mdBuffIndex < mdBuff.length) && (keyIndex < key.length); mdBuffIndex++, keyIndex++) {
                    key[keyIndex] = mdBuff[mdBuffIndex];
                }
            }
            if (ivIndex < iv.length && mdBuffIndex < mdBuff.length) {
                for (; (mdBuffIndex < mdBuff.length) && (ivIndex < iv.length); mdBuffIndex++, ivIndex++) {
                    iv[ivIndex] = mdBuff[mdBuffIndex];
                }
            }
            if (ivIndex == iv.length && keyIndex == key.length) {
                break;
            } else {
                byte[] aux = new byte[mdBuff.length + data.length];
                System.arraycopy(mdBuff, 0, aux, 0, mdBuff.length);
                System.arraycopy(data, 0, aux, mdBuff.length, data.length);
                mdBuff = Hashing.sha256(aux);
            }
        }
        byte[][] keyAndIv = new byte[2][];
        keyAndIv[0] = key;
        keyAndIv[1] = iv;
        return keyAndIv;
    }

}
