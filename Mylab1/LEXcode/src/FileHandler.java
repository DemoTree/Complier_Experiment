import java.io.*;

public class FileHandler {
    private BufferedReader br;
    private static String filename;
    private FileWriter fw;

    public FileHandler(String input_filename) {
        this.filename = input_filename;
        try {
            FileInputStream fs = new FileInputStream(filename);
            InputStreamReader isr = new InputStreamReader(fs);
            br = new BufferedReader(isr);
        } catch (FileNotFoundException e) {
            System.out.println(filename + " is not found");
        }
        try {
            fw = new FileWriter("output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringBuffer readFile() {
        StringBuffer buffer = new StringBuffer();
        String temp = "";
        try {
            while ((temp = br.readLine()) != null) {
                buffer.append(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    void writeFile(String content) {
        try {
            fw.write(content + '\n');
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
