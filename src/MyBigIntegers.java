import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class MyBigIntegers {

    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    String value;
    static int numberOfTrials = 100000;
    static int MAXINPUTSIZE  = 100;
    static int MININPUTSIZE  =  1;
    boolean positive = true;

    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time
    static String ResultsFolderPath = "/home/nicolocker/Results/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    public static void main(String[] args) {

        //verifyWorks();
        System.out.println("\n");

        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
        System.out.println("Running first full experiment...");
        runFullExperiment("MyBigInt-Exp1.txt");
        System.out.println("Running second full experiment...");
        runFullExperiment("MyBigInt-Exp2.txt");
        System.out.println("Running third full experiment...");
        runFullExperiment("MyBigInt-Exp3.txt");
    }

    /*
    public static void verifyWorks(){
        MyBigIntegers str1 = "1 + 9999999999999999999999999999999999999999999999999";
        System.out.println(Plus(str1));
    }
     */


    public static void runFullExperiment(String resultsFileName){

        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch(Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
            return; // not very foolproof... but we do expect to be able to create/open the file...
        }

        ThreadCpuStopWatch BatchStopwatch = new ThreadCpuStopWatch(); // for timing an entire set of trials
        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial

        resultsWriter.println("#Fib Num       AvgTime"); // # marks a comment in gnuplot data
        resultsWriter.flush();
        /* for each size of input we want to test: in this case starting small and doubling the size each time */

        for(int inputSize=MININPUTSIZE;inputSize<=MAXINPUTSIZE; inputSize++) {
            // progress message...
            System.out.println("  Running test ... ");

            long batchElapsedTime = 0;
            System.gc();

            // run the tirals
            for (long trial = 0; trial < numberOfTrials; trial++) {

                TrialStopwatch.start();
                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime();

            }

            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials; // calculate the average time per trial in this batch

            long N = (long)(Math.floor(Math.log(inputSize)/Math.log(2)));
            /* print data for this size of input */
            resultsWriter.printf("%12d     %15.2f\n",inputSize, averageTimePerTrialInBatch); // might as well make the columns look nice
            resultsWriter.flush();
            System.out.println(" ....done.");

        }
    }

    public MyBigIntegers() {
        String value = String.valueOf(new MyBigIntegers());
        value = "0";
    }

    public MyBigIntegers(String number) {
        if (number.charAt(0) == '-')
        {
            positive = false;
            number = number.substring(1);
        }
        for(int i = 0; i < number.length(); i++)
        {
            value = String.valueOf((int)number.charAt(i));
        }
    }

    public MyBigIntegers( String value, boolean positive) {
        this.value = value;
        this.positive = positive;
    }

    public String ToString(){
        return value.toString();
    }

    // https://www.geeksforgeeks.org/sum-two-large-numbers/
    // modified to use MyBigIntergers Class
    public MyBigIntegers Plus(MyBigIntegers number){
        String str1 = this.value;
        String str2 = number.value;
        String ans = " ";
        int carry = 0;

        //makes sure str2 is longer
        if(str1.length() > str2.length()){
            String temp;
            temp = str1;
            str1 = str2;
            str2 = temp;
        }

        //reverse both strings
        str1 = new StringBuilder(str1).reverse().toString();
        str2 = new StringBuilder(str2).reverse().toString();

        //gets single digit to add
        for(int i = 0; i < str1.length(); i++){
            int sum = ((int)(str1.charAt(i) - '0') +
                    (int)(str2.charAt(i) - '0') + carry);
            ans += (char)(sum % 10 + '0');

            // gets carry for next step
            carry = sum / 10;
        }

        //add remaining digits
        for (int i = str1.length(); i < str1.length(); i++)
        {
            int sum = ((int)(str2.charAt(i) - '0') + carry);
            ans += (char)(sum % 10 + '0');
            carry = sum / 10;
        }

        // Add remaining carry
        if (carry > 0)
            ans += (char)(carry + '0');

        // reverse answer string back
        ans = new StringBuilder(ans).reverse().toString();

        return new MyBigIntegers(ans);
    }

    //https://www.geeksforgeeks.org/multiply-large-numbers-represented-as-strings/
    // modified to use MyBigIntegers class
    public MyBigIntegers Times(MyBigIntegers numberA, MyBigIntegers numberB){
        String str1 = numberA.value;
        String str2 = numberB.value;
        int len1 = str1.length();
        int len2 = str2.length();

        if (len1 == 0 || len2 == 0) {
            return new MyBigIntegers("0");
        }

        // keeps result number in reverse order
        int result[] = new int[len1 + len2];

        // find positions in result
        int pos1 = 0;
        int pos2 = 0;

        // Go from right to left
        for (int i = len1 - 1; i >= 0; i--)
        {
            int carry = 0;
            int n1 = (int)(str1.charAt(i) - '0');

            // To shift position to left
            pos2 = 0;

            // Go from right to left
            for (int j = len2 - 1; j >= 0; j--)
            {
                // Take current digit of second number
                int n2 = (int)(str1.charAt(j) - '0');

                // Multiply with current digit of first number and add result to previously stored result
                int sum = n1 * n2 + result[pos1 + pos2] + carry;

                // Carry for next loop through
                carry = sum / 10;
                result[pos1 + pos2] = sum % 10;
                pos2++;
            }

            // store carry
            if (carry > 0)
                result[pos1 + pos2] += carry;

            // To shift position to left after every
            pos1++;
        }

        // ignore '0's from the right
        int i = result.length - 1;
        while (i >= 0 && result[i] == 0)
            i--;

        // If all were '0's - means either both
        // or one of num1 or num2 were '0'
        if (i == -1)
            return new MyBigIntegers("0");

        // genarate the result String
        String s = "";

        while (i >= 0)
            s += (result[i--]);

        return new MyBigIntegers(s);
    }

    //https://www.geeksforgeeks.org/difference-of-two-large-numbers/
    // modified for MyBigInteger
    static boolean isSmaller(MyBigIntegers numberA, MyBigIntegers numberB) {
        String str1 = numberA.value;
        String str2 = numberB.value;

        // Calculate lengths of both string
        int n1 = str1.length(), n2 = str2.length();

        if (n1 < n2)
            return true;
        if (n2 > n1)
            return false;

        for (int i = 0; i < n1; i++)
        {
            if (str1.charAt(i) < str2.charAt(i))
                return true;
            else if (str1.charAt(i) > str2.charAt(i))
                return false;
        }
        return false;
    }

    //https://www.geeksforgeeks.org/difference-of-two-large-numbers/
    // modified for MyBigInteger
    static MyBigIntegers Subtract(MyBigIntegers numberA, MyBigIntegers numberB) {
        String str1 = numberA.value;
        String str2 = numberB.value;

        // Before proceeding further, make sure str1
        // is not smaller
        if (isSmaller(numberA, numberB))
        {
            String t = str1;
            str1 = str2;
            str2 = t;
        }

        // Take an empty string for storing result
        String str = "";

        // Calculate lengths of both string
        int n1 = str1.length(), n2 = str2.length();
        int diff = n1 - n2;

        // Initially take carry zero
        int carry = 0;

        // Traverse from end of both strings
        for (int i = n2 - 1; i >= 0; i--)
        {
            // Do school mathematics, compute difference of
            // current digits and carry
            int sub = (((int)str1.charAt(i + diff) - (int)'0') -
                    ((int)str2.charAt(i) - (int)'0') - carry);
            if (sub < 0)
            {
                sub = sub+10;
                carry = 1;
            }
            else
                carry = 0;

            str += String.valueOf(sub);
        }

        // subtract remaining digits of str1[]
        for (int i = n1 - n2 - 1; i >= 0; i--)
        {
            if (str1.charAt(i) == '0' && carry > 0)
            {
                str += "9";
                continue;
            }
            int sub = (((int)str1.charAt(i) - (int)'0') - carry);
            if (i > 0 || sub > 0) // remove preceding 0's
                str += String.valueOf(sub);
            carry = 0;

        }

        // reverse resultant string
        str = new StringBuilder(str).reverse().toString();

        return new MyBigIntegers(str);
    }
}
