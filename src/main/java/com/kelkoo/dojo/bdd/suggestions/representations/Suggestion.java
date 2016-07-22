package com.kelkoo.dojo.bdd.suggestions.representations;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.kelkoo.dojo.bdd.suggestions.dependencies.search.Book;

@XmlRootElement(name = "suggestion")
@XmlAccessorType(XmlAccessType.FIELD)
public class Suggestion {

	@XmlAttribute
	private String bookId;
	@XmlAttribute
	private String bookTitle;
	@XmlAttribute
	private String categoryId;
	
	public Suggestion() {

	}

	public Suggestion( String bookId, String bookTitle, String categoryId) {
		this.bookId = bookId;
		this.bookTitle = bookTitle;
		this.categoryId = categoryId ;
	}

	public Suggestion( Book book) {
		this.bookId = book.getBookId();
		this.bookTitle = book.getBookTitle();
		this.categoryId = book.getCategoryId();
	}

	public String getBookId() {
		return bookId;
	}


	public String getBookTitle() {
		return bookTitle;
	}

	public String getCategoryId() {
		return categoryId;
	}

	@Override
	public String toString() {
		return "Suggestion [bookId=" + bookId + ", bookTitle=" + bookTitle + ", categoryId=" + categoryId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bookId == null) ? 0 : bookId.hashCode());
		result = prime * result + ((bookTitle == null) ? 0 : bookTitle.hashCode());
		result = prime * result + ((categoryId == null) ? 0 : categoryId.hashCode());
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
		Suggestion other = (Suggestion) obj;
		if (bookId == null) {
			if (other.bookId != null)
				return false;
		} else if (!bookId.equals(other.bookId))
			return false;
		if (bookTitle == null) {
			if (other.bookTitle != null)
				return false;
		} else if (!bookTitle.equals(other.bookTitle))
			return false;
		if (categoryId == null) {
			if (other.categoryId != null)
				return false;
		} else if (!categoryId.equals(other.categoryId))
			return false;
		return true;
	}
	


	

}
