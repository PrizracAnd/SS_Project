package crypto;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GammaForGOST {
    //-----Randoms begin------------
    private byte randomCode;
    private Random rd;                                  //code 0
    private SecureRandom srj;                           //code 1
    private sun.security.provider.SecureRandom srssp;   //code 2
    //-----Randoms end--------------


    //-----Text begin---------------
    protected List<Long> textList;
    //-----Text end-----------------

    //-----Synchrony post begin-----
    protected long synchronizedPost;
    protected boolean noSynchronizedPost = true;
    public final static long C232 = 4294967296L;
    public final static long C1 = 1010101L;
    public final static long C2 = 1010104L;
    //-----Synchrony post end-------

    //-----Keys begin---------------
    protected long[] keys;
    //-----Keys end-----------------

    //-----Sbox begin---------------
    protected int[][] sBox;
    //-----Sbox end-----------------


    //////////////////////////////////////////////////////////
    ///  Constructors
    /////////////////////////////////////////////////////////
    //-----Constructors begin-------
    public GammaForGOST(List<Long> textList, long[] keys, int[][] sBox){
        this.textList = textList;
        this.keys = keys;
        this.sBox = sBox;

        this.randomCode = 2;
        this.srssp = new sun.security.provider.SecureRandom();
    }

    public GammaForGOST(List<Long> textList, long[] keys, int[][] sBox, Random rd){
        this.textList = textList;
        this.keys = keys;
        this.sBox = sBox;

        this.randomCode = 0;
        this.rd = rd;
    }

    public GammaForGOST(List<Long> textList, long[] keys, int[][] sBox, SecureRandom srj){
        this.textList = textList;
        this.keys = keys;
        this.sBox = sBox;

        this.randomCode = 1;
        this.srj = srj;
    }

    public GammaForGOST(List<Long> textList, long[] keys, int[][] sBox, sun.security.provider.SecureRandom srssp){
        this.textList = textList;
        this.keys = keys;
        this.sBox = sBox;

        this.randomCode = 2;
        this.srssp = srssp;
    }

    //-----Constructors with Synchronize post
    public GammaForGOST(List<Long> textList, long[] keys, int[][] sBox, long synchronizedPost){
        this(textList, keys, sBox);

        setSP(synchronizedPost);

    }

    public GammaForGOST(List<Long> textList, long[] keys, int[][] sBox, long synchronizedPost, Random rd){
        this(textList, keys, sBox, rd);

        setSP(synchronizedPost);
    }

    public GammaForGOST(List<Long> textList, long[] keys, int[][] sBox, long synchronizedPost, SecureRandom srj){
        this(textList, keys, sBox, srj);

        setSP(synchronizedPost);
    }

    public GammaForGOST(List<Long> textList, long[] keys, int[][] sBox, long synchronizedPost, sun.security.provider.SecureRandom srssp){
        this(textList, keys, sBox, srssp);

        setSP(synchronizedPost);
    }
    //-----Constructors end---------


    //////////////////////////////////////////////////////////
    ///  Method getSP
    /////////////////////////////////////////////////////////
    protected long getSP(){
        long sp = 0;

        switch (this.randomCode){
            case 0:
                if(this.rd != null){
                    sp = rd.nextLong();
                }

                break;
            case 1:
                if(this.srj != null){
                    sp =srj.nextLong();
                }
                break;
            case 2:
                if(this.srssp != null){

                    byte[] bytes = new byte[8];
                    srssp.engineNextBytes(bytes);

                    long lg = 0;
                    for (int j = 0; j < 8; j++){
                        lg |= (long)(bytes[j] << (j * 8));
                    }
                    sp =lg;
                }
                break;
            default:
                this.randomCode = 0;
                this.rd = new Random();
                sp = rd.nextLong();
                break;
        }

        return sp;
    }
//    private List<Long> getSP(){
//        List<Long> longList = new ArrayList<Long>();
//
//        switch (this.randomCode){
//            case 0:
//                if(this.rd != null){
//                    for (int i = 0; i < this.textList.size(); i++){
//                        longList.add(rd.nextLong());
//                    }
//                }
//                break;
//            case 1:
//                if(this.srj != null){
//                    for (int i = 0; i < this.textList.size(); i++){
//                        longList.add(srj.nextLong());
//                    }
//                }
//                break;
//            case 2:
//                if(this.srssp != null){
//                    for (int i = 0; i < this.textList.size(); i++){
//                        byte[] bytes = new byte[8];
//                        srssp.engineNextBytes(bytes);
//
//                        long lg = 0;
//                        for (int j = 0; j < 8; j++){
//                            lg |= (long)(bytes[j] << (j * 8));
//                        }
//                        longList.add(lg);
//                    }
//                }
//                break;
//            default:
//                break;
//        }
//
//        return longList;
//    }


    //////////////////////////////////////////////////////////
    ///  Method encryptForGama
    /////////////////////////////////////////////////////////
    public List<Long> cryptForGama(){
        List<Long> encryptText = new ArrayList<Long>();


        if(this.noSynchronizedPost){
            setSP(getSP());
        }


        long sp = this.synchronizedPost;
        for(long item: this.textList){
            long nL = ((sp & (C232 - 1)) + C1) & (C232 - 1);                        //выполняем приращение sp
            long nH = ((sp >>> 32) + C2) & (C232 - 1);                              //--||--
            sp = (nH << 32) | nL;                                                   //--||--

            long gama = new GOST(sp, this.keys, this.sBox).getEncryptDataBlock();   //получаем гаму через ГОСТ
            encryptText.add(gama ^ item);                                           //складываем гаму с текстом по модулю 2
        }

        return encryptText;
    }



//    public List<Long> getSpList() {
//        return spList;
//    }

    //////////////////////////////////////////////////////////
    ///  Methods for synchronized post
    /////////////////////////////////////////////////////////
    //-----Begin--------------------
    public long getSynchronizedPost() {
        return synchronizedPost;
    }

    public void setSynchronizedPost(long synchronizedPost) {
        setSP(synchronizedPost);
    }


    protected void setSP(long synchronizedPost) {
        this.synchronizedPost = synchronizedPost;
        this.noSynchronizedPost = false;
    }
    //-----End----------------------
}
