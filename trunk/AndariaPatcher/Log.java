package AndariaPatcher;

import javax.swing.JTextArea;
/*******************************************************************************
 *
 * @author p0l0us
 ******************************************************************************/
public class Log {
    protected static JTextArea logArea;
    private String logOwnerClass; // name of object owning instance of log
    /** Creates a new instance of Log */
    public Log(Object ob) {
        logOwnerClass = ob.getClass().toString();
    }
    public Log(String obName) {
        logOwnerClass = obName;
    }
    public void addDebug(String debugLine) {
        if (Settings.debugMode()) {
            System.out.println(debugLine);
            add("DEBUG: " + debugLine);
        }
    }
    public void addErr(String errLine) {
        if (Settings.debugMode())
            System.err.println(errLine);
        
        add("CHYBA: " + errLine + "!");
    }
    public void addEx(Exception e) {
        if (Settings.debugMode()) {
            e.printStackTrace();
            add(e.getStackTrace().toString());
        }
        add("KRITICKA CHYBA: " + e.getMessage() + "!!!!!!!");
        
    }
    public void addLine(String line) {
        if (Settings.debugMode())
            System.out.println(line);
        
        add(line);
        
    }
    private void add(String line) {
        if (Settings.debugMode()) logArea.insert("[" + logOwnerClass + "]  " + line + "\n", 0);
        else logArea.insert(line + "\n", 0);
    }
}
