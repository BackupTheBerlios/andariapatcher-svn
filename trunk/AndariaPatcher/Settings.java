package AndariaPatcher;

import java.io.File;
import java.io.FileWriter;
import org.jdom.output.XMLOutputter;
import java.io.IOException;
import java.util.Date;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;


/*******************************************************************************
 * Settings: Take cares about settings and settings XML file.
 * @author  Martin Polehla (polous@katka.biz)
 * @version 0.1
 ******************************************************************************/
public class Settings {
    
    
    static public OperatingSystem os;
    
    private static Document doc;        // XML settings document
    private static Element  root;       // root element of document
    private static Element  settings;   // settings of patcher
    private static Element patches;     // content of local stored patchlist
    private static Log log;
    
    // public static final boolean debug = false;
    
    public static final String FILE_LIST_URI = "http://www.andaria.net/admin/patcher.txt";
    
    static {
        os = OperatingSystem.createOperatingSystemInstance();
        log = new Log("Settings");
        
    }
    
    public Settings() { }
    
    /***************************************************************************
     * Get configuration value specified by config name.
     * @param item  Name of sub-element
     * @return      Required item (string)
     **************************************************************************/
    public static String getValue(String item) {
        if (settings == null) return "";
        try {
            return settings.getChildText(item);
        } catch (IllegalArgumentException e) {
            //System.err.println(e);
        } catch (NullPointerException e ){
            //System.err.println(e);
        }
        return "";
    }
    /***************************************************************************
     * Set a configuration settings (not save).
     * @param item  Name of element to set
     * @param val   New value of item
     **************************************************************************/
    public static void setValue(String item, String val) {
        if (settings == null) return;
        try {
            settings.getChild(item).setText(val);
        } catch (NullPointerException e) {
            settings.addContent(new Element(item).setText(val) );
        }
    }
    /***************************************************************************
     * Save actual PatchItem to XML file
     * @param p    PatchItem
     **************************************************************************/
    public static void setValue(PatchItem p) {
        if (patches == null) return;
        Element ch = patches.getChild(p.getFileName());
        if (ch == null) {
            ch = new Element(p.getFileName());
            patches.addContent(ch);
        }
        ch.setText(p.getHash());
        ch.setAttribute("version", p.getVersion());
        ch.setAttribute("date", p.dateFormat.format(new Date()));
        save();
    }
    
    /***************************************************************************
     * @param el    Element where to look for item
     * @param item  Name of sub-element
     * @return      Return required element or new empty one.
     **************************************************************************/
    private static Element getElement(Element el, String item) {
        List list = el.getChildren(item);
        if (list.size() > 0)
            return (Element) list.get(0);
        else
            return new Element(item);
    }
    /***************************************************************************
     * @return PatchItem data Element
     **************************************************************************/
    public static Element getPatchData(String item) {
        return patches.getChild(item);
    }
    /***************************************************************************
     * Load settings, doc, and paches objects from XML file
     **************************************************************************/
    public static void load() {
        
        File f = getFileExistingInstance(os.getConfigPath());
        Document newdoc = null;
        if (f.exists() ) {
            SAXBuilder parser = new SAXBuilder();
            try {
                newdoc = parser.build( f );
                root = newdoc.getRootElement();
            }    catch (JDOMException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        if (newdoc == null || root == null) {
            newdoc = os.getDefaults();
            root = newdoc.getRootElement();
        }
        doc = newdoc;
        settings = getElement(root, "settings");
        patches = getElement(root, "patchlist");
        
    }
    /***************************************************************************
     * Save settings and paches objects into XML file
     **************************************************************************/
    public static void save() {
        if (doc ==null) return;
        SAXBuilder parser = new SAXBuilder();
        File f = getFileExistingInstance(os.getConfigPath());
        try {
            if (!f.exists() )
                f.createNewFile();
        } catch (IOException e ) { System.err.println(e); }
        
        try {
            XMLOutputter out = new XMLOutputter();
            FileWriter fw = new FileWriter( f );
            out.output(doc , fw );
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    public static File getFileExistingInstance(File f) {
        if (f.exists())  {
            return f;
        } else {
            try {
                if (f.isDirectory()){
                    f.mkdirs();
                }else {
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                }
            } catch (IOException e) { //log.addEx(e);
                log.addLine("Nemuzu vytvorit soubor (adresar): " + f.getAbsolutePath());
            }
        }
        return f;
    }
    public static File getFileExistingInstance(String fn) {
        return getFileExistingInstance(new File(fn));
    }
    public static boolean debugMode() {
        if (getValue("debug_log").equalsIgnoreCase("1"))
            return true;
        else
           return false;
                
    }
}
