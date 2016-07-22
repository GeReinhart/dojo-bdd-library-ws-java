package com.kelkoo.dojo.bdd.suggestions.dependencies.search;

public class Book {

	private String bookId ;
	
	private String bookTitle ;

	private String categoryId ;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bookId == null) ? 0 : bookId.hashCode());
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
		Book other = (Book) obj;
		if (bookId == null) {
			if (other.bookId != null)
				return false;
		} else if (!bookId.equals(other.bookId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Book [bookId=" + bookId + ", bookTitle=" + bookTitle + ", categoryId=" + categoryId + "]";
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

	 
}
