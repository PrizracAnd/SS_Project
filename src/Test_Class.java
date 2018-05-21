import crypto.GOST;

import java.util.Scanner;

public class Test_Class {
    static Scanner cs = new Scanner(System.in);

    public static void main(String[] args) {
        int i = 0;
        do {
            System.out.println("Please select the test:");
            System.out.println("0\t ---exit;");
            System.out.println("1\t ---GOST.");

            i = cs.nextInt();
            switch (i){
                case 0:
                    break;
                case 1:
                    testGOST();
                    break;
                default:
                    break;
            }
        }while (i != 0);
    }

    private static void testGOST() {
        long data = 0xfedcba9876543210L;
        long[] keys = {         /*8 подключей из ГОСТ Р 34.12 - 2015 */
                0xffeeddcc, 0xbbaa9988, 0x77665544, 0x33221100, 0xf0f1f2f3, 0xf4f5f6f7, 0xf8f9fafb, 0xfcfdfeff };

        int [][] sBox = {             /* Таблица замен из ГОСТ Р 34.12 - 2015 */
                {12, 4, 6, 2, 10, 5, 11, 9, 14, 8, 13, 7, 0, 3, 15, 1},
                {6, 8, 2, 3, 9, 10, 5, 12, 1, 14, 4, 7, 11, 13, 0, 15},
                {11, 3, 5, 8, 2, 15, 10, 13, 14, 1, 7, 4, 12, 9, 6, 0},
                {12, 8, 2, 1, 13, 4, 15, 6, 7, 0, 10, 5, 3, 14, 9, 11},
                {7, 15, 5, 10, 8, 1, 6, 13, 0, 9, 3, 14, 11, 4, 2, 12},
                {5, 13, 15, 6, 9, 2, 12, 10, 11, 7, 8, 1, 4, 3, 14, 0},
                {8, 14, 2, 5, 6, 9, 1, 12, 15, 4, 11, 0, 13, 10, 3, 7},
                {1, 7, 14, 13, 0, 5, 8, 3, 4, 15, 10, 6, 9, 12, 11, 2}
        };
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
}