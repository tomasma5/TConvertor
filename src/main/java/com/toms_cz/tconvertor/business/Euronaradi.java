/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.toms_cz.tconvertor.business;

import cz.toms_cz.com.tconvertor.util.TConvertorConstants;
import cz.toms_cz.com.tconvertor.util.readers.PDFReader;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.toms_cz.tconvertor.dao.RowData;

/**
 *
 * @author Tom
 * @version 12.8.2012
 */
public class Euronaradi extends Template {

	/**
	 * Constructor for this class it set default reader which will be used to read file and
	 * file type to read.
	 */
    public Euronaradi() {
    	reader = new PDFReader();
        setFileDescription(TConvertorConstants.PDF_TYPE_DESCRIPTOR);
        setFileType(TConvertorConstants.PDF_TYPE_LABEL);
    }


    @Override
    public ArrayList<RowData> parsedData(String stringToParse) {
         //TODO not supported more then one page of item list
        //TODO rework - not working - only copy paste !!!
        int startPosition = 0;
        int stopPosition = 0;
        Pattern pattern = Pattern.compile("Pozn.");
        Matcher matcher = pattern.matcher(stringToParse);
        if (matcher.find()) {
            startPosition = matcher.end();
        }
        pattern = Pattern.compile("CELKEM");
        matcher = pattern.matcher(stringToParse);
        if (matcher.find()) {
          if(matcher.find()){
              stopPosition = matcher.start();
          }
        }
        String parsedString = stringToParse.substring(startPosition, stopPosition);
        String[] parsedArray = parsedString.split("\\r?\\n");
        ArrayList<RowData> records = new ArrayList<>();
        for (int i = 1; i < parsedArray.length; i++) {
            pattern = Pattern.compile("%");
            matcher = pattern.matcher(parsedArray[i]);
            int startPercentPosition = 0;
            if (matcher.find()) {
                startPercentPosition = matcher.start()-2;
            }
            String behindPercentPart = parsedArray[i].substring(startPercentPosition);
            String[] rowArray = behindPercentPart.split(" ");
            RowData row = new RowData();
            String taxRate=rowArray[0].substring(0,2);
            row.setTaxRate(Double.parseDouble(taxRate));
            row.setItemCode(parsedArray[i].split(" ")[4]);
            String numberOfItems = rowArray[3];
            numberOfItems = numberOfItems.replace(".", "");
             numberOfItems = numberOfItems.replace(",", ".");
            row.setNumberOfItems(Double.parseDouble(numberOfItems));
            String priceWithoutTaxis = rowArray[4];
            priceWithoutTaxis = priceWithoutTaxis.replace("-", "");
            priceWithoutTaxis = priceWithoutTaxis.replace(".", "");
            priceWithoutTaxis=priceWithoutTaxis.replace(",", ".");
            row.setPriceWithoutTaxes(Double.parseDouble(priceWithoutTaxis));
            records.add(row);
        }
        return records;
    }

    @Override
    public ArrayList<RowData> parsedData(File fileToParse) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
