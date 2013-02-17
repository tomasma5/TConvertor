package com.toms_cz.tconvertor.business;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.toms_cz.tconvertor.dao.RowData;

import cz.toms_cz.com.tconvertor.util.TConvertorConstants;
import cz.toms_cz.com.tconvertor.util.readers.PDFReader;

public class Visimpex extends Template {

	private String _visimpex_number_page_identifier = "Pokračování";
	private String _visimpex_content_start = "Částka";
	private String _visimpex_last_page_content_end = "Zaokrouhlení";
	private String _visimpex_negative_line_identificator_1 = "Dodávka";
	private String []_visimpex_negative_line_identificators = {_visimpex_negative_line_identificator_1,_visimpex_number_page_identifier};
	private String _visimpex_piecces_identifier_1 = "Mks";
	private double _tax_rate = 21;

	public Visimpex() {
		reader = new PDFReader();
		setFileType(TConvertorConstants.PDF_TYPE_LABEL);
		setFileDescription(TConvertorConstants.PDF_TYPE_DESCRIPTOR);
	}

	@Override
	public ArrayList<RowData> parsedData(String stringToParse) {
		ArrayList<RowData> data = new ArrayList<>();
		Pattern pattern = Pattern.compile(_visimpex_number_page_identifier);
		Matcher matcher = pattern.matcher(stringToParse);
		int previousEndPosition = 0;
		Pattern contentPattern;
		Matcher contentMatcher;
		boolean oddIdentifier = false;
		while (matcher.find()) {
			// Parse page, this parse only page between last page, last page is
			// parsed separetly
			if(oddIdentifier){
				oddIdentifier = false;
				continue;
			}
			else{
				oddIdentifier = true;
			}
			int pageEndIndex = matcher.start();
			String pageContent = stringToParse.substring(previousEndPosition, pageEndIndex);
			contentPattern = Pattern.compile(_visimpex_content_start);
			contentMatcher = contentPattern.matcher(pageContent);
			if (contentMatcher.find()) {
				int pageContentStart = contentMatcher.end();
				pageContent = pageContent.substring(pageContentStart, pageEndIndex);
				data.addAll(parsePageContent(pageContent));
			}
			previousEndPosition = matcher.end();
		}
		// Parse last page
		String pageContent = stringToParse.substring(previousEndPosition);
		contentPattern = Pattern.compile(_visimpex_content_start);
		contentMatcher = contentPattern.matcher(pageContent);
		if (contentMatcher.find()) {
			int pageContentStart = contentMatcher.end();
			contentPattern = Pattern.compile(_visimpex_last_page_content_end);
			contentMatcher = contentPattern.matcher(pageContent);
			if(contentMatcher.find()){
				int endContent = contentMatcher.start();
				pageContent = pageContent.substring(pageContentStart,endContent);
				data.addAll(parsePageContent(pageContent));
			}
			
		}
		return data;
	}

	private ArrayList<RowData> parsePageContent(String pageContent) {
		ArrayList<RowData> data = new ArrayList<>();
		Pattern pattern = Pattern.compile("\r\n");
		Matcher matcher = pattern.matcher(pageContent);
		int startRow = 0;
		while (matcher.find()) {
			int endRow = matcher.start();
			String line = pageContent.substring(startRow, endRow);
			startRow = matcher.end();
			String[] row = line.split(" ");
			int rowLenght = row.length;
			if (rowLenght != 0 && !row[0].equals("") && !row[0].equals(" ")
					&& !containForbiddenString(line)) {
				RowData dataRow = new RowData();
				// Item identifier is always on first place
				dataRow.setItemCode(new String(row[0]));
				for (int i = 0; i < rowLenght; i++) {
					String column = row[i];
					if (column.equals(_visimpex_piecces_identifier_1)) {
						String pieces = row[i-1];
						pieces = pieces.replaceAll(",", ".");
						Double numberOfPieces = Double.parseDouble(pieces);
						dataRow.setNumberOfItems(numberOfPieces);
						String cost = row[i+1];
						if(row[i+2].contains(",")){
							cost = cost.concat(row[i+2]);
						}
						char []costArr = cost.toCharArray();
						int endOfCostIndex =0;
						for(int j=0;j<costArr.length;j++){
							if(costArr[j] == ','){
								endOfCostIndex = j+2;
								break;
							}
						}
						cost = cost.substring(0,endOfCostIndex);
						cost = cost.replaceAll(" ", "");
						cost = cost.replaceAll(",", ".");
					
						Double totalCost = Double.parseDouble(cost);
						dataRow.setPriceWithoutTaxes(totalCost);
						dataRow.setTaxRate(_tax_rate);
					}
				}
				data.add(dataRow);
			}
		}
		return data;
	}
	
	
	private boolean containForbiddenString(String line){
		for(int i=0;i<_visimpex_negative_line_identificators.length;i++){
			Pattern pattern = Pattern.compile(_visimpex_negative_line_identificators[i]);
			Matcher matcher = pattern.matcher(line);
			if(matcher.find()){
				return true;
			}
		}
		return false;
	}

	@Override
	public ArrayList<RowData> parsedData(File fileToParse) {
		// TODO Auto-generated method stub
		return null;
	}

}
