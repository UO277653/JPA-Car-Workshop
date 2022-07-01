package uo.ri.cws.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import alb.util.assertion.ArgumentChecks;

@Entity
@Table(name = "TVouchers")
public class Voucher extends PaymentMean {
	@Column(unique = true) private String code;
	private double available = 0.0;
	private String description = "no-description";

	Voucher(){}
	
	public Voucher(String code) {
		// validate
		this.code = code;
	}
	
	public Voucher(String code, String description, double available) {
		this(code);
		
		ArgumentChecks.isNotEmpty(description);
		ArgumentChecks.isTrue(available >= 0);
		
		this.description = description;
		this.available = available;
		
	}
	
	public Voucher(String code, double available) {

		this(code);
		
		ArgumentChecks.isTrue(available >= 0);
		
		this.available = available;
	}

	/**
	 * Augments the accumulated (super.pay(amount) ) and decrements the available
	 * @throws IllegalStateException if not enough available to pay
	 */
	@Override
	public void pay(double amount) {

		if(amount > available) {
			throw new IllegalStateException("There is not enough available "
					+ "amount to pay");
		}
		
		super.pay(amount);
		
		available = available - amount;
	}

	public String getCode() {
		return code;
	}

	public double getAvailable() {
		return available;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
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
		Voucher other = (Voucher) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Voucher [code=" + code + ", available=" + available
				+ ", description=" + description + "]";
	}

	@Override
	public boolean canPay(double amount) {
		return amount <= available;
	}

}
