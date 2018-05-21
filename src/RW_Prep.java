import java.io.*;
import java.util.ArrayList;
import java.util.List;

interface RW_Listener{
    static void readSuccess(boolean rs){}

    static void writeSuccess(boolean ws){}
}

public class RW_Prep {
    public final static int CODENUMBER = 6;

    public final static String[] CODE = new String[] {
            "ASCII"          //0
            ,  "cp1251"         //1
            ,  "cp866"          //2
            ,  "KOI8"           //3
            ,  "KOI8-R"         //4
            ,  "KOI8-U"         //5
            ,  "Unicode"        //6
//            ,  "KOI8-RU"
            ,  "UTF-8"          //7
            ,  "UTF-16"         //8
            ,  "UTF-32"         //9
    };


    public List<Long> Encoding (String str){
        List<Long> longList = new ArrayList<Long>();
        try {
            byte[] bt = str.getBytes(CODE[CODENUMBER]);
            int i = 0;
            while (i < bt.length){
                long l64 = 0;
                while (i < bt.length){
                    l64 |= (long) bt[i] << (8 * (i%8));
                    i++;
                    if((i % 8) == 0) break;
                }
                longList.add(l64);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return longList;
    }

    public String Decoding(List<Long> longList){
        String str = null;

        //---преобразуем лист лонгов в лист байтов
        List<Byte> lb = new ArrayList<Byte>();
        for(long itemLong: longList){
            long h = itemLong;
            for(int i = 0; i < 8; i++){
               byte b = (byte)((h >> (i * 8)) % 256);
                lb.add(b);
            }
        }

        //--обрезаем конец
        for(int i = lb.size() - 1; i >= 0; i--){
            if (lb.get(i) == 0){
                lb.remove(i);
            }else break;
        }

        //---создаем результирующую строку
        if(lb.size() > 0){
            boolean l = false;
            byte[] bt = new byte[lb.size()];
            for(int i = 0; i < lb.size(); i++){
                bt[i] = lb.get(i);
            }
            try {
                str = new String(bt, CODE[CODENUMBER]);
            } catch (UnsupportedEncodingException e) {
                l = true;
                e.printStackTrace();
            }finally {
                if (l) return null;
            }
        }

        return str;
    }

    public List<String> readFile(String pathName){
        List<String> strings = new ArrayList<String>();
        boolean l = false;

        try {
            FileReader fr = new FileReader(new File(pathName));
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null){
                strings.add(line);
            }

            br.close();
            fr.close();
            l = true;
        }catch (IOException e){
            e.printStackTrace();
            strings.removeAll(strings);
            l = false;
        }finally {
            RW_Listener.readSuccess(l);
            return strings;
        }

    }

    public void writeFile(String pathName, List<String> strings){
        boolean l = false;

        try {
            FileWriter fw = new FileWriter(new File(pathName));
            BufferedWriter bw = new BufferedWriter(fw);
            for (String str: strings){
                bw.write(str + "\n");
            }

            bw.close();
            fw.close();
            l =true;
        }catch (IOException e){
            e.printStackTrace();
            l = false;
        }finally {
            RW_Listener.writeSuccess(l);
        }
    }

}
