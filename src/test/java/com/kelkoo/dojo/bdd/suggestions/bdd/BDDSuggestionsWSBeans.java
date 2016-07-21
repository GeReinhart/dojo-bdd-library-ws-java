package com.kelkoo.dojo.bdd.suggestions.bdd;

import com.kelkoo.dojo.bdd.suggestions.representations.Suggestion;


public class BDDSuggestionsWSBeans {

	public static class BDDSuggestionBean{
		public String country ;
		public String offerId ;
		public String categoryId ;
		public String offerTitle ;
		
		
		public BDDSuggestionBean() {
		}

		public BDDSuggestionBean(String country, String offerId, String categoryId, String offerTitle) {
			super();
			this.country = country;
			this.offerId = offerId;
			this.categoryId = categoryId;
			this.offerTitle = offerTitle;
		}

		@Override
		public String toString() {
			return "BDDSuggestionBean [country=" + country + ", offerId=" + offerId + ", categoryId=" + categoryId
					+ ", offerTitle=" + offerTitle + "]";
		}
		
		public boolean isSameAs(Suggestion suggestion){
			if ( ! this.offerId.equals(suggestion.getOfferId())){
				return false;
			}
			if ( ! this.offerTitle.equals(suggestion.getOfferTitle())){
				return false;
			}
			return true;
		}
		
	}

	public static class BDDSearchDocumentBean{
		public String offerId ;
		public String categoryId ;
		public String offerTitle ;
		public Float  score ;
		
		public BDDSearchDocumentBean(){
		}
				
		public BDDSearchDocumentBean(String offerId, String categoryId, String offerTitle, Float score) {
			this.offerId = offerId;
			this.categoryId = categoryId;
			this.offerTitle = offerTitle;
			this.score = score;
		}

		@Override
		public String toString() {
			return "BDDSearchDocumentBean [offerId=" + offerId + ", categoryId=" + categoryId + ", offerTitle="
					+ offerTitle + ", score=" + score + "]";
		}
	}
	
	
}
