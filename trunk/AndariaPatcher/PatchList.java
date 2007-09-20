package AndariaPatcher;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;
import java.io.IOException;

/*******************************************************************************
 * installatortchList: List of installatortches, offers download start procedure.
 * @author  Martin Polehla (polous@katka.biz)
 * @version 0.1
 *******************************************************************************/

public class PatchList extends Logable {
    private Vector data = new Vector();
    private Downloader downloader; // downloader object
    private Installator installator;
    
    private Thread downloadThread, installThread;
    private Thread t;
    
    /***************************************************************************
     * Creates a new instance of installatortchList
     **************************************************************************/
    PatchList() {
        
        installator = new Installator();
        downloader = new Downloader(installator);
        
        installThread = new Thread( installator );
        downloadThread = new Thread( downloader );
        
        downloader.setSingleBP(FrontEnd.instance.getjPBDownloadSingle());
        downloader.setTotalBP(FrontEnd.instance.getjPBDownloadTotal());
        downloader.setLabel(FrontEnd.instance.getjLDownload());
        
        installator.setSingleBP(FrontEnd.instance.getjPBInstall());
        installator.setTotalBP(FrontEnd.instance.getjPBTotal());
        installator.setLabel(FrontEnd.instance.getjLInstall());
        
        read();
        
        downloadThread.start();
        installThread.start();
    }
    /***************************************************************************
     * Return count of installatortches
     * @return amount of installatortchItem in list
     **************************************************************************/
    public int getCount() {
        return data.size();
    }
    /***************************************************************************
     * Read installatortchList from remote storage and fill data  vector
     * (list of installatortchItems).
     **************************************************************************/
    private void read() {
        t = new Thread() {
            public boolean canceled = false;
            private Log log = new Log("ReaderThread");
            public void run() {
                try {
                    log.addLine("Zacinam stahovat seznam patchu z internetu.");
                    URL url = new URL(Settings.FILE_LIST_URI);
                    URLConnection connection = url.openConnection();
                    InputStream in = connection.getInputStream();
                    Reader reader = new InputStreamReader(in, "UTF-8");
                    BufferedReader br = new BufferedReader(reader);
                    
                    String sLine; // Line buffer
                    String [] sItems; // Item Buffer
                    for (int i=0; br.ready(); i++) {
                        if (canceled) {
                            reader.close();
                            return;
                        }
                        sLine = br.readLine();
                        sItems = sLine.split(";");
                        data.add(new PatchItem(sItems));
                    }
                    reader.close();
                    FrontEnd.instance.refreshPlPane();
                    log.addLine("Seznam patchu byl nahran z internetu.");
                } catch (IOException e) { log.addEx(e); }
            }
        };
        t.start();
    }
    public boolean isWorking() {
        return (downloader.inProgress() && installator.inProgress());
    }
    /***************************************************************************
     * Return installatortch list at jinstallatornel using installatortchinstallatornel objects.
     * @return jinstallatornel containig installatortchinstallatornel objects for all installatortches in list.
     **************************************************************************/
    public Vector getInPanel() {
        PatchItem patchItem;
        Vector result = new Vector();
        
        for(int i=0; i<data.size() ;i++) {
            patchItem = (PatchItem) data.get(i);
            result.add(patchItem.getInFrame());
        }
        return result;
    }
    /***************************************************************************
     * Cancel downloads and installations
     **************************************************************************/
    public void cancelDownload() { downloader.cancel(); }
    /***************************************************************************
     * Start downloads and installations
     **************************************************************************/
    synchronized public void download() {
        PatchItem patchItem;
        downloader.reset();
        for(int i=0; i<data.size() ;i++) {
            patchItem = (PatchItem) data.get(i);
            if (patchItem.isWanted()) {
                downloader.addPatchItem(patchItem);
            }
        }
        downloader.safeWork();
    }
}
