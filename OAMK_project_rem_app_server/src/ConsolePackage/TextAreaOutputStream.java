package ConsolePackage;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.swing.JTextArea;

public class TextAreaOutputStream
extends OutputStream
{

// *************************************************************************************************
// INSTANCE MEMBERS
// *************************************************************************************************

private byte[]                          oneByte;                                                    // array for write(int val);
private Appender                        appender;                                                   // most recent action

public TextAreaOutputStream(JTextArea txtara) {
    this(txtara,1000);
    }

public TextAreaOutputStream(JTextArea txtara, int maxlin) {
    if(maxlin<1) { throw new IllegalArgumentException("TextAreaOutputStream maximum lines must be positive (value="+maxlin+")"); }
    oneByte=new byte[1];
    appender=new Appender(txtara,maxlin);
    }

/** Clear the current console text area. */
public synchronized void clear() {
    if(appender!=null) { appender.clear(); }
    }

public synchronized void close() {
    appender=null;
    }

public synchronized void flush() {
    }

public synchronized void write(int val) {
    oneByte[0]=(byte)val;
    write(oneByte,0,1);
    }

public synchronized void write(byte[] ba) {
    write(ba,0,ba.length);
    }

public synchronized void write(byte[] ba,int str,int len) {
    if(appender!=null) { appender.append(bytesToString(ba,str,len)); }
    }

@edu.umd.cs.findbugs.annotations.SuppressWarnings("DM_DEFAULT_ENCODING")
static private String bytesToString(byte[] ba, int str, int len) {
    try { return new String(ba,str,len,"UTF-8"); } catch(UnsupportedEncodingException thr) { return new String(ba,str,len); } // all JVMs are required to support UTF-8
    }
}