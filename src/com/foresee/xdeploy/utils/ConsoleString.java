package com.foresee.xdeploy.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;


public class ConsoleString {
    public static StringBuffer Content = new StringBuffer();
    PrintStream ps =null;


    public ConsoleString() throws IOException {
        final LoopedStreams ls = new LoopedStreams();
        // 重定向System.out和System.err
         ps = new PrintStream(ls.getOutputStream());
        System.setOut(ps);
        System.setErr(ps);
        startConsoleReaderThread(ls.getInputStream());
    }
    private void startConsoleReaderThread(InputStream inStream) {
        final BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
        new Thread(new Runnable() {
            public void run() {
                //StringBuffer sb = new StringBuffer();
                try {
                    String s;
                    //Document doc = getDocument();
                    while ((s = br.readLine()) != null) {
                        //boolean caretAtEnd = false;
                        //caretAtEnd = getCaretPosition() == doc.getLength() ? true : false;
                        //sb.setLength(0);
                        Content.append(s).append('\n');
//                        if (caretAtEnd)
//                            setCaretPosition(doc.getLength());
                    }
                } catch (IOException e) {
                    Content.append( "从BufferedReader读取错误：" + e);
                    System.exit(1);
                }
            }
        }).start();
    } // startConsoleReaderThread()

    
    static ConsoleString consoleString = null;

    public static ConsoleString createConsole(){
        try {
            consoleString = new ConsoleString();
        } catch (IOException e) {
            System.err.println("不能创建LoopedStreams：" + e);
            System.exit(1);
        }
        return consoleString;
        
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
