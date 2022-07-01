package uo.ri.cws.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import alb.util.assertion.ArgumentChecks;
import uo.ri.cws.domain.base.BaseEntity;

@Entity
@Table(name = "TWorkOrders",
		uniqueConstraints = {
		@UniqueConstraint(columnNames = {"VEHICLE_ID", "DATE"})
})
public class WorkOrder extends BaseEntity{
	public enum WorkOrderStatus {
		OPEN,
		ASSIGNED,
		FINISHED,
		INVOICED
	}

	// natural attributes
	private LocalDateTime date;
	private String description = "no-description";
	private double amount = 0.0;
	
	@Enumerated(EnumType.STRING) private WorkOrderStatus status = WorkOrderStatus.OPEN;
	
	private boolean usedForVoucher;

	// accidental attributes
	@ManyToOne private Vehicle vehicle;
	@ManyToOne private Mechanic mechanic;
	@ManyToOne private Invoice invoice;
	@OneToMany(mappedBy = "workOrder") private Set<Intervention> interventions = new HashSet<>(); // protected?
	
	WorkOrder(){}
	
	public WorkOrder(Vehicle vehicle) {
		ArgumentChecks.isNotNull(vehicle);
		
		this.date = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
		
		Associations.Fix.link(vehicle, this);
	}

	public WorkOrder(Vehicle vehicle, String description) {
		this(vehicle);
		ArgumentChecks.isNotEmpty(description);
		this.description = description;
	}
	
	public WorkOrder(Vehicle vehicle, LocalDateTime date, String description) {
		this(vehicle, description);
		
		ArgumentChecks.isNotNull(date);
		
		this.date = date.truncatedTo(ChronoUnit.MILLIS);
	}

	/**
	 * Changes it to INVOICED state given the right conditions
	 * This method is called from Invoice.addWorkOrder(...)
	 * @see UML_State diagrams on the problem statement document
	 * @throws IllegalStateException if
	 * 	- The work order is not FINISHED, or
	 *  - The work order is not linked with the invoice
	 */
	public void markAsInvoiced() {

		if(this.status != WorkOrderStatus.FINISHED) {
			throw new IllegalStateException("Error, the workorder is not "
					+ "finished");
		}
		
		if(this.invoice == null) {
			throw new IllegalStateException("Error, the workorder is not "
					+ "linked with the invoice");
		}
		
		this.status = WorkOrderStatus.INVOICED;
	}

	/**
	 * Changes it to FINISHED state given the right conditions and
	 * computes the amount
	 *
	 * @see UML_State diagrams on the problem statement document
	 * @throws IllegalStateException if
	 * 	- The work order is not in ASSIGNED state, or
	 *  - The work order is not linked with a mechanic
	 */
	public void markAsFinished() {

		if(this.status != WorkOrderStatus.ASSIGNED) {
			throw new IllegalStateException("Error, the workorder is not "
					+ "assigned");
		}
		
		if(this.mechanic == null) {
			throw new IllegalStateException("Error, the workorder is not "
					+ "linked with any mechanic");
		}
		
		this.status = WorkOrderStatus.FINISHED;
		
		computeAmount();
	}
	
	private void computeAmount() {
		double amount = 0;
		
		for(Intervention i: interventions) {
			amount += i.getAmount();
		}
		
		this.amount = amount;
	}

	/**
	 * Changes it back to FINISHED state given the right conditions
	 * This method is called from Invoice.removeWorkOrder(...)
	 * @see UML_State diagrams on the problem statement document
	 * @throws IllegalStateException if
	 * 	- The work order is not INVOICED, or
	 *  - The work order is still linked with the invoice
	 */
	public void markBackToFinished() {

		if(this.status != WorkOrderStatus.INVOICED) {
			throw new IllegalStateException("Error, the workorder is not "
					+ "invoiced");
		}
		
		if(this.invoice != null) {
			throw new IllegalStateException("Error, the workorder is still "
					+ "linked with the invoice");
		}
		
		this.status = WorkOrderStatus.FINISHED;
		
	}

	/**
	 * Links (assigns) the work order to a mechanic and then changes its status
	 * to ASSIGNED
	 * @see UML_State diagrams on the problem statement document
	 * @throws IllegalStateException if
	 * 	- The work order is not in OPEN status, or
	 *  - The work order is already linked with another mechanic
	 */
	public void assignTo(Mechanic mechanic) {

		if(this.status != WorkOrderStatus.OPEN) {
			throw new IllegalStateException("Error, the workorder is not "
					+ "open");
		}
		
		if(this.mechanic != null) {
			throw new IllegalStateException("Error, the workorder is already "
					+ "linked with another mechanic");
		}
		
		Associations.Assign.link(mechanic, this);
		
		this.status = WorkOrderStatus.ASSIGNED;
	}

	/**
	 * Unlinks (deassigns) the work order and the mechanic and then changes
	 * its status back to OPEN
	 * @see UML_State diagrams on the problem statement document
	 * @throws IllegalStateException if
	 * 	- The work order is not in ASSIGNED status
	 */
	public void desassign() {

		if(this.status != WorkOrderStatus.ASSIGNED) {
			throw new IllegalStateException("Error, the workorder is not "
					+ "assigned");
		}
		
		Associations.Assign.unlink(mechanic, this);
		
		this.status = WorkOrderStatus.OPEN;
	}

	/**
	 * In order to assign a work order to another mechanic is first have to
	 * be moved back to OPEN state and unlinked from the previous mechanic.
	 * @see UML_State diagrams on the problem statement document
	 * @throws IllegalStateException if
	 * 	- The work order is not in FINISHED status
	 */
	public void reopen() {

		if(this.status != WorkOrderStatus.FINISHED) {
			throw new IllegalStateException("Error, the workorder is not "
					+ "finished");
		}
		
		Associations.Assign.unlink(mechanic, this);
		
		this.status = WorkOrderStatus.OPEN;
	}

	public Set<Intervention> getInterventions() {
		return new HashSet<>( interventions );
	}

	Set<Intervention> _getInterventions() {
		return interventions;
	}

	void _setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	void _setMechanic(Mechanic mechanic) {
		this.mechanic = mechanic;
	}

	void _setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}

	public double getAmount() {
		return amount;
	}

	public WorkOrderStatus getStatus() {
		return status;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public Mechanic getMechanic() {
		return mechanic;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public boolean isFinished() {
		return status == WorkOrderStatus.FINISHED;
	}

	public boolean isInvoiced() {
		return status == WorkOrderStatus.INVOICED;
	}
	
	public boolean canBeUsedForVoucher() {

		if(usedForVoucher) {
			return false;
		}

		if(isInvoiced() && invoice.isSettled()) {
			return true;
		}

		return false;
	}

	public void markAsUsedForVoucher() {
		this.usedForVoucher = true;
	}
	
}
