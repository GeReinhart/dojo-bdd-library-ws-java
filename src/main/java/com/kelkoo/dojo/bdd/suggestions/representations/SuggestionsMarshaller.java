package com.kelkoo.dojo.bdd.suggestions.representations;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


public class SuggestionsMarshaller {

	
	public Suggestions deserialize(String suggestionsAsString) {
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(Suggestions.class);
			StringReader sr = new StringReader(suggestionsAsString);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			return (Suggestions) unmarshaller.unmarshal(sr);
		} catch (JAXBException e) {
			throw new RuntimeException(suggestionsAsString,e );
		}
	}

	public String serialize(Suggestions suggestions) {
		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(Suggestions.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			jaxbMarshaller.marshal(suggestions, baos);

			return baos.toString("UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
