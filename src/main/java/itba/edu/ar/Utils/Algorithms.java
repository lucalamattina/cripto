package itba.edu.ar.Utils;

public enum Algorithms {
    AES128 ("aes128"),
    AES192 ("aes192"),
    AES256 ("aes256"),
    DES ("des");

    String aCase;

    Algorithms(String aCase) {
        this.aCase = aCase;
    }

    public String getACase() {
        return aCase;
    }

    public static Algorithms parseACases(String arg) {
        Algorithms aCase;
        switch (arg) {
            case "aes128": aCase = AES128;
                break;
            case "aes192": aCase = AES192;
                break;
            case "aes256": aCase = AES256;
                break;
            case "des": aCase = DES;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + arg);
        }
        return aCase;
    }
}
