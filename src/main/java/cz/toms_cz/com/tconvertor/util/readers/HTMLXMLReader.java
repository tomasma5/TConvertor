package cz.toms_cz.com.tconvertor.util.readers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.toms_cz.tconvertor.business.Pht;

import cz.toms_cz.com.tconvertor.util.parsers.HTMLParser;

public class HTMLXMLReader implements IReader{

	@Override
	public String readFile(File fileToRead) {
		InputStreamReader in = null;
		String fileString = null;
		try {
			in = new InputStreamReader(new FileInputStream(fileToRead), Charset.forName("windows-1250"));
			HTMLParser parser = new HTMLParser();
			parser.parse(in);
			fileString = parser.getText();
			Pattern pattern = Pattern.compile("Fakturujeme");
			Matcher matcher = pattern.matcher(fileString);
			int pos = 0;
			while (matcher.find()) {
				pos = matcher.start();
			}
			fileString = fileString.substring(0, pos);
		} catch (IOException ex) {
			Logger.getLogger(Pht.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
				Logger.getLogger(Pht.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return fileString;
	}
	

}
