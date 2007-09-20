package AndariaPatcher;
import java.io.File;
import org.jdom.Document;

/*******************************************************************************
 * Provide operating system specific methods
 * @author  Martin Polehla (polous@katka.biz)
 * @version 0.1
 ******************************************************************************/
abstract  class OperatingSystem extends Logable {
    
    /***************************************************************************
     * Creates a new instance of current operating system
     * @return OS object with current os specific methods
     **************************************************************************/
    static OperatingSystem createOperatingSystemInstance() {
        if (System.getProperty("os.name").contentEquals("Linux")) {
            return new LinuxOS();
        }
        return new WindowsOS();
    }
    /***************************************************************************
     * Creates a new instance of OperatingSystem
     **************************************************************************/
    public OperatingSystem() { super(); }
    
    /***************************************************************************
     * @return XML cofiguration file patch
     **************************************************************************/
    abstract String getConfigPath();
    /***************************************************************************
     * @return os oriented patch script command
     **************************************************************************/
    abstract String getBatchExecCommand(File f);
    /***************************************************************************
     * @return default settings
     **************************************************************************/
    abstract Document getDefaults();
    /***************************************************************************
     * unpack a rar file.
     * @param fileName name
     **************************************************************************/
    public void unrar(String fileName) {
        File file = new File(fileName);
        unrar(file);
    }
    /***************************************************************************
     * unpack a rar file.
     * @param file to extract
     **************************************************************************/
    public void unrar(File file) {
        
    }
    /***************************************************************************
     * take care about unrar tool existance and unrar settings inicialization
     *
     **************************************************************************/
     public void unrarInit() {
         //if (new File("c:\\..."))
        //File urfile = new File("c:\\windows\\unrar.dll");
        
     }
}
