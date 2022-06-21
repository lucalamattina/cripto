package itba.edu.ar.Utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryptor {

    private static final int ITERATIONS = 1;
    private static final int INDEX_KEY = 0;
    private static final int INDEX_IV = 1;

    private Algorithm algorithm;
    private Modes mode;

    private byte[] size;
    private byte[] bytes;

    private byte[] decryption;

    public byte[] getDecryption() {
        return decryption;
    }

    public byte[] getCipherSize() {
        return size;
    }

    public byte[] getCipherBytes() {
        return bytes;
    }

    public byte[] getMessage(String password) {
        try {
            return symmetricEncrypt(Cipher.DECRYPT_MODE, this.bytes ,password);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return this.bytes;// solo si se rompe
    }


    public Encryptor(Message message, String password , Algorithm algorithm, Modes mode) {

        this.algorithm = algorithm;
        this.mode = mode;
        try {
            this.bytes = symmetricEncrypt(Cipher.ENCRYPT_MODE, message.makeByteArray(), password);
        }catch (Exception e){
            e.printStackTrace();
        }

        this.size = Tools.makeBigEndian(this.bytes.length);
    }

    private byte[] symmetricEncrypt(int type ,byte[] bytes, String password) throws Exception {

        Cipher cipher = Cipher.getInstance(algorithm.getAlgTransformation() + "/" + mode.getModeTransformation() + "/" + mode.getPaddingTransformation());

        System.out.println("Instance Request:" + algorithm.getAlgTransformation() + "/" + mode.getModeTransformation() + "/" + mode.getPaddingTransformation());


        byte[] passwordInBytes = password.getBytes(StandardCharsets.UTF_8);

        // byte[][] keyWithIv = EVPBytesToKeyAndIv(ITERATIONS, passwordInBytes, algorithm.getKeySize(), algorithm.getBlockSize());
         byte[][] keyWithIv = EVP_BytesToKey( algorithm.getKeySize(), algorithm.getBlockSize(), passwordInBytes);

        System.out.println("keyy ADQUIRED");
        byte[] key = keyWithIv[INDEX_KEY];
        byte[] iv = keyWithIv[INDEX_IV];

        SecretKey secretKey = new SecretKeySpec(key, algorithm.getAlgTransformation());

        switch (type){
            case Cipher.ENCRYPT_MODE:
                if (mode.equals(Modes.ECB))
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                else
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
                break;
            case Cipher.DECRYPT_MODE:
                if (mode.equals(Modes.ECB))
                    cipher.init(Cipher.DECRYPT_MODE, secretKey);
                else
                    cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
                break;
            default:
                throw new Exception("No such Encryption type");
        }

        return cipher.doFinal(bytes);
    }

    private byte[][] EVP_BytesToKey(int key_len, int iv_len, byte[] data) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[][] both = new byte[2][];
        byte[] key = new byte[key_len];
        int key_ix = 0;
        byte[] iv = new byte[iv_len];
        int iv_ix = 0;

        both[0] = key;
        both[1] = iv;
        byte[] md_buf = null;
        int nkey = key_len;
        int niv = iv_len;
        int i = 0;

        if (data == null) {
            return both;
        }
        int addmd = 0;
        for (;;) {
            md.reset();
            if (addmd++ > 0) {
                md.update(md_buf);
            }
            md.update(data);

			/*if (null != salt) {
				md.update(salt, 0, 8);
			}*/

            md_buf = md.digest();
            for (i = 1; i < ITERATIONS; i++) {
                md.reset();
                md.update(md_buf);
                md_buf = md.digest();
            }
            i = 0;
            if (nkey > 0) {
                for (;;) {
                    if (nkey == 0)
                        break;
                    if (i == md_buf.length)
                        break;
                    key[key_ix++] = md_buf[i];
                    nkey--;
                    i++;
                }
            }
            if (niv > 0 && i != md_buf.length) {
                for (;;) {
                    if (niv == 0)
                        break;
                    if (i == md_buf.length)
                        break;
                    iv[iv_ix++] = md_buf[i];
                    niv--;
                    i++;
                }
            }
            if (nkey == 0 && niv == 0) {
                break;
            }
        }
        for (i = 0; i < md_buf.length; i++) {
            md_buf[i] = 0;
        }
        return both;
    }


//TODO
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
    }    */


}
