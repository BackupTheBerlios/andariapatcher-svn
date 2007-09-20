package AndariaPatcher;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

/*******************************************************************************
 * Panel containing PatchItem element details.
 * @author  Martin Polehla (polous@katka.biz)
 * @version 0.1
 ******************************************************************************/

public class PatchPanel extends JPanel {
    Log log;
    private PatchItem patchItem;
    
    /***************************************************************************
     * Create new panel PatchPanel and inicialize object
     * @param pi PatchItem linked to this instance of object
     **************************************************************************/
    public PatchPanel(PatchItem pi) {
        log = new Log(this);
        patchItem = pi;
        initComponents();
        addMouseListener(new Clicked());
    }
    /***************************************************************************
     * Set jChRequired checkbox state.
     * @param b new state of checkbox
     **************************************************************************/
    public void setRequired(boolean required) {
        jChRequired.setSelected(required);
        setBackground( ( required ? new Color( 255,233,233 ) : new Color(255,255,255) ) );
    }
    /***************************************************************************
     * Set jChWanted checkbox state.
     * @param b new state of checkbox
     **************************************************************************/
    public void setWanted(boolean wanted) { jChWanted.setSelected( wanted ); }
    
    /***************************************************************************
     * Generate toolTip text for jPanel tooltip property.
     * @return a tooltip string value
     **************************************************************************/
    public String getToolTip()
    {
        String res = patchItem.getDescription();
        res = res + "\n necum pico  !";
        return res;
    }
    
    /***************************************************************************
     * Implements item selection process.
     * When any elements of form sends onMouseClicked event, Clicked object
     * should switch "wanted" state of it's patchItem and switch state of
     * jChWanted checkbox.
     **************************************************************************/
    class Clicked implements MouseListener {
        Clicked() {
            jChRequired.addMouseListener(this);
            jChWanted.addMouseListener(this);
            jLDate.addMouseListener(this);
            jLHash.addMouseListener(this);
            jLSize.addMouseListener(this);
            jLVersion.addMouseListener(this);
            jSeparator1.addMouseListener(this);
            jSeparator2.addMouseListener(this);
            jSeparator3.addMouseListener(this);
            jTDate.addMouseListener(this);
            jTFileName.addMouseListener(this);
            jTHash.addMouseListener(this);
            jTSize.addMouseListener(this);
            jTVersion.addMouseListener(this);
        }
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        
        public void mouseClicked(MouseEvent e) {
            log.addDebug("clicked !!");
            patchItem.setWanted(!patchItem.isWanted());
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jChWanted = new javax.swing.JCheckBox();
        jLVersion = new javax.swing.JLabel();
        jTFileName = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jTVersion = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLHash = new javax.swing.JLabel();
        jTHash = new javax.swing.JTextField();
        jLDate = new javax.swing.JLabel();
        jTDate = new javax.swing.JTextField();
        jChRequired = new javax.swing.JCheckBox();
        jLSize = new javax.swing.JLabel();
        jTSize = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();

        setBackground(java.awt.Color.white);
        setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(163, 125, 86), 1, true), patchItem.getName(), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 14), new java.awt.Color(163, 125, 86)));
        setForeground(new java.awt.Color(163, 125, 86));
        setToolTipText(getToolTip());
        setFocusable(false);
        setMinimumSize(new java.awt.Dimension(730, 75));
        setPreferredSize(new java.awt.Dimension(730, 78));
        jChWanted.setBackground(getBackground());
        jChWanted.setForeground(getForeground());
        jChWanted.setSelected(patchItem.isWanted());
        jChWanted.setText("Instalovat soubor    ");
        jChWanted.setToolTipText(getToolTipText());
        jChWanted.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jChWanted.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jChWanted.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jChWantedStateChanged(evt);
            }
        });

        jLVersion.setBackground(getBackground());
        jLVersion.setForeground(getForeground());
        jLVersion.setLabelFor(jTVersion);
        jLVersion.setText("Verze: ");
        jLVersion.setToolTipText(getToolTipText());

        jTFileName.setBackground(getBackground());
        jTFileName.setEditable(false);
        jTFileName.setForeground(getForeground());
        jTFileName.setText(patchItem.getFileName());
        jTFileName.setToolTipText(getToolTipText());
        jTFileName.setBorder(null);

        jSeparator1.setBackground(getBackground());
        jSeparator1.setForeground(getBackground());
        jSeparator1.setToolTipText(getToolTipText());
        jSeparator1.setMinimumSize(new java.awt.Dimension(10, 10));
        jSeparator1.setPreferredSize(new java.awt.Dimension(10, 10));

        jTVersion.setBackground(getBackground());
        jTVersion.setEditable(false);
        jTVersion.setForeground(getForeground());
        jTVersion.setText(patchItem.getVersions());
        jTVersion.setToolTipText(getToolTipText());
        jTVersion.setBorder(null);
        jTVersion.setMinimumSize(new java.awt.Dimension(100, 15));
        jTVersion.setPreferredSize(new java.awt.Dimension(100, 15));

        jSeparator2.setBackground(getBackground());
        jSeparator2.setForeground(getBackground());
        jSeparator2.setToolTipText(getToolTipText());
        jSeparator2.setMinimumSize(new java.awt.Dimension(10, 10));
        jSeparator2.setPreferredSize(new java.awt.Dimension(10, 10));

        jLHash.setBackground(getBackground());
        jLHash.setForeground(getForeground());
        jLHash.setText("MD5: ");
        jLHash.setToolTipText(getToolTipText());

        jTHash.setBackground(getBackground());
        jTHash.setEditable(false);
        jTHash.setForeground(getForeground());
        jTHash.setText(patchItem.getHash());
        jTHash.setToolTipText(getToolTipText());
        jTHash.setBorder(null);
        jTHash.setMinimumSize(new java.awt.Dimension(410, 19));
        jTHash.setPreferredSize(new java.awt.Dimension(300, 15));

        jLDate.setBackground(getBackground());
        jLDate.setForeground(getForeground());
        jLDate.setLabelFor(jTDate);
        jLDate.setText("ze dne: ");
        jLDate.setToolTipText(getToolTipText());

        jTDate.setBackground(getBackground());
        jTDate.setEditable(false);
        jTDate.setForeground(getForeground());
        jTDate.setText(patchItem.getDates());
        jTDate.setToolTipText(getToolTipText());
        jTDate.setBorder(null);
        jTDate.setMinimumSize(new java.awt.Dimension(250, 15));
        jTDate.setPreferredSize(new java.awt.Dimension(250, 15));

        jChRequired.setBackground(getBackground());
        jChRequired.setForeground(getForeground());
        jChRequired.setSelected(patchItem.isRequired());
        jChRequired.setText("Povinny                ");
        jChRequired.setToolTipText(getToolTipText());
        jChRequired.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jChRequired.setEnabled(false);
        jChRequired.setFocusable(false);
        jChRequired.setMargin(new java.awt.Insets(0, 5, 0, 0));
        jChRequired.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jChRequiredActionPerformed(evt);
            }
        });

        jLSize.setBackground(getBackground());
        jLSize.setForeground(getForeground());
        jLSize.setText("Velikost: ");
        jLSize.setToolTipText(getToolTipText());

        jTSize.setBackground(getBackground());
        jTSize.setEditable(false);
        jTSize.setForeground(getForeground());
        jTSize.setText(Long.toString(patchItem.getSize()));
        jTSize.setToolTipText(getToolTipText());
        jTSize.setBorder(null);

        jSeparator3.setBackground(getBackground());
        jSeparator3.setForeground(getBackground());
        jSeparator3.setToolTipText(getToolTipText());
        jSeparator3.setMinimumSize(new java.awt.Dimension(10, 10));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jChWanted)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jTFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLVersion)
                                .add(4, 4, 4))
                            .add(layout.createSequentialGroup()
                                .add(jLSize)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jTSize, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(3, 3, 3)))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jTVersion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLDate)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jTDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(layout.createSequentialGroup()
                                .add(12, 12, 12)
                                .add(jSeparator3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLHash)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jTHash, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(6, 6, 6))))
                    .add(jChRequired))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jChWanted)
                        .add(jTFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLVersion)
                        .add(jTVersion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLDate)
                        .add(jTDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jChRequired)
                        .add(jLSize)
                        .add(jTSize, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLHash)
                    .add(jTHash, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void jChRequiredActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jChRequiredActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_jChRequiredActionPerformed
    
    private void jChWantedStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jChWantedStateChanged
        
    }//GEN-LAST:event_jChWantedStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jChRequired;
    private javax.swing.JCheckBox jChWanted;
    private javax.swing.JLabel jLDate;
    private javax.swing.JLabel jLHash;
    private javax.swing.JLabel jLSize;
    private javax.swing.JLabel jLVersion;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField jTDate;
    private javax.swing.JTextField jTFileName;
    private javax.swing.JTextField jTHash;
    private javax.swing.JTextField jTSize;
    private javax.swing.JTextField jTVersion;
    // End of variables declaration//GEN-END:variables
}
