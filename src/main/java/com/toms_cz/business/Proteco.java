/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.toms_cz.business;

import com.toms_cz.data.RowData;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class proteco represent supplier Proteco. Contain a specific approach to 
 * read file and parse this file. This supplier use XML format. 
 * This is reason how is there DOM XML parser.
 * @author Tom
 * 
 */
public class Proteco extends Template {
    /**
     * Constructor of class Pretoco, set file type and 
     * file description which this supplier use and 
     */
    public Proteco() {
        setFileDescription("XML soubory");
        setFileType("xml");
    }
  
    /**
     * This method provide read and show content from specific file. Supplier proteco
     * use XML format of data, therefore the DOM parser is used.
     * @param fileToRead 
     * @return content of input file
     */
    @Override 
    public String readFile(File fileToRead) {
        Dom dom=new Dom();
        String content=dom.domRead(fileToRead);
        return content;
    }
    
    @Override
    public ArrayList<RowData> parsedData(String stringToParse) {
        return null;
    }
    /**
     * This method parse a data from input file using method 
     * parseData(fileToParse) in embeded class Dom.
     * 
     * @return ArrayList of data represented by RowData class.
     */
    @Override
    public ArrayList<RowData> parsedData(File fileToParse) {
        Dom domParser=new Dom();
        ArrayList<RowData> records=domParser.parseData(fileToParse);
        return records;
    }
}

class Dom {
   private static final String ITEM_START="item";
   private static final String CODE="article_number";
   private static final String QUANTITY="quantity";
   private static final String TOTAL_PRICE="total_price";
   private static final String TAX_RATE="20";
 private DocumentBuilderFactory dbf=null;
 DocumentBuilder builder=null; 
    public Dom() {
       dbf = DocumentBuilderFactory.newInstance(); 
       dbf.setValidating(false);     
    }  
    public String domRead(File fileToRead){      
        String fileContent=null;
            try {
           builder = dbf.newDocumentBuilder();
            //parser zpracuje vstupní soubor a vytvoří z něj strom DOM objektů
            Document doc = builder.parse(fileToRead);
            //zpracujeme DOM strom
            fileContent=readXml(doc);
        } catch (Exception e) {
            Logger.getLogger(Proteco.class.getName()).log(Level.SEVERE, null, e);
        }
        return fileContent;
    }
    /**
     * This method parse data from input file using DBF framework and DOM parser to 
     * read property XML file
     * @param fileToParse 
     * @return ArrayList of parsed data represented by RowData class.
     */
    public ArrayList<RowData> parseData(File fileToParse){
        ArrayList<RowData> records=null;
             try {
           builder = dbf.newDocumentBuilder();
            //parser zpracuje vstupní soubor a vytvoří z něj strom DOM objektů
            Document doc = builder.parse(fileToParse);
            //zpracujeme DOM strom
            records=exportXML(doc);
        } catch (Exception e) {
          Logger.getLogger(Proteco.class.getName()).log(Level.SEVERE, null, e);
        }
             return records;
    }
    /**
     *This method read XML document and return contant of file withnout tags.
     * @param doc 
     * @return content of input Document.
     */
    private String  readXml(Document doc){
       Node rootNode=doc.getDocumentElement();
       return rootNode.getTextContent();
    }
    /**
     * This method parse data from input document to Arraylist of RowData class, 
     * which represent a single record in file. 
     * 
     * @param doc 
     * @return ArrayList of RowData class.
     */
     private ArrayList<RowData> exportXML(Document doc) {
        NodeList itemList = doc.getElementsByTagName(ITEM_START);
        ArrayList<RowData> records=new ArrayList<>();
        for(int i=0;i<itemList.getLength();i++){     
       Node currItem=itemList.item(i);
       NodeList itemDesc=currItem.getChildNodes();
       RowData row=new RowData();
       for(int j=0;j<itemDesc.getLength();j++){
           Node currNode=itemDesc.item(j);
           if(currNode.getNodeName().equals(CODE)){
                row.setItemCode(currNode.getTextContent());
           }
           else  if(currNode.getNodeName().equals(QUANTITY)){
               String numberOfItems=currNode.getTextContent();
               row.setNumberOfItems(Double.valueOf(numberOfItems));
           }
              else  if(currNode.getNodeName().equals(TOTAL_PRICE)){
                  String totalPrice=currNode.getTextContent();
                row.setPriceWithoutTaxes(Double.valueOf(totalPrice));
           }                
       }
       row.setTaxRate(Double.valueOf(TAX_RATE));
       records.add(row);
       } 
        if(records.size()<1){
            return null;
        }
        return records;
     }
}
