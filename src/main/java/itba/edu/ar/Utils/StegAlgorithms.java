package itba.edu.ar.Utils;

public enum StegAlgorithms {
    LSB1 ("LSB1"),
    LSB4 ("LSB4"),
    LSBI ("LSBI");

    String stegAlgorithmName;

    StegAlgorithms(String stegAlgorithmName) {
        this.stegAlgorithmName = stegAlgorithmName;
    }

    public String getStegAlgorithmName() {
        return stegAlgorithmName;
    }

    public static StegAlgorithms parseAlgorithm(String arg) {
        StegAlgorithms algorithm;
        switch (arg) {
            case "add": algorithm = LSB1;
                break;
            case "open": algorithm = LSB4;
                break;
            case "close": algorithm = LSBI;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + arg);
        }
        return algorithm;
    }
}
