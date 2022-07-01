package uo.ri.cws.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import alb.util.assertion.ArgumentChecks;
import uo.ri.cws.domain.base.BaseEntity;

@Entity
@Table(name = "TRecommendations")
public class Recommendation extends BaseEntity{

	//private String id;
	private boolean usedForVoucher;
	
	// accidental*
	@OneToOne private Client recommended;
	@ManyToOne private Client sponsor;
	
	public Recommendation() {}
	
	public Recommendation(Client sponsor, Client rec) {
		// checks
		ArgumentChecks.isNotNull(rec);
		ArgumentChecks.isNotNull(rec);
		
		Associations.Recommend.link(rec, sponsor, this);
	}

	public Client getRecommended() {
		return recommended;
	}

	public Client getSponsor() {
		return sponsor;
	}
	
	void _setRecommended(Client rec) {
		this.recommended = rec;
	}
	
	void _setSponsor(Client sponsor) {
		this.sponsor = sponsor;
	}

	public void markAsUsed() {
		this.setUsedForVoucher(true);
		
	}

	public boolean isUsedForVoucher() {
		return usedForVoucher;
	}

	public void setUsedForVoucher(boolean usedForVoucher) {
		this.usedForVoucher = usedForVoucher;
	}

}
