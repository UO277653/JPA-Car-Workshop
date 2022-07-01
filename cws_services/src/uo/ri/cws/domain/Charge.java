package uo.ri.cws.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import alb.util.assertion.ArgumentChecks;
import uo.ri.cws.domain.Invoice.InvoiceStatus;
import uo.ri.cws.domain.base.BaseEntity;

@Entity
// uniqueconstraint invoice_id paymentmean_id
@Table(name = "TCharges",
		uniqueConstraints = {
		@UniqueConstraint(columnNames = {"INVOICE_ID", "PAYMENTMEAN_ID"}
		)
})
public class Charge extends BaseEntity{
	// natural attributes
	private double amount = 0.0;

	// accidental attributes
	@ManyToOne private Invoice invoice;
	@ManyToOne private PaymentMean paymentMean;

	Charge(){}
	
	public Charge(double amount) {
		super();
		this.amount = amount;
	}
	
	public Charge(Invoice invoice, PaymentMean paymentMean, double amount) {
		this(amount);
		
		//this.amount = amount;
		// store the amount
		// increment the paymentMean accumulated -> paymentMean.pay( amount )
		// link invoice, this and paymentMean
		
		ArgumentChecks.isNotNull(paymentMean);
		ArgumentChecks.isTrue(amount >= 0);
		
		paymentMean.pay(amount);
		
		Associations.Charges.link(paymentMean, this, invoice);
	}

	public double getAmount() {
		return amount;
	}

	@Override
	public String toString() {
		return "Charge [amount=" + amount + "]";
	}

//	@Override
//	public int hashCode() {
//		return Objects.hash(amount);
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Charge other = (Charge) obj;
//		return Double.doubleToLongBits(amount) == Double
//				.doubleToLongBits(other.amount);
//	}

	public Invoice getInvoice() {
		return invoice;
	}

	public PaymentMean getPaymentMean() {
		return paymentMean;
	}
	
	void _setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	
	void _setPaymentMean(PaymentMean paymentMean) {
		this.paymentMean = paymentMean;
	}

	/**
	 * Unlinks this charge and restores the accumulated to the payment mean
	 * @throws IllegalStateException if the invoice is already settled
	 */
	public void rewind() {
		// asserts the invoice is not in PAID status
		// decrements the payment mean accumulated ( paymentMean.pay( -amount) )
		// unlinks invoice, this and paymentMean
		if(invoice.getStatus() == InvoiceStatus.PAID) {
			throw new IllegalStateException("Error: the invoice is already"
					+ "settled");
		}
		
		paymentMean.pay(-amount);
		
		Associations.Charges.unlink(this);
	}

}
