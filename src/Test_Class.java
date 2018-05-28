import crypto.GOST;
import crypto.GammaForGOST;
import crypto.GammaForGOST_Parallel;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Test_Class {
    static Scanner cs = new Scanner(System.in);



    static long C232 = GammaForGOST.C232;
    static long[] keys = {         /*8 подключей из ГОСТ Р 34.12 - 2015 */
            0xffeeddcc, 0xbbaa9988, 0x77665544, 0x33221100, 0xf0f1f2f3, 0xf4f5f6f7, 0xf8f9fafb, 0xfcfdfeff };

    static int [][] sBox = {             /* Таблица замен из ГОСТ Р 34.12 - 2015 */
            {12, 4, 6, 2, 10, 5, 11, 9, 14, 8, 13, 7, 0, 3, 15, 1},
            {6, 8, 2, 3, 9, 10, 5, 12, 1, 14, 4, 7, 11, 13, 0, 15},
            {11, 3, 5, 8, 2, 15, 10, 13, 14, 1, 7, 4, 12, 9, 6, 0},
            {12, 8, 2, 1, 13, 4, 15, 6, 7, 0, 10, 5, 3, 14, 9, 11},
            {7, 15, 5, 10, 8, 1, 6, 13, 0, 9, 3, 14, 11, 4, 2, 12},
            {5, 13, 15, 6, 9, 2, 12, 10, 11, 7, 8, 1, 4, 3, 14, 0},
            {8, 14, 2, 5, 6, 9, 1, 12, 15, 4, 11, 0, 13, 10, 3, 7},
            {1, 7, 14, 13, 0, 5, 8, 3, 4, 15, 10, 6, 9, 12, 11, 2}
    };

    public static void main(String[] args) {
        String messageText = "Please select the test:\n" +
                "0\t ---exit;\n" +
                "1\t ---GOST;\n" +
                "2\t ---Graphic test of random;\n" +
                "3\t ---Gamma test;\n" +
                "4\t ---Gamma test parallel.\n";


        int i;
        do {
//            System.out.println("Please select the test:");
//            System.out.println("0\t ---exit;");
//            System.out.println("1\t ---GOST;");
//            System.out.println("2\t ---Graphic test of random.");
            System.out.print(messageText);

            i = cs.nextInt();
            switch (i){
                case 0:
                    break;
                case 1:
                    testGOST();
                    break;
                case 2:
                    graphicTest();
                    break;
                case 3:
                    gamaTest();
                    break;
                case 4:
                    gamaTestParallel();
                    break;
                default:
                    break;
            }
        }while (i != 0);
    }

    private static void testGOST() {
        long data = 0xfedcba9876543210L;
        GOST gost = new GOST(data, keys, sBox);

        gost.encrypt32();
        data = gost.getDataBlock();
        System.out.printf("%x%n", data);
        System.out.println("------------------------------------------");

        gost.setDataBlock(data);
        gost.decrypt32();

        data = gost.getDataBlock();
        System.out.printf("%x%n", data);
        System.out.println("------------------------------------------");
        System.out.println("------------------------------------------");



    }

    private static void graphicTest() {
        //Count of random numbers:
        int col = 100;

        //Graphic Test:
        GraphicTestOfRandom gtr = new GraphicTestOfRandom();

        //Names of random:
        String nameA = "Random";
        String nameB = "SecureRandom (Java)";
        String nameC = "SecureRandom (Sun Security Provider)";

        //Randoms:
        Random rd = new Random();
        SecureRandom srj = new SecureRandom();
        sun.security.provider.SecureRandom srssp = new sun.security.provider.SecureRandom();

        //Arrays:
        int[] a = new int[col];
        int[] b = new int[col];
        int[] c = new int[col];

        //Preparing arrays:
        for (int i = 0; i < col; i++){
            //Random:
            a[i] = rd.nextInt(10);

            //SecureRandom Java:
            b[i] = srj.nextInt(10);

            //SecureRandom SSP:
            c[i] = -1;
            while (c[i] < 0 || c[i] > 9){
                byte[] bt = new byte[1];
                srssp.engineNextBytes(bt);
                c[i] = bt[0] & 15;
                if(c[i] < 0 || c[i] > 9){
                    c[i] = bt[0] >>> 4;
                }
            }
        }

        //Output MAN information:
        gtr.printMan();

        //Test Random:
        gtr.test(nameA, a);
        gtr.test(nameB, b);
        gtr.test(nameC, c);
    }

    private static void gamaTest() {
        long data = 0xfedcba9876543210L;
        List<Long> dataList = new ArrayList<Long>();
        List<Long> encryptDataList;
        long sp;

        dataList.add(data);
        dataList.add(data);
        dataList.add(data);

        GammaForGOST gfg = new GammaForGOST(dataList, keys, sBox);

        encryptDataList = gfg.cryptForGama();
        sp = gfg.getSynchronizedPost();

        System.out.println("%nOpen text:");
        for (long item: dataList){
            System.out.printf("%x%n", item);
        }

        System.out.println("Encrypt text:");

        for (long item: encryptDataList){
            System.out.printf("%x%n", item);
        }

        gfg = new GammaForGOST(encryptDataList, keys, sBox, sp);
        dataList = gfg.cryptForGama();

        System.out.println("Decrypt text:");
        for (long item: dataList){
            System.out.printf("%x%n", item);
        }
        System.out.println();
    }


    private static void gamaTestParallel() {
        long data = 0xfedcba9876543210L;
        List<Long> dataList = new ArrayList<Long>();
        List<Long> encryptDataList;
        long sp;

        //-----preparing data-----
        for (int i = 0; i < 10; i++){
            long nL =((data & (C232 - 1)) + i) & (C232 - 1);
            long nH =((data >>> 32) + i) & (C232 - 1);
            dataList.add((nH << 32) | nL);
        }


        GammaForGOST_Parallel gfgp = new GammaForGOST_Parallel(dataList, keys, sBox);

        gfgp.setNumberOfThread(1);
        encryptDataList = gfgp.cryptForGama();
        sp = gfgp.getSynchronizedPost();

        System.out.println("%nOpen text:");
        for (long item: dataList){
            System.out.printf("%x%n", item);
        }

        System.out.println("Encrypt text:");

        for (long item: encryptDataList){
            System.out.printf("%x%n", item);
        }

        gfgp = new GammaForGOST_Parallel(encryptDataList, keys, sBox, sp);
        gfgp.setNumberOfThread(1);
        dataList = gfgp.cryptForGama();

        System.out.println("Decrypt text:");
        for (long item: dataList){
            System.out.printf("%x%n", item);
        }
        System.out.println();
    }
}
