/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.toms_cz.business;

import com.toms_cz.data.RowData;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 * This class represents supplier Fischer international s.r.o.tain a specific
 * approach to read file and parse this file. This supplier use XML format. This
 * is reason how is there DOM XML parser. Licence GNU/GPL
 * 
 * @author Tom
 */
public class FischerIm extends Template {
	/**
	 * Constructor of class FischerIM, set file type and file description which
	 * this supplier use and
	 */
	public FischerIm() {
		setFileDescription("PDF soubory");
		setFileType("PDF");
	}

	@Override
	public String readFile(File fileToRead) {
		PDFParser parser;
		String fileString = null;
		try {
			parser = new PDFParser(new FileInputStream(fileToRead));
			parser.parse();
			COSDocument cos;
			cos = parser.getDocument();
			PDFTextStripper strip;
			strip = new PDFTextStripper();
			PDDocument doc;
			doc = new PDDocument(cos);
			fileString = strip.getText(doc);
			doc.close();
		} catch (IOException ex) {
			Logger.getLogger(Template.class.getName()).log(Level.SEVERE, null, ex);
		}
		return fileString;
	}

	@Override
	public ArrayList<RowData> parsedData(String stringToParse) {
		String parsed = null;
		int startPosition = 0;
		int stopPosition = 0;
		int numberOfPages = 0;
		StringBuilder buffer = new StringBuilder(stringToParse);
		Pattern pattern = Pattern.compile("abrazivo");
		Matcher matcher = pattern.matcher(stringToParse);
		stringToParse = stringToParse.replaceAll("abrazivo\r\n", "");
		/*
		 * List<Integer> startParse=new ArrayList<>(); while(matcher.find()){
		 * startParse.add(matcher.start()); } Iterator
		 * stIt=startParse.iterator(); while(stIt.hasNext()){
		 * 
		 * }
		 */
		// TODO delete abrazivo
		pattern = Pattern.compile("FAKTURA");

		matcher = pattern.matcher(stringToParse);
		while (matcher.find()) {
			numberOfPages++;
		}
		if (numberOfPages > 1) {
			String pages[];
			pages = pagesParsed(stringToParse, numberOfPages);
			parsed = pages[0];
			for (int j = 1; j < numberOfPages; j++) {
				pattern = Pattern.compile("\r\n");
				matcher = pattern.matcher(pages[j]);
				if (matcher.find()) {
					startPosition = matcher.end();
					pages[j] = pages[j].substring(startPosition);
				}
				parsed = parsed.concat(pages[j]);
			}
		} else {
			pattern = Pattern.compile("Měna");
			matcher = pattern.matcher(stringToParse);
			if (matcher.find()) {
				startPosition = matcher.end();
				stringToParse = stringToParse.substring(startPosition);
			}
			pattern = Pattern.compile("Diskont");
			matcher = pattern.matcher(stringToParse);
			if (matcher.find()) {
				stopPosition = matcher.start();
				parsed = stringToParse.substring(0, stopPosition);
			}
		}
		String[] parsedArray = parsed.split("\\r?\\n");
		parsedArray[0] = null;
		ArrayList<RowData> records = new ArrayList<>();
		try {
			for (int j = 1; j < parsedArray.length; j = j + 3) {
				boolean aditionalPrice;
				boolean aditionalPieces;
				String line = parsedArray[j + 2];
				if (line.contains(",")) {
					line = line.replaceAll("\\.", " ");
					line = line.replaceAll("\\,", "\\.");
				}
				String[] firstLine = parsedArray[j].split(" ");
				String[] secondLine = line.split(" ");
				RowData nowLine = new RowData();
				nowLine.setItemCode(firstLine[0]);
				try {
					int testPieces = Integer.parseInt(secondLine[0]);
					aditionalPieces = true;
				} catch (NumberFormatException e) {
					aditionalPieces = false;
				}
				String adPieces = null;
				if (aditionalPieces == true) {
					adPieces = secondLine[0];
					adPieces = adPieces.concat(secondLine[1]);
				} else {
					adPieces = secondLine[0];
				}
				nowLine.setNumberOfItems(Double.parseDouble(adPieces));
				nowLine.setTaxRate(Double.parseDouble(secondLine[secondLine.length - 2]));
				try {
					Integer.parseInt(secondLine[secondLine.length - 4]);
					aditionalPrice = true;
				} catch (NumberFormatException e) {
					aditionalPrice = false;
				}
				String price = null;
				if (aditionalPrice == true) {
					price = secondLine[secondLine.length - 4];
					price = price.concat(secondLine[secondLine.length - 3]);
				} else {
					price = secondLine[secondLine.length - 3];
				}
				nowLine.setPriceWithoutTaxes(Double.parseDouble(price));
				records.add(nowLine);
			}
		} catch (Exception e) {
			System.out.println(e.getCause());
		}
		return records;

	}

	private String[] pagesParsed(String document, int numberOfPages) {
		Pattern pattern;
		Matcher matcher;
		String[] pages = new String[numberOfPages];
		for (int j = 0; j < numberOfPages - 1; j++) {
			String part = null;
			pattern = Pattern.compile("Měna");
			matcher = pattern.matcher(document);
			if (matcher.find()) {
				int startPosition = matcher.end();
				document = document.substring(startPosition);
			}
			pattern = Pattern.compile("Vystavil/a");
			matcher = pattern.matcher(document);
			if (matcher.find()) {
				int startPosition = matcher.start();
				part = document.substring(0, startPosition);
				document = document.substring(startPosition);
			}
			pages[j] = part;
		}
		pattern = Pattern.compile("Měna");
		matcher = pattern.matcher(document);
		if (matcher.find()) {
			int startPosition = matcher.end();
			document = document.substring(startPosition);
		}
		pattern = Pattern.compile("Diskont");
		matcher = pattern.matcher(document);
		if (matcher.find()) {
			int stopPosition = matcher.start();
			document = document.substring(0, stopPosition);
			pages[pages.length - 1] = document;
		} else {
			String prevPage = pages[pages.length - 2];
			matcher = pattern.matcher(prevPage);
			if (matcher.find()) {
				int stopPosition = matcher.start();
				prevPage = prevPage.substring(0, stopPosition);
				pages[pages.length - 2] = prevPage;
			}

			pages[pages.length - 1] = "";
		}
		return pages;
	}

	@Override
	public ArrayList<RowData> parsedData(File fileToParse) {
		return null;
	}
}
