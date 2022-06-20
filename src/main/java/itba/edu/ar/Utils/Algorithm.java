package itba.edu.ar.Utils;

public enum Algorithm {
    AES128 ("aes128"),
    AES192 ("aes192"),
    AES256 ("aes256"),
    DES ("des");

    private String aCase;
    private int keySize;
    private int blockSize;
    private String algTransformation;

    Algorithm(String aCase) {

        this.aCase = aCase;
        switch (aCase) {
            case "aes128":
                this.keySize = 128 / 8;
                this.blockSize = 128 / 8;
                this.algTransformation = "AES";
                break;
            case "aes192":
                this.keySize = 192 / 8;
                this.blockSize = 128 / 8;
                this.algTransformation = "AES";
                break;
            case "aes256":
                this.keySize = 256 / 8;
                this.blockSize = 128 / 8;
                this.algTransformation = "AES";
                break;
            case "des":
                this.keySize = (56 + 8) / 8;
                this.blockSize = 64 / 8;
                this.algTransformation = "DES";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + aCase);
        }
    }

      public String getACase() {
        return aCase;
    }

    public int getKeySize() {
        return keySize;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public String getAlgTransformation() {
        return algTransformation;
    }
}
