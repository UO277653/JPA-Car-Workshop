package uo.ri.cws.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import alb.util.assertion.ArgumentChecks;

@Entity
@Table(name = "TCashes")
// @DiscriminatorValue(value = "")
public class Cash extends PaymentMean {

	Cash(){}
	
	public Cash(Client client) {
		// validate
		ArgumentChecks.isNotNull(client);
		Associations.Pay.link(this, client);
	}

	@Override
	public boolean canPay(double amount) {
		return true;
	}
	
	
}
