package com.kelkoo.dojo.bdd.suggestions.representations;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.kelkoo.dojo.bdd.suggestions.dependencies.search.Book;

@XmlRootElement(name = "suggestions")
@XmlAccessorType(XmlAccessType.FIELD)
public class Suggestions {

	@XmlElement(name = "suggestions")
	private List<Suggestion> suggestions = new ArrayList<Suggestion>();

	public Suggestions() {

	}

	public Suggestions(List<Suggestion> suggestions) {
		this.suggestions = suggestions;
	}

	public Suggestions(Suggestion suggestion) {
		addSuggestion(suggestion);
	}

	public Suggestions(Suggestions suggestions) {
		addSuggestions(suggestions);
	}
	
	public boolean isEmpty(){
		return suggestions.isEmpty();
	}
	
	public List<Suggestion> getSuggestions() {
		return suggestions;
	}

	public void addSuggestions(List<Suggestion> suggestions) {
		this.suggestions.addAll(suggestions);
	}

	public void addSuggestionsAsBooks(List<Book> books) {
		for (Book book : books) {
		   addSuggestion(book);	
		}
	}
	
	public void addSuggestions(Suggestions suggestions) {
		this.suggestions.addAll(suggestions.getSuggestions());
	}

	public void addSuggestion(Suggestion suggestion) {
		this.suggestions.add(suggestion);
	}

	public void addSuggestion(Book book) {
		this.suggestions.add(new Suggestion(book));
	}
	
	public int size(){
		return this.suggestions.size();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((suggestions == null) ? 0 : suggestions.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Suggestions other = (Suggestions) obj;
		if (suggestions == null) {
			if (other.suggestions != null)
				return false;
		} else if (!suggestions.equals(other.suggestions))
			return false;
		return true;
	}

	public void setSuggestions(List<Suggestion> suggestions) {
		this.suggestions = suggestions;
	}

	@Override
	public String toString() {
		return "Suggestions [suggestions=" + suggestions + "]";
	}

}
