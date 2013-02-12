package cz.toms_cz.com.tconvertor.util.readers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import com.toms_cz.tconvertor.business.Template;

public class PDFReader implements IReader{

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

}
