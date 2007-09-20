package AndariaPatcher;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/*******************************************************************************
 * Installator: take care about installing downloaded files.
 * Downloader adds files into installator queue and run it.
 * Installator unpack files to ultima's directory and try execute start_a.bat
 * or start_g.bat if exists.
 *
 * @author  Martin Polehla (polous@katka.biz)
 * @version 0.1
 ******************************************************************************/

class Installator extends PatcherQueue {
    /***************************************************************************
     * Creates a new instance of Runner
     **************************************************************************/
    public Installator() {
        super();
        //  log.addLine("PatchAplier inicializovan.\n", 0);
    }
    /***************************************************************************
     * Add new PatchItem to queue.
     * Override PatchQueue object method to remove totalAmount (file size)
     * adding. totalAmount variable is set by Downloader.
     * @param p item to add
     * @see Downloader.executeNext()
     **************************************************************************/
    protected void addPatchItem(PatchItem p) {
        patchQueue.add(p);
    }
    
    /***************************************************************************
     * Run file in separated thread and pause installator thread using wait() method. Installator thread is resumed using work() method.
     *  Note: this is reason of existing safeWork()
     *  @param command Command to execute
     **************************************************************************/
    private synchronized void exec(final String command) {
        log.addLine("Spoustim prikaz: "+ command + ".");
        exec(command.split(" "));
    }
    /***************************************************************************
     * Run file in separated thread and pause installator thread using wait()
     * method. Installator thread is resumed using work() method.
     *  Note: this is reason of existing safeWork()
     *  @param command Command to execute
     **************************************************************************/
    private synchronized void exec(final String[] command) {
        Thread t = new Thread() {
            public void run() {
                try {
                    Process proc = Runtime.getRuntime().exec(command, null, new File(Settings.getValue("ultima_online_path")));
                    if (Settings.debugMode()) {
                        try {
                            String line;
                            
                            BufferedReader input = new BufferedReader
                                    (new InputStreamReader(proc.getInputStream()));
                            while ((line = input.readLine()) != null) {
                                log.addDebug("[" + command[0] +"]: "+ line );
                            }
                            input.close();
                        } catch (Exception e) { log.addEx(e);
                        }
                    } else {
                        
                        proc.waitFor();
                    }
                } catch (InterruptedException e) { log.addEx(e);
                } catch (IOException e) { log.addEx(e);
                }
                work();
            }
        };
        t.start();
        try {
            wait();
        } catch (InterruptedException e) { log.addEx(e);
        }
    }
    /***************************************************************************
     * Main install procedure
     *  - (re)set progress indicator (for fun of users, generated random value .-) )
     *  - Unpack patch files
     *  - if exists start_a.bat, execute it
     *  - if exists start_g.bat, execute it
     *  - finish install procedure and remove file from queue
     **************************************************************************/
    synchronized void executeNext() {
        setInProgress();
        PatchItem patchItem = getFirstItem();
        
        // - reset progress
        resetSingleDone(patchItem.getSize());
        // - Unpack patch files
        setLabelText("Rozbaluju patch: "+ patchItem.getFileName());
        final String uopath = Settings.getValue("ultima_online_path");
        singleDone(  ( (double) patchItem.getSize() )/ (5+Math.random()*10));
        log.addDebug("Pracuju se souborem: " + patchItem.getLocalFileName());
        
        exec(Settings.getValue("unrar_command").replace("%rar%", patchItem.getLocalFileName() ).replace("%adresar%", uopath) );
        singleDone(  ( (double) patchItem.getSize() )/2);
        
        // - if exist start_a.bat, execute it
        File f = new File( uopath + File.separator +"start_a.bat");
        if (f.exists()) {
            setLabelText("Instaluju patch: "+patchItem.getFileName());
            exec(Settings.os.getBatchExecCommand(f));
            f.delete();
        }
        // - if exist start_g.bat, execute it
        f = new File( uopath + File.separator +"start_g.bat");
        if (f.exists()) {
            setLabelText("Instaluju patch: "+patchItem.getFileName());
            exec(Settings.os.getBatchExecCommand(f));
            f.delete();
        }
        // - finish install procedure and remove file from queue
        
        // Fake progress ? :-) .. maybe later, I don't wanna think about it now :-P
        // singleDone(  ( (double) patchItem.getSize() )/(Math.random()*5));
        // wait(300);
        setLabelText("Prace dokoncena ("+patchItem.getFileName()+").");
        singleDone((double) patchItem.getSize());
        
        
        patchItem.setInstalled();
        removeFirst();
        resetInProgress();
    }
    
    
}
