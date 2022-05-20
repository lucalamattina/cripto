package itba.edu.ar.Utils;

public enum Modes {
    ECB ("ecb"),
    CFB ("cfb"),
    OFB ("ofb"),
    CBC ("cbc");

    String mCase;

    Modes(String mCase) {
        this.mCase = mCase;
    }

    public String getMCase() {
        return mCase;
    }

    public static Modes parseMCase(String arg) {
        Modes mCase;
        switch (arg) {
            case "ecb": mCase = ECB;
                break;
            case "cfb": mCase = CFB;
                break;
            case "ofb": mCase = OFB;
                break;
            case "cbc": mCase = CBC;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + arg);
        }
        return mCase;
    }
}
