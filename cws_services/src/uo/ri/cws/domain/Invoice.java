package uo.ri.cws.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import alb.util.assertion.ArgumentChecks;
import uo.ri.cws.domain.base.BaseEntity;

@Entity
@Table(name = "TInvoices")
public class Invoice extends BaseEntity {
	public enum InvoiceStatus { NOT_YET_PAID, PAID }

	// natural attributes
	@Column(unique = true) private Long number;
	private LocalDate date;
	private double amount;
	private double vat;
	@Enumerated(EnumType.STRING) private InvoiceStatus status = InvoiceStatus.NOT_YET_PAID;

	private boolean usedForVoucher;
	
	// accidental attributes
	@OneToMany(mappedBy = "invoice") private Set<WorkOrder> workOrders = new HashSet<>();
	@OneToMany(mappedBy = "invoice") private Set<Charge> charges = new HashSet<>();

	Invoice(){}
	
	public Invoice(Long number) {
		this(number, new ArrayList<>());
	}

	public Invoice(Long number, LocalDate date) {
		// call full constructor with sensible defaults
		this(number, date, new ArrayList<>());
	}

	public Invoice(Long number, List<WorkOrder> workOrders) {
		this(number, LocalDate.now(), workOrders);
	}

	// full constructor
	public Invoice(Long number, LocalDate date, List<WorkOrder> workOrders) {
		// check arguments (always), through IllegalArgumentException
		// store the number
		// store a copy of the date
		// add every work order calling addWorkOrder( w )
		ArgumentChecks.isNotNull(number);
		ArgumentChecks.isNotNull(date);
		ArgumentChecks.isNotNull(workOrders);
		
		this.number = number;
		this.date = date;
		for(WorkOrder w: workOrders) {
			addWorkOrder(w);
		}
	}

	/**
	 * Computes amount and vat (vat depends on the date)
	 */
	private void computeAmount() {
		double amount = 0;
		
		for(WorkOrder wo: workOrders) {
			amount += wo.getAmount();
		}
		
		vat = LocalDate.parse("2012-07-01").isBefore(date) ? 21.0 : 18.0;
		
		amount = Math.round((amount + ((amount * vat)/100))*100);
		this.amount = amount/100;
		
	}

	/**
	 * Adds (double links) the workOrder to the invoice and updates the amount and vat
	 * @param workOrder
	 * @see UML_State diagrams on the problem statement document
	 * @throws IllegalStateException if the invoice status is not NOT_YET_PAID
	 */
	public void addWorkOrder(WorkOrder workOrder) {

		if(this.status != InvoiceStatus.NOT_YET_PAID) {
			throw new IllegalStateException("Error, the invoice status is not "
					+ "NOT_YET_PAID");
		}
		
		Associations.ToInvoice.link(this, workOrder);
		
		computeAmount();
		
		workOrder.markAsInvoiced();
	}

	/**
	 * Removes a work order from the invoice and recomputes amount and vat
	 * @param workOrder
	 * @see UML_State diagrams on the problem statement document
	 * @throws IllegalStateException if the invoice status is not NOT_YET_PAID
	 */
	public void removeWorkOrder(WorkOrder workOrder) {
		
		if(this.status != InvoiceStatus.NOT_YET_PAID) {
			throw new IllegalStateException("Error, the invoice status is "
					+ "not NOT_YET_PAID");
		}
		
		Associations.ToInvoice.unlink(this, workOrder);
		
		computeAmount();
		
		workOrder.markBackToFinished();
	}

	/**
	 * Marks the invoice as PAID, but
	 * @throws IllegalStateException if
	 * 	- Is already settled
	 *  - Or the amounts paid with charges to payment means do not cover
	 *  	the total of the invoice
	 */
	public void settle() {
		
		if(this.status == InvoiceStatus.PAID) {
			throw new IllegalStateException("Error, the invoice status is "
					+ "already PAID");
		}
		
		double chargeSum = 0;
		
		for(Charge c: charges) {
			chargeSum += c.getAmount();
		}
		
		if(chargeSum < (this.amount-0.01)) {
			throw new IllegalStateException("Error, the amount of the charges "
					+ "is not enough to pay the invoice");
		}
		
		if(chargeSum > (this.amount+0.01)) {
			throw new IllegalStateException("Error, the invoiced is being overpaid");
		}
		
		this.status = InvoiceStatus.PAID;
	}

	public Set<WorkOrder> getWorkOrders() {
		return new HashSet<>( workOrders );
	}

	Set<WorkOrder> _getWorkOrders() {
		return workOrders;
	}

	public Set<Charge> getCharges() {
		return new HashSet<>( charges );
	}

	Set<Charge> _getCharges() {
		return charges;
	}
	
	public Long getNumber() {
		return number;
	}

	public LocalDate getDate() {
		return date;
	}

	public double getAmount() {
		return amount;
	}

	public double getVat() {
		return vat;
	}

	public InvoiceStatus getStatus() {
		return status;
	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((number == null) ? 0 : number.hashCode());
//		return result;
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
//		Invoice other = (Invoice) obj;
//		if (number == null) {
//			if (other.number != null)
//				return false;
//		} else if (!number.equals(other.number))
//			return false;
//		return true;
//	}

	@Override
	public String toString() {
		return "Invoice [number=" + number + ", date=" + date + ", amount="
				+ amount + ", vat=" + vat + ", status=" + status + "]";
	}
	
	public boolean isNotSettled() {
		return this.status != InvoiceStatus.PAID;
	}
	
	public boolean isSettled() {
		return this.status == InvoiceStatus.PAID;
	}

	public boolean canGenerate500Voucher() {
		
		if(usedForVoucher) {
			throw new IllegalStateException("This invoice has already been used to generate a voucher");
		}
		
		return isSettled() && amount > 500;
	}

	public void markAsUsed() {
		if(!canGenerate500Voucher()) {
			throw new IllegalStateException("This invoice cannot be used to generate the voucher");
		}
		
		this.usedForVoucher = true;
	}

	public boolean isUsedForVoucher() {
		return usedForVoucher;
	}

}
