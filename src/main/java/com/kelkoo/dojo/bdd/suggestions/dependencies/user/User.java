package com.kelkoo.dojo.bdd.suggestions.dependencies.user;

import java.util.ArrayList;
import java.util.List;

import com.kelkoo.dojo.bdd.suggestions.dependencies.search.Book;

public class User {

	private String userId ;
	
	private Integer age ;

	private List<Book> alreadyBookedBooks  = new ArrayList<Book>();
	
	public User(String userId){
		this.userId = userId;
	}

	public User(String userId, Integer age){
		this.userId = userId;
		this.age = age ;
	}
	
	public User(String userId, Integer age, List<Book> alreadyBookedBooks){
		this.userId = userId;
		this.age = age ;
		this.alreadyBookedBooks = alreadyBookedBooks;
	}
	
	public User() {
	}

	public String getUserId() {
		return userId;
	}

	public boolean hasAlreadyBooked(Book book){
		return alreadyBookedBooks.contains(book) ;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((age == null) ? 0 : age.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		User other = (User) obj;
		if (age == null) {
			if (other.age != null)
				return false;
		} else if (!age.equals(other.age))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", age=" + age + ", alreadyBookedBooks=" + alreadyBookedBooks + "]";
	}

	public Integer getAge() {
		return age;
	}

	public List<Book> getAlreadyBookedBooks() {
		return alreadyBookedBooks;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public void setAlreadyBookedBooks(List<Book> alreadyBookedBooks) {
		this.alreadyBookedBooks = alreadyBookedBooks;
	}

	
	
	
	
	
	
}
