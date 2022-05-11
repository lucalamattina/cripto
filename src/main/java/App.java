import Utils.*;

import java.util.EnumSet;
import java.util.Optional;

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

        System.out.println(embed);
        System.out.println(inFileName);
        System.out.println(carrierFileName);
        System.out.println(outFileName);
        System.out.println(stegAlgorithm);
        System.out.println(a);
        System.out.println(m);
        System.out.println(password);
        System.out.println(extract);
    }

    private static void parseArguments(){
        try {
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