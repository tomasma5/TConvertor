/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.toms_cz.view;

import com.toms_cz.data.RowData;
import com.toms_cz.business.Template;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Tom
 */
public class WelcomeScreen extends JFrame {
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItem;
    private JFrame frame;
    private JTextArea loadedFileText;
    private JPanel panel;
    private JFileChooser fileChoose;
    private File fileChoosen;
    private JScrollPane scrollPanel;
    private Template template;
    private Dialogs dialogs;
    private final String welcomeText = "Vitejte, zde budou zobrazena importovana data.\r\n"
            + "Začněte prosím volbou SOUBOR v levém horním menu.";
    public WelcomeScreen() {
        createMenu();
        frame = new JFrame("Converter");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        createMenu();
        loadedFileText = new JTextArea(welcomeText);
        loadedFileText.setBackground(Color.WHITE);
        loadedFileText.setEditable(false);
        loadedFileText.setLocation(0, 0);
        loadedFileText.setWrapStyleWord(true);
        loadedFileText.setLineWrap(true);
        scrollPanel = new JScrollPane(loadedFileText);
        frame.setSize(800, 600);
        scrollPanel.setSize(frame.getSize().width - 10, frame.getSize().height - 50);
        frame.setJMenuBar(menuBar);
        panel = new JPanel();
        panel.add(scrollPanel);
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        panel.setLayout(null);
        frame.setResizable(false);
        dialogs = new Dialogs(frame);
        frame.setVisible(true);
    }

    private void createMenu() {
        menuBar = new JMenuBar();
        menu = new JMenu("Soubor");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription("Popis");
        menuBar.add(menu);
        menuItem = new JMenuItem("Nacti soubor");
        menuItem.getAccessibleContext().setAccessibleDescription("Komponenta k nacteni souboru");
        menuItem.addActionListener(chooseFile);
        menu.add(menuItem);
        menuItem = new JMenuItem("Exportuj");
        menuItem.getAccessibleContext().setAccessibleDescription("Komponenta k nacteni souboru");
        menuItem.addActionListener(parseFile);
        menu.add(menuItem);
        menuItem = new JMenuItem("Ukončit");
        menuItem.getAccessibleContext().setAccessibleDescription("Komponenta k nacteni souboru");
        menuItem.addActionListener(close);
        menu.add(menuItem);
    }
    /**
     * Action listener, which serve load file menu button
     */
    private ActionListener chooseFile = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            template = dialogs.chooseTemplate();
            if (template == null) {
                dialogs.chooseTemplateErr();
                loadedFileText.setText(welcomeText);
            } else {
                fileChoose = new JFileChooser("c://prijemky");
                fileChoose.setFileSelectionMode(JFileChooser.FILES_ONLY);
                FileFilter filter = new FileNameExtensionFilter(template.getFileDescription(), template.getFileType());
                fileChoose.setFileFilter(filter);
                fileChoose.showOpenDialog(panel);
                fileChoosen = fileChoose.getSelectedFile();
                if (fileChoosen != null) {
                    String loadFileContent = template.readFile(fileChoosen);
                    if ((loadFileContent==null)||(loadFileContent.length() < 4)) {
                        dialogs.badFileSelected();
                    } else {
                        loadedFileText.setText(loadFileContent);
                        frame.setTitle("Converter " + fileChoosen.getPath());
                    }
                }
            }
        }
    };
    private ActionListener close = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            frame.dispose();
        }
    };
    /**
     * Action listener, which serve convert button
     */
    private ActionListener parseFile = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {    
            
            if (template == null) {
                dialogs.noTemplateChoose();
                return;
            }
            if (fileChoosen == null) {
                dialogs.noFileSelected();
                return;
            }
            
            ArrayList<RowData> dataToExport = null;
            try {
                dataToExport = template.parsedData(loadedFileText.getText());
                if (dataToExport == null) {
                    dataToExport = template.parsedData(fileChoosen);
                }
            } catch (Exception except) {
                dialogs.parsingError(fileChoosen.getAbsolutePath());
                return;
            }
            if (dataToExport != null) {
                displayData(dataToExport);
            } else {
                dialogs.parsingError(fileChoosen.getAbsolutePath());
                return;
            }
            int result = template.exportData(dataToExport);
            if (result == 0) {
                dialogs.sucessfullyExported("C://prijemky//import.dbf");
            }
        }
    };

    private void displayData(ArrayList<RowData> loadedData) {
        Iterator loadIt = loadedData.iterator();
        String dataString = "Kód \t počet \t celková cena \t daň \r\n";
        while (loadIt.hasNext()) {
            RowData actualRow = (RowData) loadIt.next();
            dataString = dataString.concat(actualRow.getItemCode() + "\t" + actualRow.getNumberOfItems()
                    + "\t" + actualRow.getPriceWithoutTaxes() + "\t" + actualRow.getTaxRate() + "\r\n");
        }
        loadedFileText.setText(dataString);
    }
}
