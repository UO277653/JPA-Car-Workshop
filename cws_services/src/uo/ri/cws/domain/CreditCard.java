package uo.ri.cws.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import alb.util.assertion.ArgumentChecks;

@Entity
@Table(name = "TCreditCards")
public class CreditCard extends PaymentMean {
	
	@Column(unique = true) private String number;
	private String type;
	private LocalDate validThru;
	
	CreditCard(){}
	
	public CreditCard(String number) {
		
		ArgumentChecks.isNotEmpty(number);
		this.number = number;
		
		validThru = LocalDate.now().plusDays(1);
		type = "UNKNOWN";
	}
	
	public CreditCard(String number, String type, LocalDate validThru) {
		this(number);
		
		ArgumentChecks.isNotEmpty(type);
		ArgumentChecks.isNotNull(validThru);
		
		this.type = type;
		this.validThru = validThru;
	}
	
	public String getNumber() {
		return number;
	}

	public String getType() {
		return type;
	}

	public LocalDate getValidThru() {
		return validThru;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((number == null) ? 0 : number.hashCode());
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
		CreditCard other = (CreditCard) obj;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CreditCard [number=" + number + ", type=" + type
				+ ", validThru=" + validThru + "]";
	}
	
	public boolean isValidNow() {
		return LocalDate.now().isBefore(validThru);
	}

	public void setValidThru(LocalDate minusDays) {
		this.validThru = minusDays;
	}
	
	@Override
	public void pay(double importe) {
		
		if(validThru.isBefore(LocalDate.now())) {
			throw new IllegalStateException("The date of expiration is "
					+ "incorrectly being set");
		}
		
		super.pay(importe);
	}

	@Override
	public boolean canPay(double amount) {
		return isValidNow();
	}
	
	

}
