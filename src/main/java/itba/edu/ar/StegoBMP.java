package itba.edu.ar;

import itba.edu.ar.Utils.StegAlgorithms;

public class StegoBMP {

    StegAlgorithms algorithm;

    public StegoBMP(StegAlgorithms algorithm) {
        this.algorithm = algorithm;
    }

    public void encrypt(){
        switch (algorithm) {
            case LSB1:
                break;
            case LSB4:
                break;
            case LSBI:
                break;
            default:
        }
    }

}
