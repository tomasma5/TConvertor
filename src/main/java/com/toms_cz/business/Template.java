/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.toms_cz.business;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;
import com.toms_cz.data.RowData;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class Template is base for all bussiness template partners. Each
 * bussiness partner has a different approach to implements operation, but
 * readFile operation is the same.
 *
 * @author Tom
 *
 */
public abstract class Template {

    private final String IDENTIFIKATE_FIELD = "identifier";
    private final String NUMBER_FIELD = "number";
    private final String PRICE_WITH_TAXES_FIELD = "priceWT";
    private final String TAX_RATE_FIELD = "tax";
    private final String OUTPUT_FILE = "C://prijemky//import.dbf";

    public String getFileDescription() {
        return fileDescription;
    }

    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    private String fileType;
    private String fileDescription;

    /**
     * This method provide read and display data.
     *
     * @param fileToRead
     * @return file content
     */
    public abstract String readFile(File fileToRead);

    /**
     * This method parse data from string.
     *
     * @param stringToParse
     * @return Arraylist whith parsed data.
     */
    public abstract ArrayList<RowData> parsedData(String stringToParse);

    /**
     * This method parse data from specifil file
     *
     * @param fileToParse
     * @return Arraylist whith parsed data.
     */
    public abstract ArrayList<RowData> parsedData(File fileToParse);

    /**
     * This method export data from input arraylist to DBF file.
     */
    public int exportData(ArrayList<RowData> dataToExport) {
        DBFField fields[] = new DBFField[4];
        fields[0] = new DBFField();
        fields[0].setName(IDENTIFIKATE_FIELD);
        fields[0].setDataType(DBFField.FIELD_TYPE_C);
        fields[0].setFieldLength(
                10);
        fields[1] = new DBFField();
        fields[1].setName(NUMBER_FIELD);
        fields[1].setDataType(DBFField.FIELD_TYPE_C);
        fields[1].setFieldLength(
                20);
        //fields[1].setDecimalCount(2);
        fields[2] = new DBFField();
        fields[2].setName(PRICE_WITH_TAXES_FIELD);
        fields[2].setDataType(DBFField.FIELD_TYPE_C);
        fields[2].setFieldLength(
                12);
        //fields[2].setDecimalCount(2);
        fields[3] = new DBFField();
        fields[3].setName(TAX_RATE_FIELD);
        fields[3].setDataType(DBFField.FIELD_TYPE_C);
        fields[3].setFieldLength(
                12);
        //fields[3].setDecimalCount(2);
        DBFWriter writer = new DBFWriter();
        try {
            writer.setFields(fields);
        } catch (DBFException ex) {
            Logger.getLogger(FischerIm.class.getName()).log(Level.SEVERE, null, ex);
        }
        Iterator recordIt = dataToExport.iterator();
        while (recordIt.hasNext()) {
            RowData record = (RowData) recordIt.next();
            Object rowData[] = new Object[4];
            rowData[0] = record.getItemCode();
            Double cell = record.getNumberOfItems();
            rowData[1] = cell.toString();
            cell = record.getPriceWithoutTaxes();
            rowData[2] = cell.toString();
            cell = record.getTaxRate();
            rowData[3] = cell.toString();
            try {

                writer.addRecord(rowData);
            } catch (DBFException ex) {
                Logger.getLogger(FischerIm.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(OUTPUT_FILE);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FischerIm.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            writer.write(
                    fos);
        } catch (DBFException ex) {
            Logger.getLogger(FischerIm.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            fos.close();

        } catch (IOException ex) {
            Logger.getLogger(FischerIm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;

    }
}
