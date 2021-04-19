package com.ascs;

import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;

public class Printer {

    public Printer() {
        AnsiConsole.systemInstall();
    }

    public void close() {
        AnsiConsole.systemUninstall();
    }

    void print(String text) {
        System.out.print(text);
    }

    void print(String text, Color color) {
        System.out.print(ansi().fg(color).a(text).reset());
    }

    void println(String text) {
        System.out.println(text);
    }

    void println(String text, Color color) {
        System.out.println(ansi().fg(color).a(text).reset());
    }

    void printArray(String[] array) {
        int idx = 0;
        for (var item : array) {
            System.out.println(ansi().fgGreen().a(++idx).fgDefault().a(" - ").fgYellow().a(item).reset());
        }
    }

    void drawLine() {
        for (int i = 0; i < 80; i++){
            System.out.print('-');
        }
        System.out.println();
    }
}
