/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.toms_cz.tconvertor.business;

import java.io.*;
import java.util.ArrayList;

import com.toms_cz.tconvertor.dao.RowData;

import cz.toms_cz.com.tconvertor.util.TConvertorConstants;
import cz.toms_cz.com.tconvertor.util.parsers.DomParser;
import cz.toms_cz.com.tconvertor.util.readers.XMLReader;

/**
 * Class proteco represent supplier Proteco. Contain a specific approach to read
 * file and parse this file. This supplier use XML format. This is reason how is
 * there DOM XML parser.
 * 
 * @author Tom
 * 
 */
public class Proteco extends Template {

	// ///////////////////////////////////
	// //////Settings for XML parse///////
	// //////////////////////////////////
	private final String PROTECO_ITEM_START = "item";
	private final String PROTECO_CODE = "article_number";
	private final String PROTECO_QUANTITY = "quantity";
	private final String PROTECO_TOTAL_PRICE = "total_price";

	/**
	 * Constructor for this class it set default reader which will be used to read file and
	 * file type to read.
	 */
	public Proteco() {
		reader = new XMLReader();
		setFileDescription(TConvertorConstants.XML_TYPE_DESCRIPTOR);
		setFileType(TConvertorConstants.XML_TYPE_LABEL);
	}

	@Override
	public ArrayList<RowData> parsedData(String stringToParse) {
		return null;
	}

	@Override
	public ArrayList<RowData> parsedData(File fileToParse) {
		DomParser domParser = new DomParser(PROTECO_ITEM_START, PROTECO_CODE, PROTECO_QUANTITY, PROTECO_TOTAL_PRICE);
		ArrayList<RowData> records = domParser.parseData(fileToParse);
		return records;
	}
}
