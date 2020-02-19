public class Lexical {
    private StringBuffer buffer;
    private Classification cf;
    private FileHandler fh;
    private StringBuffer word;
    private STATE state;
    private int isError = 0;
    private StringBuffer errorWord;

    public Lexical(String filename) {
        fh = new FileHandler(filename);
        buffer = fh.readFile();
        word = new StringBuffer();
        cf = new Classification();
        cf.readLex();
    }

    enum STATE {
        NORMAL,//正常情况 done
        MINUS,//负号 -数字 -操作符
        DIGIT, //整数 done
        FLOAT,//浮点数 done
        LETTER, //字母，可能是保留关键字或ID done
        SEPARATOR,//操作符 done
        OPERATOR,//分隔符 done
        ANNOTATION_PRE,//斜线 / -行注释，段注释，操作符
        LINE_ANNOTATION,//行注释 done
        SEGMENT_ANNOTATION_PRE,//段注释 /* done
        SEGMENT_ANNOTATION,//段注释 done
    }

    public void analyzeState() {
        state = STATE.NORMAL;
        int i = 0;
        //state 为上一个字符状态，ch 为当前字符内容
        while (i <= buffer.length()) {
            char ch = ' ';
            if (i != buffer.length()) {
                ch = buffer.charAt(i);
            }
            //System.out.println(cf.isLetter('p'));
            switch (state) {
                case NORMAL://正常情况
                    if (cf.isDigit(ch)) {
                        word.append(ch);
                        state = STATE.DIGIT;
                    } else if (cf.isLetter(ch)) {
                        word.append(ch);
                        state = STATE.LETTER;
                    } else if (cf.isSeparator(ch)) {
                        word.append(ch);
                        state = STATE.SEPARATOR;
                    } else if (cf.isSingleOperator(ch)) {
                        word.append(ch);
                        state = STATE.OPERATOR;
                    } else if (ch == '/') {
                        word.append(ch);
                        state = STATE.ANNOTATION_PRE;
                    } else if (ch == '-') {
                        word.append(ch);
                        state = STATE.MINUS;
                    } else if (ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r') {
                    } else {
                        System.out.println("Can't recognize " + ch);
                    }
                    break;
                case MINUS:
                    if (cf.isDigit(ch)) {
                        word.append(ch);
                        state = STATE.DIGIT;
                    } else if (cf.isSingleOperator(ch)) {
                        word.append(ch);
                        state = STATE.OPERATOR;
                    }
                    break;
                case DIGIT://数字 -整数 -浮点数
                    if (cf.isDigit(ch)) {
                        word.append(ch);
                    } else if (ch == '.') {//判断是否是浮点数
                        word.append(ch);
                        state = STATE.FLOAT;
                    } else if (cf.isLetter(ch)) {
                        isError = 1;
                        errorWord = word;
                        errorWord.append(ch);
                        word = new StringBuffer();
                        state = STATE.NORMAL;
                        i--;
                    } else {
                        state = STATE.NORMAL;
                        fh.writeFile("INT: " + word);
                        word = new StringBuffer();
                        i--;
                    }
                    break;
                case FLOAT://浮点数
                    if (cf.isDigit(ch)) {
                        word.append(ch);
                    } else if (cf.isLetter(ch)) {
                        isError = 1;
                        errorWord = word;
                        errorWord.append(ch);
                        word = new StringBuffer();
                        state = STATE.NORMAL;
                        i--;
                    } else {
                        state = STATE.NORMAL;
                        fh.writeFile("FLOAT: " + word);
                        word = new StringBuffer();
                        i--;
                    }
                    break;
                case LETTER://字母 -保留字 -ID
                    if (isError == 1) {
                        if (cf.isLetter(ch) || cf.isDigit(ch)) {
                            errorWord.append(ch);
                        } else {
                            System.out.println("Error format: " + errorWord);
                            state = STATE.NORMAL;
                            errorWord = new StringBuffer();
                            word = new StringBuffer();
                            isError = 0;
                            i--;
                        }
                        break;
                    } else {
                        if (cf.isLetter(ch) || cf.isDigit(ch)) {
                            word.append(ch);
                        } else {
                            if (cf.isKeyword(word.toString())) {
                                fh.writeFile("KEYWORD: " + word);
                            } else {
                                fh.writeFile("ID: " + word);
                            }
                            state = STATE.NORMAL;
                            word = new StringBuffer();
                            i--;
                        }
                        break;
                    }
                case SEPARATOR: //分隔符
                    state = STATE.NORMAL;
                    fh.writeFile("SEPARATOR: " + word);
                    word = new StringBuffer();
                    i--;
                    break;
                case OPERATOR://标识符
                    if (cf.isSingleOperator(ch)) {
                        word.append(ch);
                    } else if (cf.isOperator(word.toString())) {
                        state = STATE.NORMAL;
                        fh.writeFile("OPERATOR: " + word);
                        word = new StringBuffer();
                        i--;
                    } else {
                        state = STATE.NORMAL;
                        word = new StringBuffer();
                        i--;
                    }
                    break;
                case ANNOTATION_PRE://斜杠 -注释 -操作符
                    if (ch == '/') {
                        fh.writeFile("ANNOTATION: //");
                        state = STATE.LINE_ANNOTATION;
                        word = new StringBuffer();
                    } else if (ch == '*') {
                        fh.writeFile("ANNOTATION: /*");
                        state = STATE.SEGMENT_ANNOTATION_PRE;
                        word = new StringBuffer();
                    } else {
                        fh.writeFile("OPERATOR: /");
                        state = STATE.NORMAL;
                        word = new StringBuffer();
                        i--;
                    }
                    break;
                case LINE_ANNOTATION: //单行注释
                    if (ch == '\n' || ch == '\t' || ch == '\r') {
                        state = STATE.NORMAL;
                        fh.writeFile("ANNOTATION TEXT: " + word);
                        word = new StringBuffer();
                        i--;
                    } else {
                        word.append(ch);
                    }
                    break;
                case SEGMENT_ANNOTATION_PRE:
                    if (ch == '*') {
                        state = STATE.SEGMENT_ANNOTATION;
                    }
                    word.append(ch);
                    break;
                case SEGMENT_ANNOTATION://多行注释
                    if (ch == '/') {
                        state = STATE.NORMAL;
                        fh.writeFile("ANNOTATION TEXT: " + word.substring(0, word.length() - 1));
                        fh.writeFile("ANNOTATION: */");
                        word = new StringBuffer();
                    } else if (ch == '*') {
                        word.append(ch);
                    } else {
                        state = STATE.SEGMENT_ANNOTATION;
                        word.append(ch);
                    }
                    break;
            }
            i++;
        }
    }
}
