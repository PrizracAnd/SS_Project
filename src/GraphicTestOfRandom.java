import java.util.ArrayList;
import java.util.List;

public class GraphicTestOfRandom {
    public final static String MAN =
            "Тест «Распределение на плоскости» позволяет оценить случайность и независимость чисел случайной последовательности.\n" +
                    "Принцип данного теста заключается в том, что на координатной плоскости проставляются точки,\n" +
                    "координатами для каждой точки служат числа случайной последовательности по принципу: точка An(n, n+1),\n" +
                    "где n – элемент случайной последовательности чисел. Если в результате проведения теста расположение точек на плоскости хаотично,\n" +
                    "то числа последовательности случайны и независимы друг от друга;\n" +
                    "если наблюдаются так называемые «узоры», числа имеют зависимости и последовательность предсказуема.\n\n" +

                    "Тест «Гистограмма» позволяет оценить качество случайной последовательности с точки зрения частоты вхождения в нее тех или иных элементов.\n" +
                    "Принцип проведения теста: считается число вхождений каждого числа в последовательность, после чего по полученным числам строится гистограмма.\n" +
                    "Хорошим результатом данного теста является, что полученные столбцы (в реализации, приведенной ниже, строки) имеют примерно одинаковую длину,\n" +
                    "что означает, что числа равномерно входят в последовательность. Если наблюдаются столбцы (строки), которые сильно выделяются по длине,\n" +
                    "то это означает, что в случайной последовательности некоторые числа встречаются гораздо чаще других,\n" +
                    "что делает последовательность более предсказуемой.\n\n\n";

    private static final String OTCHERK = "--------------------------------------------------------\n";
    private List<String> stringList = new ArrayList<String>();
    private String Name;
    private int[][] a = new int[10][10];
    private int[] b = new int[10];

    //////////////////////////////////////////////////////////
    ///  Constructor
    /////////////////////////////////////////////////////////
    public GraphicTestOfRandom(){
        //prepare arrays
//        prepareArrays();

    }

    //////////////////////////////////////////////////////////
    ///  Method prepareArrays
    /////////////////////////////////////////////////////////
    private void prepareArrays(){
        for (int i = 0; i < 10; i++){
            this.b[i] = 0;
            for (int j = 0; j < 10; j++){
                this.a[i][j] = 0;
            }
        }
    }

    //////////////////////////////////////////////////////////
    ///  Method test
    /////////////////////////////////////////////////////////
    public void test(String nameOfTest, int[] rnd){
        int i = 0;
        int x = 0, y;

        this.Name = nameOfTest;
        prepareArrays();

        while (i < rnd.length){
            if (rnd[i] > -1 && rnd[i] < 10){
                x = rnd[i];
                b[x]++;
                break;
            }
            i++;
        }

        while (i < rnd.length){
            if (rnd[i] > -1 && rnd[i] < 10){
                y = rnd[i];
                a[y][x] = 1;
                b[y]++;
                x = y;
            }
            i++;
        }

        printResult();
    }


    //////////////////////////////////////////////////////////
    ///  Method printResult
    /////////////////////////////////////////////////////////
    private void printResult(){
        print(OTCHERK);
        print("\n" + this.Name + ":\n");
        print("     DIS:   \t||      GIS:   \n");

        for (int y = 9; y > -1; --y){
            print(y + "|");
            for (int x = 0; x < 10; ++x)
                if (a[y][x] == 1)
                    print("*");
                else
                    print(" ");

            print("\t||" + y + "|");
            int t = 0;
            if (b[y] > 0)
                t = ((b[y] - b[y]%10)/10) + 1;
            for (int k = 0; k < t; ++k)
                print("*");
            for (int k = t; k < 10; ++k)
                print(" ");
            print(" " + b[y] + "\n");
        }

        print("  ----------\t||   ----------\n");
        print("  0123456789\t||   0123456789\n");
        print(OTCHERK);
    }


    //////////////////////////////////////////////////////////
    ///  Methods of Man
    /////////////////////////////////////////////////////////
    public void printMan(){
        print(MAN);

    }

    //////////////////////////////////////////////////////////
    ///  Method print
    /////////////////////////////////////////////////////////
    private void print(String s){
        System.out.printf(s);
        addInProtocol(s);
    }

    //////////////////////////////////////////////////////////
    ///  Methods of Protocol
    /////////////////////////////////////////////////////////
    private void addInProtocol(String s){
        stringList.add(s);
    }

    public void clearProtocol(){
        this.stringList.removeAll(this.stringList);
    }

    public List<String> getProtocol(){
        return this.stringList;
    }

    public void printProtocol(){
        for(String item: stringList){
            System.out.printf(item);
        }
    }

    public void printProtocolToFile(String pathName){
        RW_Prep rwp = new RW_Prep();
        rwp.writeFile(pathName, this.stringList);
    }
}


