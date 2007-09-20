package AndariaPatcher;

import java.io.OutputStream;
import java.net.URLConnection;
import java.io.InputStream;
import java.net.URL;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/*******************************************************************************
 * Downloader: Downloade files and start instalator when file ready.
 * When file don't exists on localhost, download it. After correct file found on
 * local temp storage, add patchitem to Installator object (installator) and
 * run it.
 *
 * @author  Martin Polehla (polous@katka.biz)
 * @version 0.1
 *******************************************************************************/

class Downloader extends PatcherQueue {
    
    Installator installator;
    /***************************************************************************
     * Creates a new instance of Downloader
     ***************************************************************************/
    public Downloader(Installator inst ) {
        super();
        installator = inst;
    }
    /***************************************************************************
     * Cancel current downloads and send message to patch installator object.
     ***************************************************************************/
    public void cancel(){
        super.cancel();
        installator.cancel();
    }
    /***************************************************************************
     * Main download procedure
     *  - SetTotal amount of install object to same like this (It suppose, user want install all files)
     *  - Set progress
     *  - Check if file exists at local storage (if yes, finish downloading)
     *  - Downlaod file
     *  - Check if file downloaded correct (if yes, finish downloading, else remove file from queue and print error message)
     **************************************************************************/
    synchronized void executeNext() {
        setInProgress();
        //  - SetTotal amount of install object to same like this (It suppose, user want install all files)
        PatchItem p = getFirstItem();
        installator.setTotalAmount(getTotalAmount());
        
        // - Set progress
        resetSingleDone((double) p.getSize());
        
        
        // - Check if file exists at local storage (if yes, finish downloading)
        String fileName = p.getLocalFileName();
        if ( p.checkHash() ){
            setLabelText("Kontroluju soubor: "+ p.getFileName());
            log.addLine("Soubor: " + p.getFileName() +" uz je stazeny.");
            startInstaller(p);
            return;
        }
        
        
        // - Downlaod file
        OutputStream out = null;
        URLConnection conn = null;
        InputStream  in = null;
        setLabelText("Stahuju soubor: " + p.getFileName());
        log.addLine("Stahuju soubor: " + p.getFileName());
        
        try {
            String uri = p.getRemoteFileName();
            
            URL url = new URL( uri );
            out = new BufferedOutputStream( new FileOutputStream( Settings.getFileExistingInstance(fileName) ));
            conn = url.openConnection();
            in = conn.getInputStream();
            
            long length = conn.getContentLength();
            long done = 0;
            
            
            
            byte[] buff = new byte[2048];
            int numRead;
            while ((numRead = in.read(buff)) != -1 ) {
                if (canceled()) {
                    //singleDone((double) 0);
                    return;
                }
                //done += numRead;
                addSingleDone((double) numRead);
                //singleDone((double) done);
                out.write(buff, 0, numRead);
            }
            out.close();
            in.close();
            
            // - Check if file downloaded correct (if yes, finish downloading, else remove file from queue and print error message)
            setLabelText("Kontroluju soubor: "+ p.getFileName());
            if (p.checkHash() ) {
                startInstaller(p);
                return;
            } else {
                log.addLine("Doslo k chybe pri stahovani souboru: " +  p.getRemoteFileName() + ". Soubor vynechavam, zkuste to znova.");
                singleDone((double) p.getSize());
                installator.removeTotalAmount(p.getSize());
                removeFirst();
            }
            
        } catch (Exception e) {
            log.addLine(this.getClass().getName() + " hlasi chybu: " + e.toString());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException ioe) { }
        }
        resetInProgress();
    }
    /***************************************************************************
     * Downlaod finish procedure
     *  update download status
     *  add item to installer queue and run pi using safeWork();
     *  remove downlaoded file from download queue
     **************************************************************************/
    private void startInstaller( PatchItem p ) {
        p.setDownloaded(true);
        singleDone((double) p.getSize());
        installator.addPatchItem(p);
        removeFirst();
        installator.safeWork();
    }
}
