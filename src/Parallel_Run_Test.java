import crypto.GammaForGOST;
import crypto.GammaForGOST_Parallel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Parallel_Run_Test {
    private int kB;
    private int countOfThreads;
    private boolean isCounter = true;


    private final static long TOKB = 128;
    private final static long C232 = GammaForGOST.C232;
    private final static long DATA = 0xfedcba9876543210L;
    private final static long[] KEYS = {         /*8 подключей из ГОСТ Р 34.12 - 2015 */
            0xffeeddcc, 0xbbaa9988, 0x77665544, 0x33221100, 0xf0f1f2f3, 0xf4f5f6f7, 0xf8f9fafb, 0xfcfdfeff};

    private final static int[][] SBOX = {             /* Таблица замен из ГОСТ Р 34.12 - 2015 */
            {12, 4, 6, 2, 10, 5, 11, 9, 14, 8, 13, 7, 0, 3, 15, 1},
            {6, 8, 2, 3, 9, 10, 5, 12, 1, 14, 4, 7, 11, 13, 0, 15},
            {11, 3, 5, 8, 2, 15, 10, 13, 14, 1, 7, 4, 12, 9, 6, 0},
            {12, 8, 2, 1, 13, 4, 15, 6, 7, 0, 10, 5, 3, 14, 9, 11},
            {7, 15, 5, 10, 8, 1, 6, 13, 0, 9, 3, 14, 11, 4, 2, 12},
            {5, 13, 15, 6, 9, 2, 12, 10, 11, 7, 8, 1, 4, 3, 14, 0},
            {8, 14, 2, 5, 6, 9, 1, 12, 15, 4, 11, 0, 13, 10, 3, 7},
            {1, 7, 14, 13, 0, 5, 8, 3, 4, 15, 10, 6, 9, 12, 11, 2}
    };

    //////////////////////////////////////////////////////////
    ///  Constructors
    /////////////////////////////////////////////////////////
    //-----Constructors begin-------
    public Parallel_Run_Test() {
        this.kB = 1;
        this.countOfThreads = 1;
    }

    public Parallel_Run_Test(int kB, int countOfThreads) {
        this.kB = kB;
        this.countOfThreads = countOfThreads;
    }

    public Parallel_Run_Test(int kB, int countOfThreads, boolean isCounter) {
        this.kB = kB;
        this.countOfThreads = countOfThreads;
        this.isCounter = isCounter;
    }
    //-----Constructors end---------


    //////////////////////////////////////////////////////////
    ///  Method test
    /////////////////////////////////////////////////////////
    public void test() {
        //-----Variables begin----------
        long t;

        long seriallyTime;

        long[] parallelTime;
        float[] ku;
        if (this.isCounter) {
            parallelTime = new long[this.countOfThreads];
            ku = new float[this.countOfThreads];
        } else {
            parallelTime = new long[1];
            ku = new float[1];
        }

        List<Long> dataList = prepareData();
        //-----Variables end------------


        //-----Serially test begin------
        GammaForGOST gfg = new GammaForGOST(dataList, KEYS, SBOX);
        t = System.currentTimeMillis();
        gfg.cryptForGama();
        seriallyTime = System.currentTimeMillis() - t;
        //-----Serially test end--------


        //-----Parallel test begin------
        GammaForGOST_Parallel gfgp = new GammaForGOST_Parallel(dataList, KEYS, SBOX, DATA);
        for (int i = 0; i < this.countOfThreads; i++) {
            gfgp.setNumberOfThread(i + 1);
            t = System.currentTimeMillis();
            gfgp.cryptForGama();
            parallelTime[i] = System.currentTimeMillis() - t;
            ku[i] = (seriallyTime * 1.0f) / parallelTime[i];
        }
        //-----Parallel test end--------


        //-----Print begin--------------
        printResult(seriallyTime, parallelTime, ku);
        printSysData();
        //-----Print end----------------

    }

    //////////////////////////////////////////////////////////
    ///  Method prepareData
    /////////////////////////////////////////////////////////
    private List<Long> prepareData() {
        List<Long> longList = new ArrayList<Long>();

        for (long i = 0; i < (TOKB * this.kB); i++) {
            long nL = ((DATA & (C232 - 1)) + i) & (C232 - 1);
            long nH = ((DATA >>> 32) + i) & (C232 - 1);
            longList.add((nH << 32) | nL);
        }

        return longList;
    }


    //////////////////////////////////////////////////////////
    ///  Method printResult
    /////////////////////////////////////////////////////////

    public void printResult(long seriallyTime, long[] parallelTime, float[] ku) {

        System.out.println("-----------------------------------------------------");
        System.out.println("Time of serially process: " + seriallyTime + "\n\nParallel process:\n");

        System.out.printf("%-10s", "Threads:");
        for (int i = 0; i < this.countOfThreads; i++) {
            System.out.printf("%6d", (i + 1));
        }

        System.out.printf("%n%-10s", "Time:");
        for (int i = 0; i < this.countOfThreads; i++) {
            System.out.printf("%6x", parallelTime[i]);
        }

        System.out.printf("%n%-10s", "Speedup:");
        for (int i = 0; i < this.countOfThreads; i++) {
            System.out.printf("%6.3f", ku[i]);
        }

        System.out.println("\n-----------------------------------------------------");
    }

    //////////////////////////////////////////////////////////
    ///  Method printSysData
    /////////////////////////////////////////////////////////
    public void printSysData(){
        Runtime runtime = Runtime.getRuntime();
        NumberFormat format = NumberFormat.getInstance();
        StringBuilder sb = new StringBuilder();

        sb.append("OS: ");
        sb.append(System.getProperty("os.name"));
        sb.append("<br/>");
        sb.append("Version: ");
        sb.append(System.getProperty("os.version"));
        sb.append("<br/>");
        sb.append(": ");
        sb.append(System.getProperty("os.arch"));
        sb.append("<br/>");
        sb.append("Available processors (cores): ");
        sb.append(runtime.availableProcessors());
        sb.append("<br/>");
        sb.append("Max memory: ");
        sb.append(format.format(runtime.maxMemory() / 1024));
        sb.append("<br/>");

        System.out.println("Systems behavior:");
        System.out.println(sb.toString());
    }
}
