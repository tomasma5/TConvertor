/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.toms_cz.tconvertor.business;

import cz.toms_cz.com.tconvertor.util.TConvertorConstants;
import cz.toms_cz.com.tconvertor.util.readers.PDFReader;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.toms_cz.tconvertor.dao.RowData;

/**
 * This class can convert invoice paper from supplier
 * Levior s.r.o.
 * Troubky č.p.:900
 * 751 01 Troubky
 * Czech republic
 * 
 * to row data {@link RowData} format.
 * @author Tom
 * @version 16.8.2012
 */
public class Levior extends Template {


	/**
	 * Constructor for this class it set default reader which will be used to read file and
	 * file type to read.
	 */
    public Levior() {
    	reader = new PDFReader();
        setFileDescription(TConvertorConstants.PDF_TYPE_DESCRIPTOR);
        setFileType(TConvertorConstants.PDF_TYPE_LABEL);
    }

    @Override
    public ArrayList<RowData> parsedData(String stringToParse) {
        int startPosition = 0;
        int stopPosition = 0;

        int numberOfPages = 0;
        Pattern pattern = Pattern.compile("Zpracoval ekonomický systém CÉZAR G1.");
        Matcher matcher = pattern.matcher(stringToParse);
        while (matcher.find()) {
            numberOfPages++;
        }
        String partWithSupply = "";
        if (numberOfPages > 1) {
            int[] startPagePositions = new int[numberOfPages];
            int[] stopPositions = new int[numberOfPages];
            pattern = Pattern.compile("Č Á S T K A");
            matcher = pattern.matcher(stringToParse);
            int indexOfFinder = 0;
            while (matcher.find()) {
                startPagePositions[indexOfFinder] = matcher.end();
                indexOfFinder++;
            }
            pattern = Pattern.compile("... pokračuje na další straně");
            matcher = pattern.matcher(stringToParse);
            indexOfFinder = 0;
            while (matcher.find()) {
                stopPositions[indexOfFinder] = matcher.start();
                indexOfFinder++;
            }
            pattern = Pattern.compile("CELKEM");
            matcher = pattern.matcher(stringToParse);
            matcher.find();
            if (matcher.find()) {
                stopPositions[indexOfFinder] = matcher.start();
            }
            //Parsin string between start and stop positions
            for (int j = 0; j < startPagePositions.length; j++) {
                String pageSupply = stringToParse.substring(startPagePositions[j], stopPositions[j]);
                partWithSupply = partWithSupply.concat(pageSupply);
            }
            System.out.println(partWithSupply);

        } else {
            pattern = Pattern.compile("Č Á S T K A");
            matcher = pattern.matcher(stringToParse);
            if (matcher.find()) {
                startPosition = matcher.end();
            }
            pattern = Pattern.compile("CELKEM");
            matcher = pattern.matcher(stringToParse);
            matcher.find();
            if (matcher.find()) {
                stopPosition = matcher.start();
            }
            partWithSupply = partWithSupply.concat(stringToParse.substring(startPosition, stopPosition));
        }
        String[] parsedArray = partWithSupply.split("\\r?\\n");
        ArrayList<RowData> records = new ArrayList<>();
        for (int j = 1; j < parsedArray.length; j++) {
            RowData row = new RowData();
            String rowString = parsedArray[j];
            if (rowString.equals("")) {
                continue;
            }
            String[] parsedRow = rowString.split(" ");
            row.setItemCode(parsedRow[1]);
            pattern = Pattern.compile("%");
            matcher = pattern.matcher(rowString);
            String secondPart = "";
            if (matcher.find()) {
                secondPart = secondPart.concat(rowString.substring(matcher.start() - 2, rowString.length()));
            }
            secondPart = secondPart.replace("\\t", " ");
            String[] secondPartRow = secondPart.split(" ");
            int beginIndexOfNumberItems = 0;
            for (int i = 2; i < secondPartRow.length; i++) {
                if (!(secondPartRow[i].equals(""))) {
                    beginIndexOfNumberItems = i;
                    break;
                }
            }
            row.setTaxRate(Double.valueOf(secondPartRow[0].substring(0, secondPartRow[0].length() - 1)));
            String numberOfItems = secondPartRow[beginIndexOfNumberItems];
            numberOfItems = numberOfItems.replace(".", " ");
            numberOfItems = numberOfItems.replace(",", ".");
            row.setNumberOfItems(Double.valueOf(numberOfItems));
            String pricePieces = secondPartRow[beginIndexOfNumberItems + 1];
            pricePieces = pricePieces.replace(",-", "");
            pricePieces = pricePieces.replace(".", "");
            pricePieces = pricePieces.replace(",", ".");
            double pieces = Double.valueOf(numberOfItems);
            double price = Double.valueOf(pricePieces);
            double total = pieces * price;
           
            DecimalFormat df = new DecimalFormat("#.##");
            String tempDouble=df.format(total);
            tempDouble=tempDouble.replace(",", ".");
            Double priceWithoutTaxes = Double.valueOf(tempDouble);
            row.setPriceWithoutTaxes(priceWithoutTaxes);
            records.add(row);
            System.out.println(row.getItemCode());
        }
        return records;

    }

    @Override
    public ArrayList<RowData> parsedData(File fileToParse) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
