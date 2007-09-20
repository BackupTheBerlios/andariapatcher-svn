package AndariaPatcher;

import javax.swing.JOptionPane;
import java.io.File;
import org.jdom.Element;
import org.jdom.Document;

/*******************************************************************************
 *
 * @author p0l0us
 ******************************************************************************/
public class LinuxOS extends OperatingSystem {
    
    /** Creates a new instance of LinuxOS 
     **************************************************************************/
    public LinuxOS() { super(); }
    /***************************************************************************
     * @return XML cofiguration file patch
     **************************************************************************/
    public String getConfigPath() {
        return System.getProperty("user.home") + File.separator + ".AndariaPatcherConfig.xml";
    }
    /***************************************************************************
     * @return os oriented patch script command
     **************************************************************************/
    public String getBatchExecCommand(File f) {
        return "wine cmd /C " + f.getName();
    }
    /***************************************************************************
     * @return default settings
     **************************************************************************/
    public Document getDefaults() {
        JOptionPane.showMessageDialog(null, "Nebyl nalezen konfiguracni soubor (" + getConfigPath() + ").\nZkontrolujte a ulozte prosim vase nastaveni.", "Upozorneni !",JOptionPane.WARNING_MESSAGE );
        Element root = new Element("main");
        Element sett = new Element("settings");
        sett.addContent(new Element("local_storage").setText(System.getProperty("java.io.tmpdir")+File.separator+"AndariaPatcher" ));
        sett.addContent(new Element("remote_storage").setText("http://space.andaria.net/data/Andaria_Soubory"));
        sett.addContent(new Element("news_url").setText("http://www.andaria.net/novinky_updater.php"));
        sett.addContent(new Element("unrar_command").setText("unrar -o+ e %rar%"));
        sett.addContent(new Element("debug_log").setText("0"));
        
        Element patches = new Element("patchlist");
        Document doc = new Document(root);
        root.addContent(sett);
        root.addContent(patches);
       
        return doc;
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
    public  void unrarInit() {
        
    }
}