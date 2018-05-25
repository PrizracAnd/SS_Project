package crypto;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamaForGOST {
    //-----Randoms begin------------
    private byte randomCode;
    private Random rd;                                  //code 0
    private SecureRandom srj;                           //code 1
    private sun.security.provider.SecureRandom srssp;   //code 2
    //-----Randoms end--------------


    //-----Text begin---------------
    List<Long> textList;
    //-----Text end-----------------

    //-----Synchrony post begin-----
    List<Long> spList;
    //-----Synchrony post end-------


    //////////////////////////////////////////////////////////
    ///  Constructors
    /////////////////////////////////////////////////////////
    //-----Constructors begin-------
    public GamaForGOST(List<Long> textList){
        this.textList = textList;

        this.randomCode = 2;
        this.srssp = new sun.security.provider.SecureRandom();
    }

    public GamaForGOST(List<Long> textList, Random rd){
        this.textList = textList;

        this.randomCode = 0;
        this.rd = rd;
    }

    public GamaForGOST(List<Long> textList, SecureRandom srj){
        this.textList = textList;

        this.randomCode = 1;
        this.srj = srj;
    }

    public GamaForGOST(List<Long> textList, sun.security.provider.SecureRandom srssp){
        this.textList = textList;

        this.randomCode = 2;
        this.srssp = srssp;
    }
    //-----Constructors end---------


    //////////////////////////////////////////////////////////
    ///  Method getSP
    /////////////////////////////////////////////////////////
    private List<Long> getSP(){
        List<Long> longList = new ArrayList<Long>();

        switch (this.randomCode){
            case 0:
                if(this.rd != null){
                    for (int i = 0; i < this.textList.size(); i++){
                        longList.add(rd.nextLong());
                    }
                }
                break;
            case 1:
                if(this.srj != null){
                    for (int i = 0; i < this.textList.size(); i++){
                        longList.add(srj.nextLong());
                    }
                }
                break;
            case 2:
                if(this.srssp != null){
                    for (int i = 0; i < this.textList.size(); i++){
                        byte[] bytes = new byte[8];
                        srssp.engineNextBytes(bytes);

                        long lg = 0;
                        for (int j = 0; j < 8; j++){
                            lg |= (long)(bytes[j] << (j * 8));
                        }
                        longList.add(lg);
                    }
                }
                break;
            default:
                break;
        }

        return longList;
    }
}
