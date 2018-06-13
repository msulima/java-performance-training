package pl.msulima.logger;

import java.io.*;

class PrintlnLogger {

    private static final int SIZE = 10 * 1024;
    private final PrintStream printStream;

    PrintlnLogger(boolean autoFlush) {
        FileOutputStream fdOut = new FileOutputStream(FileDescriptor.out);
        BufferedOutputStream out = new BufferedOutputStream(fdOut, SIZE);
        this.printStream = new PrintStream(out, autoFlush);
    }

    void log(String text) {
        printStream.println(text);
    }
}
