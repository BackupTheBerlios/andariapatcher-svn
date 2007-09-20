package AndariaPatcher;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import org.jdom.Document;
import org.jdom.Element;

/*******************************************************************************
 *
 * @author p0l0us
 ******************************************************************************/
public class WindowsOS extends OperatingSystem {
    
    /***************************************************************************
     * Creates a new instance of WindowsOS
     **************************************************************************/
    WindowsOS() {
        super();
        System.err.println("OPERACNI SYSTEM WINDOWS");
    }
    /***************************************************************************
     * @return XML cofiguration file patch
     **************************************************************************/
    public String getConfigPath() {
        return getRegUoPath() + File.separator + "AndariaPatcherConfig.xml";
    }
    /***************************************************************************
     * @return os oriented patch script command
     **************************************************************************/
    public String getBatchExecCommand(File f) {
        return f.getAbsolutePath();
    }
    
    private static byte[] toByteArray(String str) {
        byte[] result = new byte[str.length() + 1];
        for (int i = 0; i < str.length(); i++) {
            result[i] = (byte) str.charAt(i);
        }
        result[str.length()] = 0;
        return result;
    }
    /***************************************************************************
     * @return ultima online path from windows registers
     **************************************************************************/
    
    private String getRegUoPath() {
        String uopath = null;
        
        final int HKEY_LOCAL_MACHINE = 0x80000002;
        final int KEY_QUERY_VALUE = 1;
        final Preferences root =  Preferences.systemRoot();
        final Class cl = root.getClass();
        final Method queryValue;
        final int KEY_READ = 0x20019;
        final String subKey = "SOFTWARE\\Origin Worlds Online\\Ultima Online\\1.0";
        
        Class[] params = {int.class, byte[].class};
        
        
        try {
            
            Class[] parms1 = {byte[].class, int.class, int.class};
            final Method mOpenKey = cl.getDeclaredMethod("openKey", parms1);
            mOpenKey.setAccessible(true);
            
            Class[] parms2 = {int.class};
            final Method mCloseKey = cl.getDeclaredMethod("closeKey", parms2);
            mCloseKey.setAccessible(true);
            
            Class[] parms3 = {int.class, byte[].class};
            final Method mWinRegQueryValue = cl.getDeclaredMethod( "WindowsRegQueryValueEx", parms3);
            mWinRegQueryValue.setAccessible(true);
            
            Object[] objects1 = {toByteArray(subKey), new Integer(KEY_READ), new Integer(KEY_READ)};
            Integer hSettings = (Integer) mOpenKey.invoke(root, objects1);
            
            Object[] objects2 = {hSettings, toByteArray("InstCDPath")};
            byte[] b = (byte[]) mWinRegQueryValue.invoke(root, objects2);
            String value = (b != null ? new String(b).trim() : null);
            
            Object[] objects3 = {hSettings};
            mCloseKey.invoke(root, objects3);
            
            uopath = value;
        } catch (InvocationTargetException e) {
            System.err.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println(e.getMessage());
        } catch (SecurityException e) {
            System.err.println(e.getMessage());
        } catch (NoSuchMethodException e) {
            System.err.println(e.getMessage());
        }
        
        
        if (uopath==null) {
            JOptionPane.showMessageDialog(null, "Nemuzu najit zaznam o ultime v registrech.\nBud nemas nainstalovaou uo spravne, nebo je to rozbity. Zkus ultimu preinstalovat ultimu.\nPokud to nepomuze, napis p0l0usovi na foru andarie o pomoc.", "Upozorneni !",JOptionPane.WARNING_MESSAGE );
            return null;
        }
        System.out.println("Rozpoznany adresar s ultimou: ".concat(uopath));
        return uopath;
    }
    
    /***************************************************************************
     * @return default settings
     **************************************************************************/
    public Document getDefaults() {
        JOptionPane.showMessageDialog(null, "Nebyl nalezen konfiguracni soubor (" + getConfigPath() + ").\nZkontrolujte a ulozte prosim vase nastaveni.", "Upozorneni !",JOptionPane.WARNING_MESSAGE );
        Element root = new Element("main");
        Element sett = new Element("settings");
        sett.addContent(new Element("local_storage").setText(getRegUoPath( )+ File.separator + "AndariaPatcherFiles") );
        sett.addContent(new Element("remote_storage").setText("http://space.andaria.net/data/Andaria_Soubory"));
        sett.addContent(new Element("news_url").setText("http://www.andaria.net/novinky_updater.php"));
        sett.addContent(new Element("unrar_command").setText("C:\\Program Files\\WinRAR\\unrar.exe -o+ e %rar%"));
        sett.addContent(new Element("ultima_online_path").setText(getRegUoPath()));
        sett.addContent(new Element("debug_log").setText("0"));
        
        Element patches = new Element("patchlist");
        Document doc = new Document(root);
        root.addContent(sett);
        root.addContent(patches);
        
        return doc;
    }
}
