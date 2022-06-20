package itba.edu.ar.Utils;

public enum Modes {
    ECB ("ecb"),
    CFB ("cfb"),
    OFB ("ofb"),
    CBC ("cbc");

    private final String mCase;
    private final String modeTransformation;
    private final String paddingTransformation;


    Modes(String mCase) {
        this.mCase = mCase;

        switch (mCase) {
            case "ecb" :
                this.modeTransformation = "ECB";
                this.paddingTransformation = "PKCS5Padding";
                break;
            case "cbc":
                this.modeTransformation = "CBC";
                this.paddingTransformation = "PKCS5Padding";
                break;
            case "cfb":
                this.modeTransformation = "CFB8";
                this.paddingTransformation = "NoPadding";
                break;
            case "ofb":
                this.modeTransformation = "OFB";
                this.paddingTransformation = "NoPadding";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mCase);
        }
    }

    public String getMCase() {
        return mCase;
    }

    public String getPaddingTransformation() {
        return paddingTransformation;
    }

    public String getModeTransformation() {
        return modeTransformation;
    }
}
