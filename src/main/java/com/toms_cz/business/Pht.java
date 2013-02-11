/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.toms_cz.business;

import com.toms_cz.data.RowData;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;


/**
 * Class Pht represent supplier PHT a.s. Contain a specific approach to 
 * read file and parse this file. This supplier use HTML format. With uncompatible
 * XML part, this is reason why is used string parsing based XML tags.
 * @author Tom
 * 
 */
public class Pht extends Template {
/**
     * Constructor of class PHT, set file type and 
     * file description which this supplier use and 
     */
    public Pht() {
        setFileDescription("HTML soubory");
        setFileType("html");
    }
    /**
     * This method provide read and show content from specific file. Supplier proteco
     * use XML format of data, therefore the DOM parser is used.
     * @param fileToRead 
     * @return content of input file
     */
    @Override
    public String readFile(File fileToRead) {
        InputStreamReader in = null;
        String fileString = null;
        try {
            in = new InputStreamReader(new FileInputStream(fileToRead), Charset.forName("windows-1250"));
            HtmlParser parser = new HtmlParser();
            parser.parse(in);
            fileString = parser.getText();
            Pattern pattern = Pattern.compile("Fakturujeme");
            Matcher matcher = pattern.matcher(fileString);
            int pos = 0;
            while (matcher.find()) {
                pos = matcher.start();
            }
            fileString = fileString.substring(0, pos);
        } catch (IOException ex) {
            Logger.getLogger(Pht.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(Pht.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return fileString;

    }

    @Override
    public ArrayList<RowData> parsedData(String stringToParse) {
        return null;
    }
       /**
     * This method parse a data from input file using method 
     * sbuf(fileToParse). Then is created ArrayList of RowData which represent a 
     * single record. Because incompatible HTML format there is used string parsing
     * via tags.
     * 
     * 
     * @return ArrayList of data represented by RowData class.
     */
    @Override
    public ArrayList<RowData> parsedData(File fileToParse) {
        StringBuffer sbuf;
        sbuf = parseXMLPart(fileToParse);
        String temp = sbuf.toString();
        String[] a = temp.split("<invoiceItem ");

        String[] k = a[a.length - 1].split("</invoiceItem>");
        a[a.length - 1] = k[0];
        ArrayList<RowData> records = new ArrayList<>();
        for (int j = 1; j < a.length; j++) {
            try {
                String code = splitByReg(a[j], "code=");
                String quantity = splitByReg(a[j], "quantity=");
                String totalPrice = splitByReg(a[j], "priceSum=");
                String taxes = "20";
                RowData row = new RowData();
                row.setItemCode(code);
                row.setNumberOfItems(Double.valueOf(quantity));
                row.setTaxRate(Double.valueOf(taxes));
                row.setPriceWithoutTaxes(Double.valueOf(totalPrice));
                records.add(row);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }
        return records;
    }
    /**
     * This method return buffered string from input file which containts a XML
     * part.
     * @param fileToParse 
     * @return sbuf of XML part
     */
    private StringBuffer parseXMLPart(File fileToParse) {
        StringBuffer sBuf = new StringBuffer();
        try {
            InputStreamReader in;
            in = new InputStreamReader(new FileInputStream(fileToParse), Charset.forName("windows-1250"));
            BufferedReader bufferedReader = new BufferedReader(in);
            String line = null;
            Boolean isXMLPart = false;
            Pattern pattern = Pattern.compile("<XML>");
            while ((line = bufferedReader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if ((isXMLPart == false) && (matcher.find())) {
                    isXMLPart = true;
                }
                if (isXMLPart) {
                    sBuf.append(line);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Pht.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sBuf;
    }
    /**
     * This method split input string source by string regex and return string 
     * result.
     * @param regex 
     * @param source 
     * @return line by regex
     */
    private String splitByReg(String source, String regex) throws IndexOutOfBoundsException {
        String[] currColumn = source.split(regex);
        currColumn = currColumn[1].split(" ");
        String result = currColumn[0];
        result = result.substring(1, result.length() - 1);
        return result;
    }
}
class HtmlParser extends HTMLEditorKit.ParserCallback {

    private StringBuffer s;

    public HtmlParser() {
        s = new StringBuffer();
    }

    public void parse(Reader in) throws IOException {
        ParserDelegator delegator = new ParserDelegator();
        // the third parameter is TRUE to ignore charset directive
        delegator.parse(in, this, Boolean.TRUE);

    }

    @Override
    public void handleText(char[] text, int pos) {
        s.append(text);
    }

    public String getText() {
        return s.toString();
    }
}
