import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Please enter filename: ");
        String filename = sc.nextLine();
        System.out.println("======Start analyzing======");
        Lexical lex = new Lexical("input.txt");
        lex.analyzeState();
        System.out.println("======Sucessfully======");
    }
}
