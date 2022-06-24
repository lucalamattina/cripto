package itba.edu.ar;

import itba.edu.ar.Utils.*;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Optional;

public class App {

    @Option(name = "-embed", usage = "Hide data", forbids = {"-extract"})
    private static Boolean embed;

    @Option(name = "-in", usage = "Data to be hidden", depends = {"-embed"}, handler = StringArrayOptionHandler.class)
    private static String[] inFilename = {};

    @Option(name = "-p", usage = "Image in bmp format that will hide the data", required = true, handler = StringArrayOptionHandler.class)
    private static String[] porter;

    @Option(name = "-out", usage = "bmp output file to hide the data in (data + image)", required = true, handler = StringArrayOptionHandler.class)
    private static String[] outFilename;

    @Option(name = "-steg", usage = "Steganography algorithm to be used <LSB1 | LSB4 | LSBI>", required = true)
    private static StegAlgorithms stegoAlgorithm;

    @Option(name = "-a", usage = "Encryption algorithm to be used <aes128 | aes192 | aes256 | des>")
    private static Algorithm algorithm = Algorithm.AES128;

    @Option(name = "-m", usage = "Encryption mode <ecb | cfb | ofb | cbc>")
    private static Modes mode = Modes.CBC;

    @Option(name = "-pass", usage = "Encryption password", handler = StringArrayOptionHandler.class)
    private static String[] password = {};

    @Option(name = "-extract", usage = "Extract data", forbids = {"-embed"})
    private static Boolean extract;

    private static StegoBMP steganography;
    private static boolean encrypted;

    public static void main(String[] args) {

        final CmdLineParser cmdParser = new CmdLineParser(new App());
        try {
            cmdParser.parseArgument(args);

            if( embed!= null && extract!=null )
                throw new CmdLineException(cmdParser,"Option -embed and -extract cant both be used. Chose one.", new Throwable());
            if (embed!= null && inFilename==null)
                throw new CmdLineException(cmdParser,"Option -in must be present when -embed is used.", new Throwable());
            if(embed==null && extract == null)
                throw new CmdLineException(cmdParser,"Option -embed or -extract must be used.", new Throwable());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            cmdParser.printUsage(System.err);
            System.exit(1);
        }

        Optional<String> pass = Arrays.stream(password).findFirst();
        if(pass.isPresent()){
            encrypted = true;
        }


       if(embed != null){
           try {
               if (inFilename.length > 1)
                   throw new CmdLineException(cmdParser,"in Path Is incorrect", new Throwable());
               steganography = new StegoBMP(stegoAlgorithm, inFilename[0]);

              steganography.readMessage();


              if(encrypted){
                  steganography.encrypt(password[0], algorithm, mode);
              }
            //  steganography.steg();
           }catch (Exception e){
               System.out.println(e.getMessage());
               System.exit(1);
           }

       }


        if(true){//extract != null){
            try {
                Message message = steganography.getMessageFile();
                if (encrypted) {
                    Encryptor encryptedMsg = steganography.getEncryptedMessage();


                    message = steganography.decrypt(password[0]);

                }
                /*else {

                   message = lsb.extract(holderBmp.getPixelData());
                }*/


                File outFile = new File(outFilename[0] + message.getFileExtension());

                OutputStream os = new FileOutputStream(outFile);
                os.write(message.getFileBytes());
                os.close();

            }catch (Exception e){
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }

    }
    private static String hexStringFromBytes(byte[] b) {
        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder hex = new StringBuilder();
        int msb;
        int lsb;
        int j;
        for (j = 0; j < b.length; j++) {
            msb = ((int) b[j] & 0x000000FF) / 16;
            lsb = ((int) b[j] & 0x000000FF) % 16;
            hex.append(hexChars[msb]).append(hexChars[lsb]);
        }
        return (hex.toString());
    }
}

