package com.kelkoo.dojo.bdd.suggestions.representations;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "suggestion")
@XmlAccessorType(XmlAccessType.FIELD)
public class Suggestion {

	@XmlAttribute
	private String offerId;
	@XmlAttribute
	private String offerTitle;

	public Suggestion() {

	}

	public Suggestion( String offerId, String offerTitle) {
		this.offerId = offerId;
		this.offerTitle = offerTitle;
	}


	public String getOfferId() {
		return offerId;
	}


	public String getOfferTitle() {
		return offerTitle;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((offerId == null) ? 0 : offerId.hashCode());
		result = prime * result + ((offerTitle == null) ? 0 : offerTitle.hashCode());
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
		if (offerId == null) {
			if (other.offerId != null)
				return false;
		} else if (!offerId.equals(other.offerId))
			return false;
		if (offerTitle == null) {
			if (other.offerTitle != null)
				return false;
		} else if (!offerTitle.equals(other.offerTitle))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Suggestion [offerId=" + offerId + ", offerTitle=" + offerTitle + "]";
	}

}
