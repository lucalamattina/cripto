package itba.edu.ar;

import itba.edu.ar.Utils.*;

public class App {

    private static String embed;
    private static String inFileName;
    private static String carrierFileName;
    private static String outFileName;
    private static StegAlgorithms stegAlgorithm;
    private static aCases a;
    private static mCases m;
    private static String password;
    private static String extract;

    public static void main(String[] args) {

        parseArguments();

    }

    private static void parseArguments(){
        try {
            System.out.println(System.getProperty("p"));

            embed = System.getProperty(Arguments.EMBED.getArgumentName());
            extract = System.getProperty(Arguments.EXTRACT.getArgumentName());

            inFileName = System.getProperty(Arguments.IN.getArgumentName());

            carrierFileName = System.getProperty(Arguments.CARRIER.getArgumentName());
            outFileName = System.getProperty(Arguments.OUT.getArgumentName());
            stegAlgorithm = StegAlgorithms.valueOf(System.getProperty(Arguments.STEG.getArgumentName()));

            a = aCases.valueOf(System.getProperty(OptionalArguments.A.getArgumentName()));
            m = mCases.valueOf(System.getProperty(OptionalArguments.M.getArgumentName()));
            password = System.getProperty(OptionalArguments.PASS.getArgumentName());

            if ((extract !=null && inFileName!=null) || (embed!=null && inFileName==null)){//extract!=null && embed!=null included
                throw new IllegalArgumentException("Excess or lack of arguments");
            }


        }catch (Exception e){

        }
    }

}