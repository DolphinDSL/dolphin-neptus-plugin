///*
// * Copyright (c) 2004-2017 Universidade do Porto - Faculdade de Engenharia
// * Laboratório de Sistemas e Tecnologia Subaquática (LSTS)
// * All rights reserved.
// * Rua Dr. Roberto Frias s/n, sala I203, 4200-465 Porto, Portugal
// *
// * This file is part of Neptus, Command and Control Framework.
// *
// * Commercial Licence Usage
// * Licencees holding valid commercial Neptus licences may use this file
// * in accordance with the commercial licence agreement provided with the
// * Software or, alternatively, in accordance with the terms contained in a
// * written agreement between you and Universidade do Porto. For licensing
// * terms, conditions, and further information contact lsts@fe.up.pt.
// *
// * Modified European Union Public Licence - EUPL v.1.1 Usage
// * Alternatively, this file may be used under the terms of the Modified EUPL,
// * Version 1.1 only (the "Licence"), appearing in the file LICENSE.md
// * included in the packaging of this file. You may not use this work
// * except in compliance with the Licence. Unless required by applicable
// * law or agreed to in writing, software distributed under the Licence is
// * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
// * ANY KIND, either express or implied. See the Licence for the specific
// * language governing permissions and limitations at
// * https://github.com/LSTS/neptus/blob/develop/LICENSE.md
// * and http://ec.europa.eu/idabc/eupl.html.
// *
// * For more information please see <http://lsts.fe.up.pt/neptus>.
// *
// * Author: edrdo
// * May 14, 2017
// */

package pt.lsts.neptus.plugins.dolphin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.RUndoManager;


import pt.lsts.neptus.NeptusLog;
import pt.lsts.neptus.console.ConsoleLayout;
import pt.lsts.neptus.console.ConsolePanel;
import pt.lsts.neptus.i18n.I18n;
import pt.lsts.neptus.plugins.NeptusProperty;
import pt.lsts.neptus.plugins.PluginDescription;
import pt.lsts.neptus.plugins.Popup;
import pt.lsts.neptus.util.FileUtil;
import pt.lsts.neptus.util.ImageUtils;


/**
 * Neptus Console for Dolphin language (GUI to edit and run programs)
 */
@PluginDescription(name = "Dolphin Runtime Feature",description = "Task orchestration language for autonomous vehicle networks",icon="pt/lsts/neptus/plugins/dolphin/images/dolphin.png")
@Popup(pos = Popup.POSITION.BOTTOM_RIGHT,accelerator = KeyEvent.VK_D, width=600, height=500)
@SuppressWarnings("serial")
public class DolphinConsolePanel extends ConsolePanel {

    private Border border,fontBorder;
    private JScrollPane outputPanel;
    private JTextArea output;
    private RSyntaxTextArea editor; 
    private File script;
    private JButton select,execButton,stop,saveFile,autoSave,undo,redo;
    private RTextScrollPane scroll;
    private SpinnerModel model = new SpinnerNumberModel(14, 2, 32, 1);     
    private JSpinner spinner = new JSpinner(model);
    private RUndoManager undoManager; 
    
    @NeptusProperty
    File scriptDir = new File("conf/dolphin/");
    
    public DolphinConsolePanel(ConsoleLayout layout) {
        super(layout);
    }

    /**
     * Console configuration: editor, output panel, buttons and font size spinner
     */
    @Override
    public void initSubPanel() {
        removeAll();
        NeptusPlatform.getInstance().associateTo(this);
        
        setLayout(new BorderLayout());
        editor = new RSyntaxTextArea();
        undoManager   = new RUndoManager(editor);
        editor.getDocument().addUndoableEditListener(new UndoableEditListener() {
            
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());                
            }
        });
        
        //Custom syntax highlight
        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
        atmf.putMapping("text/dolphin", "pt.lsts.neptus.plugins.dolphin.HighlightSupport"); //NVLHighlightSupport.SYNTAX_STYLE_GROOVY
        editor.setSyntaxEditingStyle("text/dolphin");
        editor.setCodeFoldingEnabled(true);
        scroll = new RTextScrollPane(editor);

        if (script != null) {
            editor.setText(FileUtil.getFileAsString(script));
            editor.discardAllEdits();
        }

        Action saveAction = new AbstractAction(I18n.text("Save Script as"), ImageUtils.getScaledIcon("pt/lsts/neptus/plugins/dolphin/images/save.png", 16, 16)) {

            @Override
            public void actionPerformed(ActionEvent e) {
                File directory = scriptDir;//new File("conf/dolphin/apdlTests");
                final JFileChooser fc = new JFileChooser(directory);
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setFileFilter( new FileNameExtensionFilter("Dolphin files","dolphin"));
                // Demonstrate "Save" dialog:
                int rVal = fc.showDialog(DolphinConsolePanel.this,"Save file as...");
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    script = fc.getSelectedFile();
                    FileUtil.saveToFile(script.getAbsolutePath(), editor.getText());
                }

            }
        };
        
        Action autosaveAction = new AbstractAction(I18n.text(""), ImageUtils.getScaledIcon("pt/lsts/neptus/plugins/dolphin/images/save.png", 16, 16)) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(script!=null)
                    FileUtil.saveToFile(script.getAbsolutePath(), editor.getText());
                else
                    saveAction.actionPerformed(e);
                

            }
        };

        Action selectAction = new AbstractAction(I18n.text("Script File..."), ImageUtils.getScaledIcon("pt/lsts/neptus/plugins/dolphin/images/filenew.png", 16, 16)) {

            @Override
            public void actionPerformed(ActionEvent e) {
                File directory = scriptDir;//new File("conf/dolphin/rep17");
                final JFileChooser fc = new JFileChooser(directory);
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setFileFilter( new FileNameExtensionFilter("Dolphin files","dolphin"));
                int returnVal = fc.showOpenDialog(DolphinConsolePanel.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    script = fc.getSelectedFile();
                    border = BorderFactory.createTitledBorder("Script "+FileUtil.getFileNameWithoutExtension(script)+" Output");
                    outputPanel.setBorder(border);
                    if(fc.getSelectedFile().exists()){
                        NeptusLog.pub().info("Opening: " + script.getName() + "\n");
                        editor.setText(FileUtil.getFileAsString(script));
                        editor.discardAllEdits();
                    }
                    else {
                        try {
                            if(script.createNewFile()){

                                editor.setText(FileUtil.getFileAsString(script));
                                NeptusLog.pub().info("Creating new script file: " + script.getName() + "\n");                          }
                        }
                        catch(IOException e1){
                            NeptusLog.pub().info("Error creating new script file\n",e1);
                        }

                    }
                }
            }
        };
        
        //Output panel
        output = new JTextArea();
        border = BorderFactory.createTitledBorder("Script Output");
        //output.setBorder(border);
        output.setEditable(false);
        output.setVisible(true);
        output.append("Dolphin Runtime Console");
        outputPanel = new JScrollPane(output);
        outputPanel.setBorder(border);

        // Buttons AbstractActions
        Action execAction = new AbstractAction(I18n.text("Execute"),ImageUtils.getScaledIcon("pt/lsts/neptus/plugins/dolphin/images/forward.png", 16, 16)) {
            @Override
            public void actionPerformed(ActionEvent e) {   
                FileUtil.saveToFile(script.getAbsolutePath(), editor.getText());
                NeptusPlatform.getInstance().run(script);
            }
        };

        Action stopAction = new AbstractAction(I18n.text("Stop"),ImageUtils.getScaledIcon("pt/lsts/neptus/plugins/dolphin/images/stop.png", 16, 16)) {
            @Override
            public void actionPerformed(ActionEvent e) {   
                output.append("Stopping script!\n");
                NeptusLog.pub().warn("Stopping script!\n");
                NeptusPlatform.getInstance().stopExecution();
            }
        };
        
        Action undoAction = new AbstractAction(I18n.text(""),ImageUtils.getScaledIcon("pt/lsts/neptus/plugins/dolphin/images/undo.png", 16, 16)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(undoManager.canUndo())
                    undoManager.undo();
                else
                    NeptusLog.pub().error("Unnable to undo typing at Dolphin editor");
            }
        };
        
        
        Action redoAction = new AbstractAction(I18n.text(""),ImageUtils.getScaledIcon("pt/lsts/neptus/plugins/dolphin/images/redo.png", 16, 16)) {
            @Override
            public void actionPerformed(ActionEvent e) {   
                if(undoManager.canRedo())
                    undoManager.redo();
                else
                    NeptusLog.pub().error("Unnable to redo typing at Dolphin editor");
            }
        };
        
        //Buttons
        execButton = new JButton(execAction);
        select     = new JButton(selectAction);
        stop       = new JButton(stopAction);
        saveFile   = new JButton(saveAction);
        autoSave   = new JButton(autosaveAction);
        undo       = new JButton(undoAction);
        redo       = new JButton(redoAction);

        JButton clear = new JButton(new AbstractAction(I18n.text("Clear Output"),ImageUtils.getScaledIcon("pt/lsts/neptus/plugins/dolphin/images/clear.png", 16, 16)) {

            @Override
            public void actionPerformed(ActionEvent e) {
                output.setText("");

            }
        });

        //Console layout
        JPanel top = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel();
        
        
        //Font size spinner
        ChangeListener listener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                    editor.setFont(new Font(Font.MONOSPACED, 0, (int) spinner.getValue()));
                
            }
          };        
        spinner.addChangeListener(listener);
        fontBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),"Font");
        ((TitledBorder) fontBorder).setTitlePosition(TitledBorder.BOTTOM);
        spinner.setBorder(fontBorder); 
        spinner.setPreferredSize(new Dimension(65,35));
        
        
        //middle section
        buttons.add(select);
        buttons.add(autoSave);
        buttons.add(saveFile);
        buttons.add(execButton);
        buttons.add(stop);
        buttons.add(spinner);
        buttons.add(undo);
        buttons.add(redo);
        
        //onHover tool tip text
        select.setToolTipText("Select File");
        autoSave.setToolTipText("Save Current File Changes");
        spinner.setToolTipText("Adjust Editor's Font Size");
        saveFile.setToolTipText("Save Current File as...");
        undo.setToolTipText("Ctrl+Z");
        redo.setToolTipText("Ctrl+Y");

        
        top.setPreferredSize(new Dimension(600, 350));
        top.add(buttons,BorderLayout.SOUTH);
        top.add(scroll,BorderLayout.CENTER);


        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setPreferredSize(new Dimension(600, 150));
        bottom.add(clear,BorderLayout.SOUTH);
        bottom.add(outputPanel,BorderLayout.CENTER);

        add(bottom,BorderLayout.SOUTH);
        add(top,BorderLayout.CENTER);

    }

    @Override
    public void cleanSubPanel() {
        NeptusPlatform.getInstance().detach();

    }

   /* @Subscribe
    public void on(ConsoleEventPlanChange changedPlan) {
        NeptusPlatform.getInstance().onPlanChanged(changedPlan);

    }*/

    /**
     * Method to display relevant debug/error/information messages from Dolphin Language
     * printf format string
     * @param fmt String format 
     * @param args arguments to @param fmt
     */
    public void displayMessage(String fmt, Object[] args) {

        if(output!=null){
            output.append(String.format(fmt, args));
            output.append("\n");
            output.setCaretPosition(output.getDocument().getLength());
        }

    }


    /**
     * @param prompt message to be displayed on the popup dialog
     * @return input from user
     */
    public String askForInput(String prompt) {
        String result = JOptionPane.showInputDialog(this, prompt);
 
       return result;
    }

}
