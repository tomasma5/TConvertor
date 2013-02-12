/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.toms_cz.tconvertor.business;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.toms_cz.tconvertor.dao.RowData;

import cz.toms_cz.com.tconvertor.util.TConvertorConstants;
import cz.toms_cz.com.tconvertor.util.readers.HTMLXMLReader;

/**
 * 
 * @author Tom
 */
public class Pht extends Template {

	/**
	 * Constructor for this class it set default reader which will be used to read file and
	 * file type to read.
	 */
	public Pht() {
		reader = new HTMLXMLReader();
		setFileDescription(TConvertorConstants.HTML_TYPE_DESCRIPTOR);
		setFileType(TConvertorConstants.HTML_TYPE_LABEL);
	}


	@Override
	public ArrayList<RowData> parsedData(String stringToParse) {
		return null;
	}

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

	private StringBuffer parseXMLPart(File fileToParse) {
		StringBuffer sBuf = new StringBuffer();
		BufferedReader bufferedReader = null;
		try {
			InputStreamReader in;
			in = new InputStreamReader(new FileInputStream(fileToParse), Charset.forName("windows-1250"));
			bufferedReader = new BufferedReader(in);
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
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sBuf;
	}

	private String splitByReg(String source, String regex) throws IndexOutOfBoundsException {
		String[] currColumn = source.split(regex);
		currColumn = currColumn[1].split(" ");
		String result = currColumn[0];
		result = result.substring(1, result.length() - 1);
		return result;
	}
}


