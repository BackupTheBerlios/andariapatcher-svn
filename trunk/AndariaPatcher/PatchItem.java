package AndariaPatcher;

import java.awt.Color;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.text.ParseException;
import javax.swing.JPanel;
import java.io.File;
import java.security.MessageDigest;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.NoSuchAlgorithmException;
import org.jdom.Element;


/*******************************************************************************
 * PatchItem: Object of one patch file and procedures to control it.
 * @author  Martin Polehla (polous@katka.biz)
 * @version 0.1
 ******************************************************************************/
public class PatchItem extends Logable {
    private String name;                    // patch name2
    private BigInteger hash;      // MD5 hash
    private String version;                 // latest version
    private String currentVersion;          // installed version
    private Date date;            // date of release
    private Date currentDate;     // date of installed patch
    private Boolean required;               // reqired patch (by server)
    private Boolean wanted;                 // auto-patch
    private Boolean installed;              // is installed
    private String description;             // patch description
    private String fileName;                // file name
    private long size;                      // file size
    private Boolean downloaded;             // is file downloaded and ready for instalation ?
    final public  DateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy, hh:mm");
    
    public PatchPanel panel;
    
    
    /***************************************************************************
     * Creates a new instance of PatchItem
     **************************************************************************/
    public PatchItem(String[] data) {
        super();
        name = data[1];
        hash = new BigInteger(data[3], 16);
        version = data[6];
        currentVersion = data[6];
        try {
            date = dateFormat.parse(data[2]);
        } catch (ParseException e) {
            log.addLine("Chyba rozpoznani datumu patche: ".concat(e.getMessage()));
        }
        required = (data[4].equalsIgnoreCase("1")?true:false);
        wanted = (data[4].equalsIgnoreCase("1")?true:false);
        
        description = data[7];
        fileName = data[0];
        size = Long.parseLong(data[5]);
        downloaded = false;
        
        Element el = Settings.getPatchData(getFileName());
        
        if (el != null) {
            try {
                // determinate if patch allready installed.
                // It will take config hash value of patch with same filename (if any)
                // and compare it with this PatchItem instance hash value
                installed = el.getText().equalsIgnoreCase( getHash() );
                currentDate = dateFormat.parse(el.getAttributeValue("date"));
                currentVersion = el.getAttributeValue("version");
            } catch (ParseException e) {
                log.addLine("Chyba rozpoznani ulozenych dat patche: ".concat(e.getMessage()));
            }
        } else {
            installed = false;
        }
        panel = new PatchPanel(this);
        updateState();
    }
    
    public String getInLine() {  return name; }
    public JPanel getInFrame() { return panel; }
    public String getLocalFileName() { return Settings.getValue("local_storage") + File.separator + fileName ;  }
    public String getRemoteFileName() { return Settings.getValue("remote_storage").concat("/").concat(fileName);  }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getDates() {
        String cd;
        try {
            cd = dateFormat.format(currentDate);
        } catch (NullPointerException e) {
            cd = "-";
        }
        return (dateFormat.format(date)).concat(" (").concat(cd).concat(")");
    }
    public String getVersion() { return version; }
    public String getVersions() {
        return version.concat(" (").concat((currentVersion==null?"-":currentVersion)).concat(")");
    }
    public String getHash() { return hash.toString(16); }
    public String getFileName() { return fileName; }
    public long getSize() { return size; }
    public boolean isRequired() { return required; }
    public boolean isWanted() { return wanted; }
    private boolean isInstalled() { return installed; }
    
    public void setDownloaded(boolean b) { downloaded = b; }
    
    public void setWanted(boolean b) {
        wanted = b;  
        panel.setWanted( wanted ); 
    }
    
    public void setRequired(boolean b) {
        required = b;
        panel.setRequired( required );
    }
    
    /***************************************************************************
     * Control local if local file hash (md5) is rigth
     * @return If hash check passed. If an error ocures, returned value will be false too.
     **************************************************************************/
    synchronized public boolean checkHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            File f = new File(getLocalFileName());
            InputStream is = new FileInputStream(f);
            
            byte[] buffer = new byte[8192];
            int read = 0;
            while( (read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            is.close();
            
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String md5 = bigInt.toString(16);
            return (0==bigInt.compareTo(hash));
            
        } catch (NoSuchAlgorithmException e) {
            log.addLine("Chyba inicializace hash algoritmu !");
        } catch (IOException e) {
            log.addLine("Nemuzu otevrit soubor pro kontrolu hash !");
        }
        return false;
    }
    /***************************************************************************
     * Set patch Item as intalled
     * - uncheck in patchlist
     * - set to settings
     **************************************************************************/
    public void setInstalled() {
        installed = true;
        updateState();
        Settings.setValue(this);
        
    }
    private void updateState() {
        setRequired( ! installed && required);
        setWanted( wanted && (! installed && required ));
    }
    
}
