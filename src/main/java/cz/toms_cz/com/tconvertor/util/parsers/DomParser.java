package cz.toms_cz.com.tconvertor.util.parsers;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.toms_cz.tconvertor.business.Proteco;
import com.toms_cz.tconvertor.dao.RowData;

public class DomParser {

	private static final String TAX_RATE = "21";
	
	private String itemStart;
	private String code;
	private String quantity;
	private String totalPrice;
	
	
	private DocumentBuilderFactory dbf = null;
	DocumentBuilder builder = null;

	public DomParser() {
		dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
	}
	

	public DomParser(String itemStart, String code, String quantity, String totalPrice) {
		this.itemStart = itemStart;
		this.code = code;
		this.quantity = quantity;
		this.totalPrice = totalPrice;
		dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
	}

	public String domRead(File fileToRead) {
		String fileContent = null;
		try {
			builder = dbf.newDocumentBuilder();
			// parser zpracuje vstupní soubor a vytvoří z něj strom DOM objektů
			Document doc = builder.parse(fileToRead);
			// zpracujeme DOM strom
			fileContent = readXml(doc);
		} catch (Exception e) {
			Logger.getLogger(Proteco.class.getName()).log(Level.SEVERE, null, e);
		}
		return fileContent;
	}

	public ArrayList<RowData> parseData(File fileToParse) {
		ArrayList<RowData> records = null;
		try {
			builder = dbf.newDocumentBuilder();
			// parser zpracuje vstupní soubor a vytvoří z něj strom DOM objektů
			Document doc = builder.parse(fileToParse);
			// zpracujeme DOM strom
			records = exportXML(doc);
		} catch (Exception e) {
			Logger.getLogger(Proteco.class.getName()).log(Level.SEVERE, null, e);
		}
		return records;
	}

	private String readXml(Document doc) {
		Node rootNode = doc.getDocumentElement();
		return rootNode.getTextContent();
	}

	private ArrayList<RowData> exportXML(Document doc) {
		NodeList itemList = doc.getElementsByTagName(itemStart);
		ArrayList<RowData> records = new ArrayList<>();
		for (int i = 0; i < itemList.getLength(); i++) {
			Node currItem = itemList.item(i);
			NodeList itemDesc = currItem.getChildNodes();
			RowData row = new RowData();
			for (int j = 0; j < itemDesc.getLength(); j++) {
				Node currNode = itemDesc.item(j);
				if (currNode.getNodeName().equals(code)) {
					row.setItemCode(currNode.getTextContent());
				} else if (currNode.getNodeName().equals(quantity)) {
					String numberOfItems = currNode.getTextContent();
					row.setNumberOfItems(Double.valueOf(numberOfItems));
				} else if (currNode.getNodeName().equals(totalPrice)) {
					String totalPrice = currNode.getTextContent();
					row.setPriceWithoutTaxes(Double.valueOf(totalPrice));
				}
			}
			row.setTaxRate(Double.valueOf(TAX_RATE));
			records.add(row);
		}
		if (records.size() < 1) {
			return null;
		}
		return records;
	}
	
	
}
