import java.util.Scanner;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        ProductionHelper ph = new ProductionHelper("input.txt");
        ph.Init();
        Scanner sc = new Scanner(System.in);
        System.out.print("请输入待判断字符串:");
        String word = sc.nextLine();
        ph.printPre();
        ph.analyze(word);

        //控制台输出到文件
        writeFile();
        ph.Init();
        ph.printPre();
        //减去最后一位加上的终结符$
        ph.analyze(word);

    }

    //将控制台输出保存至文件
    public static void writeFile() {
        try {
            File f = new File("output.txt");
            FileOutputStream fs = new FileOutputStream(f);
            PrintStream print = new PrintStream(fs);
            System.setOut(print);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
