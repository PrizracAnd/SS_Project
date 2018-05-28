package crypto;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GammaForGOST_Parallel extends GammaForGOST {

    private volatile List<Long> encryptText = new ArrayList<Long>();
    private int numberOfThread = 0;


    //////////////////////////////////////////////////////////
    ///  Constructors
    /////////////////////////////////////////////////////////
    //-----Constructors begin-------
    public GammaForGOST_Parallel(List<Long> textList, long[] keys, int[][] sBox) {
        super(textList, keys, sBox);

    }

    public GammaForGOST_Parallel(List<Long> textList, long[] keys, int[][] sBox, Random rd) {
        super(textList, keys, sBox, rd);
    }

    public GammaForGOST_Parallel(List<Long> textList, long[] keys, int[][] sBox, SecureRandom srj) {
        super(textList, keys, sBox, srj);
    }

    public GammaForGOST_Parallel(List<Long> textList, long[] keys, int[][] sBox, sun.security.provider.SecureRandom srssp) {
        super(textList, keys, sBox, srssp);
    }

    public GammaForGOST_Parallel(List<Long> textList, long[] keys, int[][] sBox, long synchronizedPost) {
        super(textList, keys, sBox, synchronizedPost);
    }

    public GammaForGOST_Parallel(List<Long> textList, long[] keys, int[][] sBox, long synchronizedPost, Random rd) {
        super(textList, keys, sBox, synchronizedPost, rd);
    }

    public GammaForGOST_Parallel(List<Long> textList, long[] keys, int[][] sBox, long synchronizedPost, SecureRandom srj) {
        super(textList, keys, sBox, synchronizedPost, srj);
    }

    public GammaForGOST_Parallel(List<Long> textList, long[] keys, int[][] sBox, long synchronizedPost, sun.security.provider.SecureRandom srssp) {
        super(textList, keys, sBox, synchronizedPost, srssp);
    }
    //-----Constructors end-------


    //////////////////////////////////////////////////////////
    ///  Method addToET
    /////////////////////////////////////////////////////////
    synchronized private void addToET(int index, long eText){
        this.encryptText.remove(index);
        this.encryptText.add(index, eText);
    }


    //////////////////////////////////////////////////////////
    ///  Method getEncryptText
    /////////////////////////////////////////////////////////
    public List<Long> getEncryptText() {
        return encryptText;
    }

    //////////////////////////////////////////////////////////
    ///  Method cryptForGama
    /////////////////////////////////////////////////////////
    @Override
    public List<Long> cryptForGama() {

        this.encryptText.addAll(super.textList);
        List<Thread> myThreads = new ArrayList<Thread>();

        if(super.noSynchronizedPost){
            super.setSP(super.getSP());
        }
        long sp = super.synchronizedPost;

        int not;
        if(this.numberOfThread < 1){
            not = 1;
        }else not = this.numberOfThread;


        int i = 0;
        while (i < super.textList.size()){
            for(int k = 0; k < not; k++){
                if(i < super.textList.size()) {

                    long nL = ((sp & (super.C232 - 1)) + super.C1) & (super.C232 - 1);                  //выполняем приращение sp
                    long nH = ((sp >>> 32) + super.C2) & (super.C232 - 1);                              //--||--
                    sp = (nH << 32) | nL;

                    myThreads.add(new Thread(new MyThread(i, super.textList.get(i), sp, super.keys, super.sBox)));
                    myThreads.get(k).start();
                    i++;
                }
            }

            for (Thread myTread: myThreads){                                                            //ждеь завершения нитей
                try {
                    myTread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for(int k = myThreads.size() - 1; k > -1; k--){                                            //очищаем список нитей
                myThreads.remove(k);                                            //очистку делаем таким образом, т.к. removeAll
            }                                                                   //м.б. нестабилен.
        }

        return this.encryptText;
    }

    //////////////////////////////////////////////////////////
    ///  Class MyThread
    /////////////////////////////////////////////////////////
    private class MyThread implements Runnable{

        private long openText;
        private int index;
        private long sp;
        private long[] keys;
        private int[][] sBox;

        private MyThread(int index, long openText, long sp, long[] keys, int[][] sBox){
            this.index = index;
            this.openText = openText;
            this.sp = sp;
            this.keys = keys;
            this.sBox = sBox;
        }

        @Override
        public void run() {
            long gama = new GOST(sp, this.keys, this.sBox).getEncryptDataBlock();   //получаем гаму через ГОСТ
            addToET(this.index,(gama ^ this.openText));
        }
    }



    //////////////////////////////////////////////////////////
    ///  Methods for synchronized post
    /////////////////////////////////////////////////////////
    //-----Begin--------------------
    public int getNumberOfThread() {
        return numberOfThread;
    }

    public void setNumberOfThread(int numberOfThread) {
        this.numberOfThread = numberOfThread;
    }
    //-----End-----------------------
}
