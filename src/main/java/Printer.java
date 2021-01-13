public class Printer {

    void print(String text) {
        System.out.print(text);
    }

    void println(String text) {
        System.out.println(text);
    }

    void drawLine() {
        for (int i = 0; i < 80; i++){
            System.out.print('-');
        }
        System.out.println();
    }
}
