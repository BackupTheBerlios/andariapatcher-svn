package AndariaPatcher;
import javax.swing.JTextArea;

/*******************************************************************************
 *
 * @author p0l0us
 ******************************************************************************/
public class Logable {
    protected Log log;
    private JTextArea logArea;
    
    /**
     * Creates a new instance of Logable
     */
    public Logable() {
        log = new Log(this);
    }
}
