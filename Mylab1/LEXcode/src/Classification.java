import java.io.*;
import java.util.ArrayList;

public class Classification {
    private BufferedReader br;
    public static ArrayList<String> keyword = new ArrayList<String>();
    public static ArrayList<Character> digit = new ArrayList<Character>();
    public static ArrayList<Character> letter = new ArrayList<Character>();
    public static ArrayList<String> operator = new ArrayList<String>();
    public static ArrayList<Character> singleOperator = new ArrayList<Character>();
    public static ArrayList<Character> separator = new ArrayList<Character>();

    Classification() {
        try {
            FileInputStream fs = new FileInputStream("REs.l");
            InputStreamReader isr = new InputStreamReader(fs);
            br = new BufferedReader(isr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void readLex() {
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                String[] temp = line.split(" ");

                if (temp[0].equals("digit")) {
                    digit.add(temp[1].charAt(0));
                } else if (temp[0].equals("letter")) {
                    letter.add(temp[1].charAt(0));
                } else if (temp[0].equals("SEPARATOR")) {
                    separator.add(temp[1].charAt(0));
                } else if (temp[0].equals("KEYWORD")) {
                    keyword.add(temp[1]);
                } else if (temp[0].equals("OPERATOR")) {
                    operator.add(temp[1]);
                } else if (temp[0].equals("singleOperator")) {
                    singleOperator.add(temp[1].charAt(0));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //判断是否是关键字
    public static boolean isKeyword(String ch) {
        for (String ky : keyword) {
            if (ky.equals(ch)) {
                return true;
            }
        }
        return false;
    }

    //判断是否是数字
    public static boolean isDigit(char ch) {
        for (Character dg : digit) {
            if (dg.equals(ch)) {
                return true;
            }
        }
        return false;
    }

    //判断是否是字母
    public static boolean isLetter(char ch) {
        for (Character lt : letter) {
            if (lt.equals(ch)) {
                return true;
            }
        }
        return false;
    }

    //判断是否是操作符
    public static boolean isOperator(String ch) {
        for (String op : operator) {
            if (op.equals(ch)) {
                return true;
            }
        }
        return false;
    }

    //判断是否是单个
    public static boolean isSingleOperator(char ch) {
        for (Character ss : singleOperator) {
            if (ss.equals(ch)) {
                return true;
            }
        }
        return false;
    }

    //判断是否是分隔符
    public static boolean isSeparator(char ch) {
        for (Character sp : separator) {
            if (sp.equals(ch)) {
                return true;
            }
        }
        return false;
    }
}
