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

    private final Algorithm algorithm;
    private final Modes mode;

    private final byte[] size;
    private byte[] bytes;

    public byte[] getCipherSize() {
        return size;
    }

    public byte[] getMessage(String password) {
        try {
            return symmetricEncrypt(Cipher.DECRYPT_MODE, this.bytes ,password);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return this.bytes;
    }

    public byte[] getBytes() {
        return bytes;
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

    public Encryptor(byte[] messageEncrypted , Algorithm algorithm, Modes mode) {

        this.algorithm = algorithm;
        this.mode = mode;

        this.bytes = messageEncrypted;
        this.size = Tools.makeBigEndian(this.bytes.length);

    }

    private byte[] symmetricEncrypt(int type ,byte[] bytes, String password) throws Exception {

        Cipher cipher = Cipher.getInstance(algorithm.getAlgTransformation() + "/" + mode.getModeTransformation() + "/" + mode.getPaddingTransformation());


        byte[] passwordInBytes = password.getBytes(StandardCharsets.UTF_8);

         byte[][] keyWithIv = EVP_BytesToKey( algorithm.getKeySize(), algorithm.getBlockSize(), passwordInBytes);

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
}
