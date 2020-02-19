public class Classification {

    public static final String[] keyword = {
            "public", "private", "protected",
            "static", "abstract", "final", "class", "extends", "implements",
            "interface", "native", "static", "strictfp", "synchronized", "transient",
            "new", "main", "Main", "System", "out", "println", "print", "args",
            "break", "continue", "return", "do", "while", "if", "else", "for",
            "instanceof", "switch", "case", "default", "try", "catch", "throw", "throws",
            "import", "package", "true", "false", "int", "char", "boolean", "byte",
            "double", "float", "long", "short", "null", "true", "false", "String", "string",
            "super", "this", "void", "goto", "const"};

    public static final char[] digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static final char[] letter = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public static final String[] operator = {"+", "-", "*", "/", "%",
            ">", "<", ">=", "<=", "==", "!=",
            "=", "+=", "-=", "*=", "/=", "%=", ">>=", "<<=", "&=", "|=", "^=",
            "&", "|", "^", "~", "&&", "||", ">>", "<<", "!", "?",
            "++", "--", "(", ")", "[", "]", ".", "->", "\"", ":"};

    //去除-，/，已在 state 中具体考虑
    public static final char[] singleOperator = {'+', '*', '%', '>', '<',
            '=', '&', '|', '^', '~', '!', '?', '.', '\"', ':'};


    public static final char[] separator = {',', ';', '(', ')', '[', ']', '{', '}'};

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
