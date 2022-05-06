package com.ascs;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;

public class Printer {

    public static Ansi.Color INFOCOLOR = Ansi.Color.YELLOW;
    public static Ansi.Color SUCCESSCOLOR = Ansi.Color.GREEN;
    public static Ansi.Color ERRCOLOR = Ansi.Color.RED;

    public Printer() {
        AnsiConsole.systemInstall();
    }

    public void close() {
        AnsiConsole.systemUninstall();
    }

    public void print(String text) {
        System.out.print(text);
    }

    public void print(String text, Color color) {
        System.out.print(ansi().fgBright(color).a(text).reset());
    }

    public void println(String text) {
        System.out.println(text);
    }

    public void println() {
        System.out.println();
    }

    public void println(String text, Color color) {
        System.out.println(ansi().fgBright(color).a(text).reset());
    }

    public void printArray(String[] array) {
        int idx = 0;
        for (var item : array) {
            System.out.println(ansi().fgBrightGreen().a(++idx).fgDefault().a(" - ").fgBrightYellow().a(item).reset());
        }
    }

    public void drawLine() {
        for (int i = 0; i < 80; i++){
            System.out.print('-');
        }
        System.out.println();
    }
}
