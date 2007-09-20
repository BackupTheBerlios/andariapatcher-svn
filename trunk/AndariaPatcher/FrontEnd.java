package AndariaPatcher;

import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import javax.swing.JFrame;
import java.awt.*;
import javax.swing.*;
import java.io.IOException;
import java.io.File;
import java.util.Vector;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/*******************************************************************************
 * Main class of FrontEnd. Displays form and hadle controll components.
 *
 * @author Martin Polehla (polous@katka.biz)
 * @version 0.1
 ******************************************************************************/
public class FrontEnd extends JFrame {
    
    
    private Log log;
    volatile public static FrontEnd instance; //representation of main class (this)
    volatile private PatchList patchList; // representation of patchlist, patch procedure control object
    
    /***************************************************************************
     * Creates new form FrontEnd and call pl inicialization
     **************************************************************************/
    public FrontEnd() {
        instance = this;
        
        initComponents();
        Log.logArea = jTLog;
        log = new Log(this);
        jTPMain.setTitleAt(0, "Co je noveho");
        jTPMain.setTitleAt(1, "Prubeh patchovani");
        jTPMain.setTitleAt(2, "Vyber dostupnych patchu");
        jTPMain.setTitleAt(3, "Nastaveni programu");
        
        loadSettings();
        
        openURL(Settings.getValue("news_url"));
        
        reloadPatchList();
        
        if (Settings.debugMode()) {
            
            log.addDebug(System.getProperty("os.name"));
            log.addDebug(System.getProperty("user.home"));
            log.addDebug(System.getProperty("java.io.tmpdir"));
        }
        //Browser b = new Browser(initialPage);
    }
    
    /***************************************************************************
     * Application runner
     * @param args the command line arguments
     **************************************************************************/
    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrontEnd().setVisible(true);
            }
        });
    }
    
    /***************************************************************************
     * Open an url
     * @param http addres string
     **************************************************************************/
    private void openURL(final String uri) {
        Thread t = new Thread() {
            public void run() {
                try {
                    log.addLine("Oteviram stranku s novinkama: " + uri);
                    jEPBrowser.addHyperlinkListener(new hyperlink());
                    jEPBrowser.setPage(uri);
                } catch (IOException e) {
                    log.addEx(e);
                }
            }
        };
        t.start();
    }
    
    /***************************************************************************
     * PatchList pl object inicialization, display list of patches at jPList panel.
     **************************************************************************/
    private void reloadPatchList() {
        jBDownlaod.setEnabled(false);
        patchList = new PatchList();
    }
    
    /***************************************************************************
     * Inicialize application settings and settings form.
     **************************************************************************/
    private void loadSettings() {
        Settings.load();
        
        jTConfRunCommand.setText(Settings.getValue("run_command"));
        jTConfUnRARCommand.setText(Settings.getValue("unrar_command"));
        jTConfUltimaOnlinePath.setText(Settings.getValue("ultima_online_path"));
        jTConfTempPath.setText(Settings.getValue("local_storage"));
        jTConfNewsURL.setText(Settings.getValue("news_url"));
        jChDebug.setSelected( Settings.debugMode() );
        
        // not used yet
        //jChParallelProcess.setSelected( new Boolean( Settings.getValue("parallel_process") ) );
        
    }
    
    /***************************************************************************
     * Store application settings.
     **************************************************************************/
    private void saveSettings() {
        Settings.setValue("run_command", jTConfRunCommand.getText());
        Settings.setValue("unrar_command", jTConfUnRARCommand.getText());
        Settings.setValue("ultima_online_path", jTConfUltimaOnlinePath.getText());
        Settings.setValue("local_storage", jTConfTempPath.getText());
        Settings.setValue("news_url", jTConfNewsURL.getText());
        Settings.setValue("debug_log", (jChDebug.isSelected()?"1":"0") );
        
        // not used yet
        //Settings.setValue("parallel_process", String.valueOf(jChParallelProcess.isSelected() ));
        
        Settings.save();
    }
    
    /***************************************************************************
     * Open file dialog openner.
     * @param title Title of JFileCooser
     * @param defPath Default path of JFileChooser
     * @param ft Selection mode (ie. JFileCooser.DIRECTORY)
     **************************************************************************/
    private String openFile(String title, String defPath, int ft) {
        final JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(title);
        fc.setCurrentDirectory(new File(defPath).getParentFile());
        fc.setFileSelectionMode(ft);
        fc.setFileHidingEnabled(false);
        
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            return file.getAbsolutePath();
        }
        return defPath;
    }
    
    private Image getIcon() {
        return Toolkit.getDefaultToolkit().getImage( getClass().getResource( "andaria.png" ) );
    }
    public JProgressBar getjPBDownloadSingle(){ return jPBDownloadSingle; }
    public JProgressBar getjPBDownloadTotal(){ return jPBDownloadTotal; }
    public JLabel getjLDownload(){ return jLDownload; }
    
    public JProgressBar getjPBInstall() { return jPBInstall; }
    public JProgressBar getjPBTotal() { return jPBTotal; }
    public JLabel getjLInstall() { return jLInstall; }
    
    /***************************************************************************
     * Refresh patchlist tab content
     * @see PatchList.getInaPanel()
     **************************************************************************/
    public void refreshPlPane() {
        Vector jPlist = patchList.getInPanel();
        jPPatchList.removeAll();
        int i;
        for (i = 0; i< jPlist.size();i++) {
            jPPatchList.add( (JPanel) jPlist.get(i));
        }
        if (i > 0) jPPatchList.setLayout(new GridLayout(patchList.getCount(), 0));
        updateButtons();
        pack();
    }
    
    /***************************************************************************
     * Update front end button state (enabled or disabled).
     * Usualy called when downloader or installer progress state may be changed.
     * @see Downloader & Installer objects.
     **************************************************************************/
    public void updateButtons() {
        try {
            if (patchList.isWorking()) {
                jBDownlaod.setEnabled(false);
                jBCancel.setEnabled(true);
                jBClose.setEnabled(false);
            } else {
                jBDownlaod.setEnabled(true);
                jBCancel.setEnabled(false);
                jBClose.setEnabled(true);
            }
        } catch (NullPointerException e) { }
        
        
    }
    
    /***************************************************************************
     * Custom hyperlink listener. Used by jTBrowser JTextArea.
     * @see openURL()
     **************************************************************************/
    private class hyperlink implements HyperlinkListener {
        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                JEditorPane pane = (JEditorPane) e.getSource();
                if (e instanceof HTMLFrameHyperlinkEvent) {
                    HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent) e;
                    HTMLDocument doc = (HTMLDocument)pane.getDocument();
                    doc.processHTMLFrameHyperlinkEvent(evt);
                } else {
                    try {
                        pane.setPage(e.getURL());
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        }
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jTPMain = new javax.swing.JTabbedPane();
        jSPBrowserScroll = new javax.swing.JScrollPane();
        jEPBrowser = new javax.swing.JEditorPane();
        jPControls = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jBRefreshPatchList = new javax.swing.JButton();
        jBDownlaod = new javax.swing.JButton();
        jBCancel = new javax.swing.JButton();
        jBClose = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTLog = new javax.swing.JTextArea();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel5 = new javax.swing.JPanel();
        jLDownload = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        jPBDownloadSingle = new javax.swing.JProgressBar();
        jSeparator6 = new javax.swing.JSeparator();
        jPBDownloadTotal = new javax.swing.JProgressBar();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel6 = new javax.swing.JPanel();
        jLInstall = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jPBInstall = new javax.swing.JProgressBar();
        jSeparator5 = new javax.swing.JSeparator();
        jPBTotal = new javax.swing.JProgressBar();
        jSeparator4 = new javax.swing.JSeparator();
        jSPPatchList = new javax.swing.JScrollPane();
        jPPatchList = new javax.swing.JPanel();
        jPSettings = new javax.swing.JPanel();
        jLConfUltimaOnlinePath = new javax.swing.JLabel();
        jTConfUltimaOnlinePath = new javax.swing.JTextField();
        jBConfBrowseUltimaOnlinePath = new javax.swing.JButton();
        jLConfTempPath = new javax.swing.JLabel();
        jTConfTempPath = new javax.swing.JTextField();
        jBConfBrowseTempPath = new javax.swing.JButton();
        jLConfRunCommand = new javax.swing.JLabel();
        jTConfRunCommand = new javax.swing.JTextField();
        jBConfBrowseRunCommand = new javax.swing.JButton();
        jLConfUnRARCommand = new javax.swing.JLabel();
        jTConfUnRARCommand = new javax.swing.JTextField();
        jBConfBrowseUnRARCommand = new javax.swing.JButton();
        jChParallelProcess = new javax.swing.JCheckBox();
        jBConfLoad = new javax.swing.JButton();
        jBConfSave = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JSeparator();
        jLConfNewsURL = new javax.swing.JLabel();
        jTConfNewsURL = new javax.swing.JTextField();
        jChDebug = new javax.swing.JCheckBox();

        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.X_AXIS));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Andaria Patcher");
        setBackground(java.awt.Color.white);
        setFont(new java.awt.Font("Verdana", 1, 12));
        setForeground(new java.awt.Color(163, 125, 86));
        setIconImage(getIcon());
        setLocationByPlatform(true);
        setMaximizedBounds(new java.awt.Rectangle(0, 0, 2147483647, 2147483647));
        setName("AndariaPatcher");
        getAccessibleContext().setAccessibleDescription("Instalator souboru potrebnych pro hrani na Ultima Online RP Free Shradu Adaria");
        jTPMain.setBackground(getBackground());
        jTPMain.setForeground(getForeground());
        jTPMain.setToolTipText("Andaria patcher");
        jTPMain.setFont(new java.awt.Font("Verdana", 1, 14));
        jTPMain.setMinimumSize(new java.awt.Dimension(727, 481));
        jTPMain.setName("");
        jSPBrowserScroll.setMinimumSize(new java.awt.Dimension(722, 452));
        jSPBrowserScroll.setPreferredSize(new java.awt.Dimension(722, 452));
        jEPBrowser.setEditable(false);
        jEPBrowser.setToolTipText("");
        jEPBrowser.setAutoscrolls(false);
        jEPBrowser.setContentType("text/html");
        jEPBrowser.setFocusable(false);
        jEPBrowser.setMinimumSize(new java.awt.Dimension(722, 452));
        jEPBrowser.setPreferredSize(new java.awt.Dimension(722, 452));
        jSPBrowserScroll.setViewportView(jEPBrowser);

        jTPMain.addTab("tab5", jSPBrowserScroll);

        jPControls.setLayout(new javax.swing.BoxLayout(jPControls, javax.swing.BoxLayout.Y_AXIS));

        jPControls.setBackground(getBackground());
        jPControls.setForeground(getForeground());
        jPControls.setMinimumSize(new java.awt.Dimension(722, 452));
        jPControls.setPreferredSize(new java.awt.Dimension(722, 452));
        jPanel4.setLayout(new java.awt.GridLayout(1, 0));

        jPanel4.setBackground(getBackground());
        jPanel4.setForeground(getForeground());
        jPanel4.setMaximumSize(new java.awt.Dimension(32767, 25));
        jPanel4.setMinimumSize(new java.awt.Dimension(400, 25));
        jPanel4.setOpaque(false);
        jPanel4.setPreferredSize(new java.awt.Dimension(400, 25));
        jBRefreshPatchList.setBackground(getBackground());
        jBRefreshPatchList.setForeground(getForeground());
        jBRefreshPatchList.setText("Obnovit seznam");
        jBRefreshPatchList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBRefreshPatchListActionPerformed(evt);
            }
        });

        jPanel4.add(jBRefreshPatchList);

        jBDownlaod.setBackground(getBackground());
        jBDownlaod.setForeground(getForeground());
        jBDownlaod.setText("Stahnout a instalovat");
        jBDownlaod.setEnabled(false);
        jBDownlaod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDownlaodActionPerformed(evt);
            }
        });

        jPanel4.add(jBDownlaod);

        jBCancel.setBackground(getBackground());
        jBCancel.setForeground(getForeground());
        jBCancel.setText("Zrusit");
        jBCancel.setEnabled(false);
        jBCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelActionPerformed(evt);
            }
        });

        jPanel4.add(jBCancel);

        jBClose.setBackground(getBackground());
        jBClose.setForeground(getForeground());
        jBClose.setText("Zavrit");
        jBClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCloseActionPerformed(evt);
            }
        });

        jPanel4.add(jBClose);

        jPControls.add(jPanel4);

        jSeparator1.setBackground(getBackground());
        jSeparator1.setForeground(getForeground());
        jSeparator1.setMaximumSize(new java.awt.Dimension(32767, 5));
        jSeparator1.setMinimumSize(new java.awt.Dimension(0, 5));
        jSeparator1.setPreferredSize(new java.awt.Dimension(50, 5));
        jPControls.add(jSeparator1);

        jScrollPane2.setBackground(getBackground());
        jScrollPane2.setForeground(getForeground());
        jTLog.setBackground(getBackground());
        jTLog.setColumns(20);
        jTLog.setForeground(getForeground());
        jTLog.setRows(5);
        jScrollPane2.setViewportView(jTLog);

        jPControls.add(jScrollPane2);

        jSeparator2.setBackground(getBackground());
        jSeparator2.setForeground(getForeground());
        jSeparator2.setMaximumSize(new java.awt.Dimension(32767, 10));
        jSeparator2.setMinimumSize(new java.awt.Dimension(0, 10));
        jSeparator2.setPreferredSize(new java.awt.Dimension(50, 10));
        jPControls.add(jSeparator2);

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.Y_AXIS));

        jPanel5.setBackground(getBackground());
        jPanel5.setForeground(getForeground());
        jLDownload.setBackground(getBackground());
        jLDownload.setForeground(getForeground());
        jLDownload.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLDownload.setLabelFor(jPBDownloadSingle);
        jLDownload.setText("Nic nestahuju");
        jLDownload.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLDownload.setMaximumSize(new java.awt.Dimension(99999, 13));
        jLDownload.setMinimumSize(new java.awt.Dimension(400, 13));
        jLDownload.setPreferredSize(new java.awt.Dimension(400, 13));
        jPanel5.add(jLDownload);

        jSeparator7.setBackground(getBackground());
        jSeparator7.setForeground(getForeground());
        jSeparator7.setMaximumSize(new java.awt.Dimension(32767, 5));
        jSeparator7.setMinimumSize(new java.awt.Dimension(0, 5));
        jSeparator7.setPreferredSize(new java.awt.Dimension(50, 5));
        jPanel5.add(jSeparator7);

        jPBDownloadSingle.setBackground(getBackground());
        jPBDownloadSingle.setForeground(getForeground());
        jPBDownloadSingle.setToolTipText("Prubeh stahovani aktualniho souboru.");
        jPBDownloadSingle.setBorder(null);
        jPBDownloadSingle.setStringPainted(true);
        jPanel5.add(jPBDownloadSingle);

        jSeparator6.setBackground(getBackground());
        jSeparator6.setForeground(getForeground());
        jSeparator6.setMaximumSize(new java.awt.Dimension(32767, 5));
        jSeparator6.setMinimumSize(new java.awt.Dimension(0, 5));
        jSeparator6.setPreferredSize(new java.awt.Dimension(50, 5));
        jPanel5.add(jSeparator6);

        jPBDownloadTotal.setBackground(getBackground());
        jPBDownloadTotal.setForeground(getForeground());
        jPBDownloadTotal.setToolTipText("Prubeh stahovani aktualniho souboru.");
        jPBDownloadTotal.setBorder(null);
        jPBDownloadTotal.setStringPainted(true);
        jPanel5.add(jPBDownloadTotal);

        jPControls.add(jPanel5);

        jSeparator3.setBackground(getBackground());
        jSeparator3.setForeground(getForeground());
        jSeparator3.setMaximumSize(new java.awt.Dimension(32767, 10));
        jSeparator3.setMinimumSize(new java.awt.Dimension(0, 10));
        jSeparator3.setPreferredSize(new java.awt.Dimension(50, 10));
        jPControls.add(jSeparator3);

        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.Y_AXIS));

        jPanel6.setBackground(getBackground());
        jPanel6.setForeground(getForeground());
        jLInstall.setBackground(getBackground());
        jLInstall.setForeground(getForeground());
        jLInstall.setLabelFor(jPBInstall);
        jLInstall.setText("Nic neinstaluju");
        jLInstall.setMaximumSize(new java.awt.Dimension(99999, 13));
        jLInstall.setMinimumSize(new java.awt.Dimension(400, 13));
        jLInstall.setPreferredSize(new java.awt.Dimension(400, 13));
        jPanel6.add(jLInstall);

        jSeparator8.setBackground(getBackground());
        jSeparator8.setForeground(getForeground());
        jSeparator8.setMaximumSize(new java.awt.Dimension(32767, 5));
        jSeparator8.setMinimumSize(new java.awt.Dimension(0, 5));
        jSeparator8.setPreferredSize(new java.awt.Dimension(50, 5));
        jPanel6.add(jSeparator8);

        jPBInstall.setBackground(getBackground());
        jPBInstall.setForeground(getForeground());
        jPBInstall.setToolTipText("Prubeh stahovani aktualniho souboru.");
        jPBInstall.setBorder(null);
        jPBInstall.setStringPainted(true);
        jPanel6.add(jPBInstall);

        jSeparator5.setBackground(getBackground());
        jSeparator5.setForeground(getForeground());
        jSeparator5.setMaximumSize(new java.awt.Dimension(32767, 5));
        jSeparator5.setMinimumSize(new java.awt.Dimension(0, 5));
        jSeparator5.setPreferredSize(new java.awt.Dimension(50, 5));
        jPanel6.add(jSeparator5);

        jPBTotal.setBackground(getBackground());
        jPBTotal.setForeground(getForeground());
        jPBTotal.setToolTipText("Prubeh stahovani aktualniho souboru.");
        jPBTotal.setBorder(null);
        jPBTotal.setStringPainted(true);
        jPanel6.add(jPBTotal);

        jPControls.add(jPanel6);

        jSeparator4.setBackground(getBackground());
        jSeparator4.setForeground(getForeground());
        jSeparator4.setMaximumSize(new java.awt.Dimension(32767, 5));
        jSeparator4.setMinimumSize(new java.awt.Dimension(0, 5));
        jSeparator4.setPreferredSize(new java.awt.Dimension(50, 5));
        jPControls.add(jSeparator4);

        jTPMain.addTab("tab2", jPControls);

        jSPPatchList.setBackground(getBackground());
        jSPPatchList.setForeground(getForeground());
        jSPPatchList.setMinimumSize(new java.awt.Dimension(722, 452));
        jSPPatchList.setPreferredSize(new java.awt.Dimension(722, 452));
        jPPatchList.setLayout(new java.awt.GridLayout(1, 0));

        jPPatchList.setBackground(getBackground());
        jPPatchList.setForeground(getForeground());
        jSPPatchList.setViewportView(jPPatchList);

        jTPMain.addTab("tab3", jSPPatchList);

        jPSettings.setBackground(getBackground());
        jPSettings.setForeground(getForeground());
        jPSettings.setMinimumSize(new java.awt.Dimension(722, 452));
        jLConfUltimaOnlinePath.setBackground(getBackground());
        jLConfUltimaOnlinePath.setForeground(getForeground());
        jLConfUltimaOnlinePath.setLabelFor(jTConfUltimaOnlinePath);
        jLConfUltimaOnlinePath.setText("Adresari Ultimy");

        jTConfUltimaOnlinePath.setBackground(getBackground());
        jTConfUltimaOnlinePath.setColumns(30);
        jTConfUltimaOnlinePath.setForeground(getForeground());
        jTConfUltimaOnlinePath.setMinimumSize(new java.awt.Dimension(20, 19));

        jBConfBrowseUltimaOnlinePath.setBackground(getBackground());
        jBConfBrowseUltimaOnlinePath.setForeground(getForeground());
        jBConfBrowseUltimaOnlinePath.setText("Prochazet");
        jBConfBrowseUltimaOnlinePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfBrowseUltimaOnlinePathActionPerformed(evt);
            }
        });

        jLConfTempPath.setBackground(getBackground());
        jLConfTempPath.setForeground(getForeground());
        jLConfTempPath.setLabelFor(jTConfTempPath);
        jLConfTempPath.setText("Uloziste souboru");

        jTConfTempPath.setBackground(getBackground());
        jTConfTempPath.setColumns(30);
        jTConfTempPath.setForeground(getForeground());
        jTConfTempPath.setMinimumSize(new java.awt.Dimension(200, 19));

        jBConfBrowseTempPath.setBackground(getBackground());
        jBConfBrowseTempPath.setForeground(getForeground());
        jBConfBrowseTempPath.setText("Prochazet");
        jBConfBrowseTempPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfBrowseTempPathActionPerformed(evt);
            }
        });

        jLConfRunCommand.setBackground(getBackground());
        jLConfRunCommand.setForeground(getForeground());
        jLConfRunCommand.setLabelFor(jTConfRunCommand);
        jLConfRunCommand.setText("Spustit program");

        jTConfRunCommand.setBackground(getBackground());
        jTConfRunCommand.setColumns(30);
        jTConfRunCommand.setForeground(getForeground());
        jTConfRunCommand.setMinimumSize(new java.awt.Dimension(20, 19));

        jBConfBrowseRunCommand.setBackground(getBackground());
        jBConfBrowseRunCommand.setForeground(getForeground());
        jBConfBrowseRunCommand.setText("Prochazet");
        jBConfBrowseRunCommand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfBrowseRunCommandActionPerformed(evt);
            }
        });

        jLConfUnRARCommand.setBackground(getBackground());
        jLConfUnRARCommand.setForeground(getForeground());
        jLConfUnRARCommand.setLabelFor(jTConfUnRARCommand);
        jLConfUnRARCommand.setText("Rozbalovaci prikaz");

        jTConfUnRARCommand.setBackground(getBackground());
        jTConfUnRARCommand.setColumns(30);
        jTConfUnRARCommand.setForeground(getForeground());
        jTConfUnRARCommand.setMinimumSize(new java.awt.Dimension(20, 19));

        jBConfBrowseUnRARCommand.setBackground(getBackground());
        jBConfBrowseUnRARCommand.setForeground(getForeground());
        jBConfBrowseUnRARCommand.setText("Prochazet");
        jBConfBrowseUnRARCommand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfBrowseUnRARCommandActionPerformed(evt);
            }
        });

        jChParallelProcess.setBackground(getBackground());
        jChParallelProcess.setForeground(getForeground());
        jChParallelProcess.setSelected(true);
        jChParallelProcess.setText("Stahovat a zaroven instalovat patche zaroven");
        jChParallelProcess.setToolTipText("Pokud zaskrtnete tohle policko, budou se jednotlive patche instalovat zaroven pri stahovani ostatnich souboru. To muze zpusobit na slabsich pocitacich vyrazne spomaleni.");
        jChParallelProcess.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jChParallelProcess.setEnabled(false);
        jChParallelProcess.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jChParallelProcess.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jChParallelProcessStateChanged(evt);
            }
        });

        jBConfLoad.setBackground(getBackground());
        jBConfLoad.setForeground(getForeground());
        jBConfLoad.setText("Nacti nastaveni");
        jBConfLoad.setMaximumSize(new java.awt.Dimension(300, 25));
        jBConfLoad.setMinimumSize(new java.awt.Dimension(100, 25));
        jBConfLoad.setPreferredSize(new java.awt.Dimension(130, 25));
        jBConfLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfLoadActionPerformed(evt);
            }
        });

        jBConfSave.setBackground(getBackground());
        jBConfSave.setForeground(getForeground());
        jBConfSave.setText("Uloz nastaveni");
        jBConfSave.setMaximumSize(new java.awt.Dimension(300, 25));
        jBConfSave.setMinimumSize(new java.awt.Dimension(100, 25));
        jBConfSave.setPreferredSize(new java.awt.Dimension(130, 25));
        jBConfSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBConfSaveActionPerformed(evt);
            }
        });

        jSeparator9.setBackground(getBackground());
        jSeparator9.setForeground(getBackground());
        jSeparator9.setEnabled(false);
        jSeparator9.setMinimumSize(new java.awt.Dimension(10, 30));

        jLConfNewsURL.setBackground(getBackground());
        jLConfNewsURL.setForeground(getForeground());
        jLConfNewsURL.setLabelFor(jTConfRunCommand);
        jLConfNewsURL.setText("Adresa novinek");

        jTConfNewsURL.setBackground(getBackground());
        jTConfNewsURL.setColumns(30);
        jTConfNewsURL.setForeground(getForeground());
        jTConfNewsURL.setMinimumSize(new java.awt.Dimension(20, 19));

        jChDebug.setBackground(getBackground());
        jChDebug.setForeground(getForeground());
        jChDebug.setText("Zobrazovat ladici informace (debug rezim logovani)");
        jChDebug.setToolTipText("Po zaskrtnuti bude patcher zobrazovat detailni informace o sve cinnosti. Pokud chcete nahlasit chybu, zapnete tuto moznost a spolu s popisem chyby zaslete i vypis v okne Logu.");
        jChDebug.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jChDebug.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jChDebug.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jChDebugStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPSettingsLayout = new org.jdesktop.layout.GroupLayout(jPSettings);
        jPSettings.setLayout(jPSettingsLayout);
        jPSettingsLayout.setHorizontalGroup(
            jPSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPSettingsLayout.createSequentialGroup()
                        .add(jBConfLoad, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 185, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSeparator9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jBConfSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 160, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPSettingsLayout.createSequentialGroup()
                        .add(jPSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jLConfNewsURL, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLConfRunCommand, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLConfUltimaOnlinePath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLConfTempPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLConfUnRARCommand))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPSettingsLayout.createSequentialGroup()
                                .add(jTConfTempPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jBConfBrowseTempPath))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPSettingsLayout.createSequentialGroup()
                                .add(jTConfUnRARCommand, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jBConfBrowseUnRARCommand))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPSettingsLayout.createSequentialGroup()
                                .add(jTConfRunCommand, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jBConfBrowseRunCommand))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPSettingsLayout.createSequentialGroup()
                                .add(jTConfUltimaOnlinePath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jBConfBrowseUltimaOnlinePath))
                            .add(jTConfNewsURL, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)))
                    .add(jChParallelProcess)
                    .add(jChDebug))
                .addContainerGap())
        );
        jPSettingsLayout.setVerticalGroup(
            jPSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jBConfBrowseUltimaOnlinePath)
                    .add(jLConfUltimaOnlinePath)
                    .add(jTConfUltimaOnlinePath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jBConfBrowseTempPath)
                    .add(jLConfTempPath)
                    .add(jTConfTempPath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jBConfBrowseUnRARCommand)
                    .add(jLConfUnRARCommand)
                    .add(jTConfUnRARCommand, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jBConfBrowseRunCommand)
                    .add(jLConfRunCommand)
                    .add(jTConfRunCommand, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLConfNewsURL)
                    .add(jTConfNewsURL, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(26, 26, 26)
                .add(jChParallelProcess)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jChDebug)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 198, Short.MAX_VALUE)
                .add(jPSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jBConfLoad, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jBConfSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jSeparator9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jTPMain.addTab("tab3", jPSettings);

        getContentPane().add(jTPMain);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void jChDebugStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jChDebugStateChanged
        Settings.setValue("debug_log", (jChDebug.isSelected()?"1":"0") );
    }//GEN-LAST:event_jChDebugStateChanged
    
    private void jBCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCloseActionPerformed
        //dispose();
        System.exit(0);
    }//GEN-LAST:event_jBCloseActionPerformed
    
    private void jChParallelProcessStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jChParallelProcessStateChanged
        Settings.setValue("parallel_process", String.valueOf(jChParallelProcess.isSelected() ));
    }//GEN-LAST:event_jChParallelProcessStateChanged
    
    private void jBCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelActionPerformed
        patchList.cancelDownload();
        updateButtons();
    }//GEN-LAST:event_jBCancelActionPerformed
    
    private void jBConfBrowseUnRARCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfBrowseUnRARCommandActionPerformed
        jTConfRunCommand.setText( openFile("Vyber soubor", jTConfRunCommand.getText(), JFileChooser.FILES_ONLY) );
    }//GEN-LAST:event_jBConfBrowseUnRARCommandActionPerformed
    
    private void jBConfLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfLoadActionPerformed
        loadSettings();
        
    }//GEN-LAST:event_jBConfLoadActionPerformed
    
    private void jBConfSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfSaveActionPerformed
        saveSettings();
        
    }//GEN-LAST:event_jBConfSaveActionPerformed
    
    private void jBConfBrowseRunCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfBrowseRunCommandActionPerformed
        jTConfRunCommand.setText( openFile("Vyber soubor ktery mam spustit po ukonceni", jTConfRunCommand.getText(), JFileChooser.FILES_ONLY) );
    }//GEN-LAST:event_jBConfBrowseRunCommandActionPerformed
    
    private void jBConfBrowseTempPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfBrowseTempPathActionPerformed
        jTConfTempPath.setText( openFile("Vyber adresar kam stahovat soubory", jTConfTempPath.getText(), JFileChooser.DIRECTORIES_ONLY  ));
    }//GEN-LAST:event_jBConfBrowseTempPathActionPerformed
    
    private void jBConfBrowseUltimaOnlinePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBConfBrowseUltimaOnlinePathActionPerformed
        jTConfUltimaOnlinePath.setText( openFile("Vyber adresar s Ultimou", jTConfUltimaOnlinePath.getText(), JFileChooser.DIRECTORIES_ONLY ));
    }//GEN-LAST:event_jBConfBrowseUltimaOnlinePathActionPerformed
    
    private void jBDownlaodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDownlaodActionPerformed
        jBCancel.setEnabled(true);
        jBClose.setEnabled(false);
        jBDownlaod.setEnabled(false);
        patchList.download();
    }//GEN-LAST:event_jBDownlaodActionPerformed
    
    private void jBRefreshPatchListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBRefreshPatchListActionPerformed
        
    }//GEN-LAST:event_jBRefreshPatchListActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBCancel;
    private javax.swing.JButton jBClose;
    private javax.swing.JButton jBConfBrowseRunCommand;
    private javax.swing.JButton jBConfBrowseTempPath;
    private javax.swing.JButton jBConfBrowseUltimaOnlinePath;
    private javax.swing.JButton jBConfBrowseUnRARCommand;
    private javax.swing.JButton jBConfLoad;
    private javax.swing.JButton jBConfSave;
    private javax.swing.JButton jBDownlaod;
    private javax.swing.JButton jBRefreshPatchList;
    private javax.swing.JCheckBox jChDebug;
    private javax.swing.JCheckBox jChParallelProcess;
    private javax.swing.JEditorPane jEPBrowser;
    private javax.swing.JLabel jLConfNewsURL;
    private javax.swing.JLabel jLConfRunCommand;
    private javax.swing.JLabel jLConfTempPath;
    private javax.swing.JLabel jLConfUltimaOnlinePath;
    private javax.swing.JLabel jLConfUnRARCommand;
    private javax.swing.JLabel jLDownload;
    private javax.swing.JLabel jLInstall;
    private javax.swing.JProgressBar jPBDownloadSingle;
    private javax.swing.JProgressBar jPBDownloadTotal;
    private javax.swing.JProgressBar jPBInstall;
    private javax.swing.JProgressBar jPBTotal;
    private javax.swing.JPanel jPControls;
    private javax.swing.JPanel jPPatchList;
    private javax.swing.JPanel jPSettings;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jSPBrowserScroll;
    private javax.swing.JScrollPane jSPPatchList;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTextField jTConfNewsURL;
    private javax.swing.JTextField jTConfRunCommand;
    private javax.swing.JTextField jTConfTempPath;
    private javax.swing.JTextField jTConfUltimaOnlinePath;
    private javax.swing.JTextField jTConfUnRARCommand;
    private javax.swing.JTextArea jTLog;
    private javax.swing.JTabbedPane jTPMain;
    // End of variables declaration//GEN-END:variables
    
}
