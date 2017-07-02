package com.foresee.xdeploy.tmp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/*
 * private static class MyPrintStream extends PrintStream {
private PrintStream ps;
public MyPrintStream(PrintStream ps) {
super(ps);    
this.ps = ps;
}
public void write(byte[] buf, int off, int len) {
super.write(buf, off, len);
}
}

System.setOut(new MyPrintStream(System.out));
 */


public class SwitchOutPrint extends OutputStream {
    private PrintStream txtLog;

    public void write(int arg0) throws IOException {
        // 写入指定的字节，忽略
    }

    public void write(byte data[]) throws IOException {
        // 追加一行字符串
        txtLog.append(new String(data));
    }

    public void write(byte data[], int off, int len) throws IOException {
        // 追加一行字符串中指定的部分，这个最重要
        txtLog.append(new String(data, off, len));
        // 移动TextArea的光标到最后，实现自动滚动
        // txtLog.setCaretPosition(txtLog.getText().length());
    }

    public static void outTest() {
        ByteArrayOutputStream baoStream = new ByteArrayOutputStream(1024);
        PrintStream cacheStream = new PrintStream(baoStream);// 临时输出
        PrintStream oldStream = System.out;// 缓存系统输出
        System.setOut(cacheStream);
        System.out.print("控制台打印信息测试...");// 不会打印到控制台
        String message = baoStream.toString();
        System.setOut(oldStream);// 还原到系统输出
        System.out.println("获取到的数据为【" + message + "】");
    }

}