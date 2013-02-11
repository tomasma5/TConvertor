/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.toms_cz.view;

import com.toms_cz.business.Pht;
import com.toms_cz.business.FischerIm;
import com.toms_cz.business.Template;
import com.toms_cz.business.Proteco;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Tom
 */
public class Dialogs extends JDialog {

    JFrame owner;

    public Dialogs(JFrame owner) {
        this.owner = owner;
    }

    public Template chooseTemplate() {
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Object[] possibilities = {"Fischer","PHT","Proteco"};
        Template template = null;
        String choosenValue = (String) JOptionPane.showInputDialog(owner, "Vyberte prosim dodavatele",
                "Dodavatel", JOptionPane.OK_OPTION, null, possibilities, "Fischer");
        if(choosenValue==null){
            return null;
        }
        switch (choosenValue) {
            case "Fischer":
                template = new FischerIm();
                break;
            case "PHT":
                template=new Pht();
                break;
            case "Proteco":
                template=new Proteco();
                break;
        }
        return template;
    }

    public void noTemplateChoose() {
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JOptionPane.showMessageDialog(owner, "Nebyl zvolen dodavatel, klikněte prosím"
                + "na Soubor -> Nacti soubor. Pote stiskněte tlacitko export.", "Chyba při volbě dodavatele", JOptionPane.ERROR_MESSAGE);
    }

    public void sucessfullyExported(String path) {
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JOptionPane.showMessageDialog(owner, "Export položek z faktury byl úspěšně dokončen."
                + "Soubor byl exportovan do adresare: " + path,
                "Export byl dokončen.", JOptionPane.INFORMATION_MESSAGE);
    }

    public void parsingError(String filePath) {
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JOptionPane.showMessageDialog(owner, "Při zpracování vstupního souboru došlo k "
                + "chybě, prosim načtěte soubor znovu a postup opakujte. Soubor, při kterém se vyskytla chyba parsování: "
                + filePath, 
                "Chyba při zpracovani vstupniho souboru", JOptionPane.ERROR_MESSAGE);
    }
    public void chooseTemplateErr() {
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JOptionPane.showMessageDialog(owner, "Nebyl vybrán žádný dodavatel. Načítání souboru bylo přerušeno.", 
                "Chyba při výběru šablony vstupniho souboru", JOptionPane.ERROR_MESSAGE);
    }
       public void noFileSelected() {
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JOptionPane.showMessageDialog(owner, "Nebyl vybrán žádný soubor. Export byl přerušen.", 
                "Chyba při výběru vstupniho souboru", JOptionPane.ERROR_MESSAGE);
    }
         public void badFileSelected() {
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JOptionPane.showMessageDialog(owner, "Byl vybrán nekompatibilní vstupní soubor. Načítání bylo přerušeno", 
                "Chyba při výběru vstupniho souboru", JOptionPane.ERROR_MESSAGE);
    }
}
