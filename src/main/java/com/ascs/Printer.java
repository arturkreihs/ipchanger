package com.ascs;

import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

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