package cz.toms_cz.com.tconvertor.util.readers;

import java.io.File;

import cz.toms_cz.com.tconvertor.util.parsers.DomParser;

public class XMLReader  implements IReader{

	@Override
	public String readFile(File fileToRead) {
		DomParser dom = new DomParser();
		return dom.domRead(fileToRead);
	}

}
