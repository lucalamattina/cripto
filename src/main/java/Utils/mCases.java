package Utils;

public enum mCases {
    ECB ("ecb"),
    CFB ("cfb"),
    OFB ("ofb"),
    CBC ("cbc");

    String mCase;

    mCases(String mCase) {
        this.mCase = mCase;
    }

    public String getMCase() {
        return mCase;
    }

    public static mCases parseMCase(String arg) {
        mCases mCase;
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
