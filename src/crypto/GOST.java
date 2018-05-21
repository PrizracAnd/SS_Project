package crypto;

public class GOST {
    private long dataBlock;         // входные данные для шифрования/расшифровки (64 бита)
    private long[] keys = {         /*8 подключей из ГОСТ Р 34.12 - 2015 */
            0xffeeddcc, 0xbbaa9988, 0x77665544, 0x33221100, 0xf0f1f2f3, 0xf4f5f6f7, 0xf8f9fafb, 0xfcfdfeff };

    private int [][] sBox = {             /* Таблица замен из ГОСТ Р 34.12 - 2015 */
                                    {12, 4, 6, 2, 10, 5, 11, 9, 14, 8, 13, 7, 0, 3, 15, 1},
                                    {6, 8, 2, 3, 9, 10, 5, 12, 1, 14, 4, 7, 11, 13, 0, 15},
                                    {11, 3, 5, 8, 2, 15, 10, 13, 14, 1, 7, 4, 12, 9, 6, 0},
                                    {12, 8, 2, 1, 13, 4, 15, 6, 7, 0, 10, 5, 3, 14, 9, 11},
                                    {7, 15, 5, 10, 8, 1, 6, 13, 0, 9, 3, 14, 11, 4, 2, 12},
                                    {5, 13, 15, 6, 9, 2, 12, 10, 11, 7, 8, 1, 4, 3, 14, 0},
                                    {8, 14, 2, 5, 6, 9, 1, 12, 15, 4, 11, 0, 13, 10, 3, 7},
                                    {1, 7, 14, 13, 0, 5, 8, 3, 4, 15, 10, 6, 9, 12, 11, 2}
                                                                                                    };

    private final static long C232 = 4294967296L;
    /* Открытый текст - fedcba9876543210 */
    private long nH = 0xfedcba98, nL = 0x76543210;

    // Constructor
    public GOST(long dataBlock, long[] keys, int[][] sBox){
        this.dataBlock = dataBlock;
        this.nL = dataBlock % C232;     // заполняем младшую часть
        this.nH = dataBlock >>> 32;     // заполняем старшую чать (т.к. сдвиг с заполнением нулями, должно прокатить и так)
        clearHi();

        this.keys = keys;
        this.sBox = sBox;

    }

    /* Основной шаг криптопреобразования */
    private void round(long nLeft, long nRight, long roundKey){
        long n, q;
        nLeft &= (C232-1);
        nRight &= (C232-1);
        roundKey &= (C232-1);
        n = (nRight + roundKey)% C232;       // младший блок складывается по модулю 2^32 с подключом
        n = substitution(n);                 // проходит через таблицу замен
        n = (n << 11) | (n >> 21);           // циклический сдвиг влево на 11 символов
        n = n & (C232-1);
        n = (n ^ nLeft);                     // складывается по модулю 2 со старшим блоком
        nH = nRight;                         //
        nL = n;                              //
    }

    /* 8 частей 32-битного блока проходят через таблицу замен на 4 бита */
    private long substitution(long value){
        long output = 0;

        for (int i = 0; i < 8; i++)
        {
            int temp = (int) ((value >> (4*i)) & 0x0f);
            temp = sBox[i][temp];
            output |= temp << (4*i);
        }

        output &= (C232-1);
        return output;
    }

    public void encrypt32(){
        for (int i = 0; i < 32; i++){
            int k;
            if (i < 24) k = i%8;         // первые 24 раундовых ключа: 0-7, 0-7, 0-7
            else k = 7-(i%8);            // последние 8 раундовых ключей: 7-0

            round(nH, nL, this.keys[k]);
        }

        long n = this.nH;
        this.nH = this.nL;
        this.nL = n;
        clearHi();
    }

    public void decrypt32(){

        for (int i = 0; i < 32; i++){
            int k;
            if (i < 8) k = i%8;         // первые 8 раундовых ключей: 0-7
            else k = 7-(i%8);            // следующие 24 раундовых ключа: 7-0, 7-0, 7-0

            round(nH, nL, this.keys[k]);
        }

        long n = this.nH;
        this.nH = this.nL;
        this.nL = n;
        clearHi();
    }

    private void clearHi(){
        this.nL  &= (C232-1);
        this.nH  &= (C232-1);

    }

    public long getDataBlock() {
        clearHi();
        this.dataBlock ^= this.dataBlock;       // очищаем переменнную
        this.dataBlock |= this.nH << 32;        // заполням сначала из старшей части со здвигом на 32 разряда
        this.dataBlock |= this.nL;

        return dataBlock;
    }

    public void setDataBlock(long dataBlock) {
        this.dataBlock = dataBlock;
        this.nL = dataBlock % C232;     // заполняем младшую часть
        this.nH = dataBlock >>> 32;     // заполняем старшую чать (т.к. сдвиг с заполнением нулями, должно прокатить и так)
        clearHi();
    }
}
