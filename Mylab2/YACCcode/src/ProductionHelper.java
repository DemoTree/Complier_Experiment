import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class ProductionHelper {
    public BufferedReader br;
    public static String filename;
    public HashSet<Character> terminal = new HashSet<Character>();//终结符集合
    public HashSet<Character> nonterminal = new HashSet<Character>();//非终结符集合
    public ArrayList<String> production = new ArrayList<String>();//产生式集合
    public static final char[] operator = {'+', '-', '*', '/', '%', '(', ')'};//自定义非终结符列表
    public HashMap<Character, HashSet<Character>> first = new HashMap<Character, HashSet<Character>>();//first集
    public HashMap<Character, HashSet<Character>> follow = new HashMap<Character, HashSet<Character>>();//follow集
    public String[][] table;//预测分析表
    public Stack<Character> stack = new Stack<Character>();//分析栈


    public void Init() {
        //解析文件里的产生式
        analyzeProduction();

        //为终结符和非终结符计算 First 集
        for (Character c : terminal) {
            getFirst(c);
        }
        for (Character c : nonterminal) {
            getFirst(c);
        }

        //两遍确保尾也全部运算到
        for (Character c : nonterminal) {
            getFollow(c);
        }
        for (Character c : nonterminal) {
            getFollow(c);
        }

        getTable();
    }

    public ProductionHelper(String filename) {
        this.filename = filename;
        try {
            FileInputStream fs = new FileInputStream(filename);
            InputStreamReader isr = new InputStreamReader(fs);
            br = new BufferedReader(isr);
        } catch (FileNotFoundException e) {
            //没有找到文件
            System.out.println("没有找到" + filename);
        }
    }

    /******** 解析产生式 *********/
    //读取 input.txt 文件分析产生式
    public void analyzeProduction() {
        StringBuffer buffer = new StringBuffer();
        String line = "";
        try {
            //从文件中按行读取产生式
            while ((line = br.readLine()) != null) {
                //读取产生式至production并区分终结符和非终结符
                String[] str = line.split("->");
                String ss = "";
                ss = Character.toString(str[0].charAt(0));//第一个字符为产生式左侧
                nonterminal.add(str[0].charAt(0));//左侧都为非终结符
                //从->右侧开始读
                for (int i = 0; i < str[1].length(); i++) {
                    //判断终结符，包括小写字母，operator
                    if (isTerminal(str[1].charAt(i))) {
                        terminal.add(str[1].charAt(i));
                    } else if (Character.isUpperCase(str[1].charAt(i))) {
                        //大写字母为非终结符
                        nonterminal.add(str[0].charAt(0));
                    }
                    //如果是|，说明|左侧已经读完，存入production，初始化 ss
                    if (str[1].charAt(i) == '|') {
                        production.add(ss);
                        ss = Character.toString(str[0].charAt(0));
                    } else {
                        ss += str[1].charAt(i);

                    }
                }
                production.add(ss);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //判断是否是非终结符
    public static boolean isTerminal(char ch) {
        //终结符包括小写字母，operator 列表
        if (Character.isLowerCase(ch)) {
            return true;
        } else {
            for (int i = 0; i < operator.length; i++) {
                if (operator[i] == ch) {
                    return true;
                }
            }
        }
        return false;
    }
    /******** 产生式解析完成 *********/

    /******** 生成first集 *********/
    //生成 first 集
    public void getFirst(Character c) {
        HashSet<Character> set = first.containsKey(c) ? first.get(c) : new HashSet<Character>();
        //c 为终结符直接添加
        if (terminal.contains(c)) {
            set.add(c);
            first.put(c, set);
            return;
        }//非终结符处理所有产生式
        else {
            for (int i = 0; i < production.size(); i++) {
                //以所求非终结符开头产生式
                if (production.get(i).charAt(0) == c) {
                    //产生式右部
                    String s = production.get(i).substring(1, production.get(i).length());
                    // ε空串直接添加
                    if (s.equals(Character.toString('ε'))) {
                        set.add('ε');
                    } else {
                        // 遍历产生式右部
                        int j = 0;
                        while (j < s.length()) {
                            //先区分终结符和非终结符
                            getFirst(s.charAt(j));
                            //如果遇到相同字符
                            HashSet<Character> tempfirst = first.get(s.charAt(j));
                            // 将新的first集加入原first集
                            for (Character temp : tempfirst)
                                set.add(temp);
                            //处理完直接退出
                            break;
                        }
                    }
                    first.put(c, set);
                }
            }
        }
    }
    /******** first集生成完成 *********/

    /******** 生成follow集
     * A->aBb A即传入的c，B为tempc
     * 若b存在且firstb不包含空串，把firstb-ε加入followB
     * 若b存在且firstb包含空串，把followA加入followB
     * 若b不存在，followA加入followB
     ********/
    public void getFollow(Character c) {
        HashSet<Character> setA = follow.containsKey(c) ? follow.get(c) : new HashSet<Character>();
        //先为所有项添加 $
        setA.add('$');
        //查找输入的所有产生式，确定c的后跟终结符
        for (int i = 0; i < production.size(); i++) {
            //产生式右部
            String s = production.get(i).substring(1);
            for (int j = 0; j < s.length(); j++)
                if (s.charAt(j) == c && j + 1 < s.length() && terminal.contains(s.charAt(j + 1)))
                    setA.add(s.charAt(j + 1));
        }
        follow.put(c, setA);
        //处理c的每一条产生式
        for (int i = 0; i < production.size(); i++) {
            //以所求非终结符开头产生式
            if (production.get(i).charAt(0) == c) {
                //产生式右部为 s , 左部为 c
                String s = production.get(i).substring(1);
                //从右往左遍历
                int j = s.length() - 1;
                while (j >= 0) {
                    char tempc = s.charAt(j);//tempc即b
                    //只处理非终结符
                    if (nonterminal.contains(tempc)) {
                        //b存在
                        if (s.length() - j - 1 > 0) {//确保有a存在
                            String right = s.substring(j + 1, j + 2);//b
                            HashSet<Character> firstb = first.get(right.charAt(0));
                            HashSet<Character> followb = follow.containsKey(tempc) ? follow.get(tempc) : new HashSet<Character>();//是否已存在 followb
                            //firstb 不包含空串，把firstb-ε加入followB
                            for (Character ch : firstb) {
                                if (ch != 'ε') //取非空
                                    followb.add(ch);
                            }
                            follow.put(tempc, followb);
                            //若b存在且firstb包含空串，把followA加入followB
                            if (firstb.contains('ε')) {
                                if (tempc != c) {
                                    HashSet<Character> setB = follow.containsKey(tempc) ? follow.get(tempc) : new HashSet<Character>();//是否已存在 followb
                                    for (Character ch : setA) {
                                        setB.add(ch);
                                    }
                                    follow.put(tempc, setB);
                                }
                            }
                        }
                        //若b不存在 followA加入followB
                        else {
                            // A和B相同不添加
                            if (tempc != c) {
                                HashSet<Character> setB = follow.containsKey(tempc) ? follow.get(tempc) : new HashSet<Character>();//是否已存在 followb
                                for (Character ch : setA) {
                                    setB.add(ch);
                                }

                                follow.put(c, setB);
                            }
                        }
                        j--;
                    } else {
                        j--;
                    }
                }
            }
        }
    }
    /******** follow 集生成完成*********/

    /******** 生成预测分析表 *********/
    public void getTable() {
        Object[] tArray = terminal.toArray();
        Object[] ntArray = nonterminal.toArray();
        // 预测分析表初始化
        table = new String[ntArray.length + 1][tArray.length + 1];
        table[0][0] = "Vn/Vt";
        //初始化首行首列
        for (int i = 0; i < tArray.length; i++)
            //ε当$输出
            table[0][i + 1] = (tArray[i].toString().charAt(0) == 'ε') ? "$" : tArray[i].toString();
        for (int i = 0; i < ntArray.length; i++)
            table[i + 1][0] = ntArray[i] + "";
        //初始化全部置空
        for (int i = 0; i < ntArray.length; i++)
            for (int j = 0; j < tArray.length; j++)
                table[i + 1][j + 1] = "/";

        //处理每一条产生式
        for (int i = 0; i < production.size(); i++) {
            //以所求非终结符开头产生式
            char c = production.get(i).charAt(0);
            if (nonterminal.contains(c)) {
                //产生式右边
                String s = production.get(i).substring(1);
                //直接推出ε，需要 Follow 集
                if (s.equals("ε")) {
                    HashSet<Character> set = follow.get(c);
                    for (Character ch : set) {
                        addTable(c, ch, s);
                    }
                }//直接推出终结符，加在终结符项
                else if (terminal.contains(s.charAt(0))) {
                    addTable(c, s.charAt(0), s);
                } else {
                    //需要 first 集
                    HashSet<Character> set = first.get(c);
                    for (Character ch : set) {
                        addTable(c, ch, s);
                    }
                }
            }
        }

    }

    //填写表格 c 为非终结符，ch 为终结符，s 为表达式右端
    public void addTable(char c, char ch, String s) {
        if (ch == 'ε') {
            ch = '$';
        }
        for (int i = 0; i < nonterminal.size() + 1; i++) {
            if (table[i][0].charAt(0) == c)
                for (int j = 0; j < terminal.size() + 1; j++) {
                    if (table[0][j].charAt(0) == ch) {
                        table[i][j] = c + "->" + s;
                        return;
                    }
                }
        }
    }

    /******** 构造表生成完成 *********/

    /******** 打印准备过程 *********/
    public void printPre() {

        //产生式集
        System.out.println("======产生式集合======");
        for (Object object : production) {
            String s = object.toString();
            System.out.println(s.charAt(0) + "->" + s.substring(1, s.length()));
        }

        //first 集
        System.out.println("\n=======First集======");
        for (Character key : first.keySet()) {
            System.out.println(key + ":" + first.get(key));
        }

        //follow 集
        System.out.println("\n=======Follow集======");
        for (Character key : follow.keySet()) {
            System.out.println(key + ":" + follow.get(key));
        }

        System.out.println("\n======预测分析表:======");
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                System.out.printf("%6s", table[i][j] + " ");
            }
            System.out.println();
        }
    }


    /******** 分析输入字符串 *********/
    public void analyze(String word) {
        System.out.println("\n======执行分析======");
        System.out.println("          Stack     Input     Action");
        stack = new Stack<Character>();
        String action = "";
        stack.push('$');//开始符号压入栈
        stack.push(production.get(0).charAt(0));//开始符压入栈
        char X = stack.peek();//栈顶符号 S
        int index = 0;
        word = word + "$";//加上终止符
        printStack(index, action, word);
        while (X != '$') {
            char ch = word.charAt(index);
            //表示匹配栈顶符号，符号栈弹出一个，index++指向下一个
            if (X == ch) {
                action = "match " + stack.peek();
                stack.pop();
                index++;
            } else if (terminal.contains(X)) {
                System.out.println("表内未查到");
                return;
            } else if (search(X, ch).equals("/")) {
                System.out.println("表内未查到");
                return;
            } else if (search(X, ch).equals("ε")) {
                stack.pop();
                action = X + "->ε";
            } else {
                //否则查表正确，弹出栈顶符号，右部符号进栈
                String result = search(X, ch);
                if (!result.equals("")) {
                    action = X + "->" + result;
                    stack.pop();
                    for (int i = result.length() - 1; i >= 0; i--)
                        stack.push(result.charAt(i));
                } else {
                    System.out.println("匹配失败，在第" + index + "个'" + word.charAt(index) + "'字符处失败");
                    return;
                }
            }
            X = stack.peek();
            printStack(index, action, word);
        }
        System.out.println("匹配成功");
    }

    public String search(char X, char ch) {
        for (int i = 0; i < nonterminal.size() + 1; i++) {
            if (table[i][0].charAt(0) == X)
                for (int j = 0; j < terminal.size() + 1; j++) {
                    if (table[0][j].charAt(0) == ch)
                        return table[i][j].substring(3);//取产生式右端
                }
        }
        return "";
    }

    //打印栈情况
    public void printStack(int i, String action, String word) {
        Stack<Character> s = stack;
        System.out.printf("%20s", s);
        System.out.printf("%10s", word.substring(i));
        System.out.printf("%10s", action);
        System.out.println();
    }
    /******** 分析完成 *********/
}
