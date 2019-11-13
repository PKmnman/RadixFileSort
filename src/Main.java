import javax.print.DocFlavor;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Main {

    public static final URL FILE = Main.class.getResource("/files/file.dat");
    public static final URL ONE = Main.class.getResource("/files/one.dat");
    public static final URL ZERO = Main.class.getResource("/files/zero.dat");

    private static final int AMT_OF_NUMBERS = 10;

    public static void main(String[] args) throws IOException {

        //Open binary file for read/write operations
        File fileFile = null;
        File fileOne = null;
        File fileZero = null;

        try {
            fileFile = new File(FILE.toURI());
            fileOne = new File(ONE.toURI());
            fileZero = new File(ZERO.toURI());
        } catch (URISyntaxException e) {
            System.err.println("Failed to load file");
            System.exit(-1);
        }

        RandomAccessFile file = new RandomAccessFile(fileFile, "rw");
        RandomAccessFile one = new RandomAccessFile(fileOne, "rw");
        RandomAccessFile zero = new RandomAccessFile(fileZero, "rw");

        createFile(file);

        System.out.println("Unsorted:");
        printFile(file);
        sortFile(file, zero, one);

        System.out.println("Sorted:");
        printFile(file);

        file.close();
        one.close();
        zero.close();

    }

    //Reads and prints file to console
    private static void printFile(RandomAccessFile file) throws IOException {
        file.seek(0);
        for (int i = 0; i < AMT_OF_NUMBERS; i++) {
            int unsignedInt = file.readInt();
            System.out.printf("%12d%n",unsignedInt);
        }
        System.out.println();
    }

    //creates and writes 10 random unsigned binary ints to file
    private static void createFile(RandomAccessFile file) throws IOException {
        Random random = new Random();
        for (int i = 0; i < AMT_OF_NUMBERS; i++) {
            int unsignedInt = random.nextInt(0x7fffffff);
            file.writeInt(unsignedInt);
        }
        file.seek(0);
    }

    //converts int to int[] binary
    private static int[] convertToBinary(int number) {
        int[] binaryNum = new int[32];
        String str = Integer.toBinaryString(number);
        if (str.length() != 32) {
            int diff = 32 - str.length();
            String temp = "";
            for (int i = 0; i < diff; i++) {
                temp += "0";
            }
            temp += str;
            str = temp;
        }

        for (int i = 0; i < 32; i++) {
            binaryNum[i] = Integer.parseInt(str.charAt(i) + "");
        }

        return binaryNum;

    }

    //Sorts file using radix sort
    private static void sortFile(RandomAccessFile file, RandomAccessFile zero, RandomAccessFile one) throws IOException {
        file.seek(0);
        for (int i = 31; i >= 0; i--) {  //bit selector
            int z = 0;  //amount of numbers in zero file
            int o = 0;  //amount of numbers in one file
            for (int j = 0; j < AMT_OF_NUMBERS; j++) {  //number selector
                int m = file.readInt();  //reads the selected bit of the number
                int[] mArr = convertToBinary(m);
                if (mArr[i] == 0) {
                    zero.writeInt(m);  //bit was zero so the number goes to the zero file
                    z++;  //number added to the zero file
                } else {
                    one.writeInt(m);  //bit was one so the number goes to the one file
                    o++;  //number added to the one file
                }
            }
            one.seek(0);
            for (int k = 0; k < o; k++) {  //appends one to zero
                zero.writeInt(one.readInt());
            }

            RandomAccessFile temp = zero;  //swaps file and zero
            zero = file;
            file = temp;

            one.seek(0);
            zero.seek(0);
            file.seek(0);
        }
    }


}
