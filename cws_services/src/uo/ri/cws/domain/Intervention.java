package uo.ri.cws.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import alb.util.assertion.ArgumentChecks;
import uo.ri.cws.domain.base.BaseEntity;

@Entity
@Table(name = "TInterventions",
		uniqueConstraints = {
		@UniqueConstraint(columnNames = {"WORKORDER_ID", "MECHANIC_ID", "DATE"}
		)
})
public class Intervention extends BaseEntity{
	// natural attributes
	private LocalDateTime date;
	private int minutes;

	// accidental attributes
	@ManyToOne private WorkOrder workOrder;
	@ManyToOne private Mechanic mechanic;
	@OneToMany(mappedBy = "intervention") private Set<Substitution> substitutions = new HashSet<>();

	Intervention(){}
	
	public Intervention(WorkOrder workOrder, Mechanic mechanic) {
		ArgumentChecks.isNotNull(mechanic);
		ArgumentChecks.isNotNull(workOrder);
		
		this.date = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
		Associations.Intervene.link(workOrder, this, mechanic);
		
		//this.workOrder = workOrder; Not valid for us
		//this.mechanic = mechanic;
	}

	public Intervention(Mechanic mechanic, WorkOrder workOrder, int minutes) {
		this(workOrder, mechanic);
		ArgumentChecks.isTrue(minutes >= 0);
		
		this.minutes = minutes;
	}
	
	public Intervention(Mechanic mechanic, WorkOrder workOrder, LocalDateTime date, int minutes) {
		this(mechanic, workOrder, minutes);
		
		ArgumentChecks.isNotNull(date);
		
		this.date = date.truncatedTo(ChronoUnit.MILLIS);
	}

	void _setWorkOrder(WorkOrder workOrder) {
		this.workOrder = workOrder;
	}

	void _setMechanic(Mechanic mechanic) {
		this.mechanic = mechanic;
	}

	public Set<Substitution> getSubstitutions() {
		return new HashSet<>( substitutions );
	}

	Set<Substitution> _getSubstitutions() {
		return substitutions;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public int getMinutes() {
		return minutes;
	}

	public WorkOrder getWorkOrder() {
		return workOrder;
	}

	public Mechanic getMechanic() {
		return mechanic;
	}
	
	public double getAmount() {
		
		double pricePerHour = workOrder.getVehicle().getVehicleType()
				.getPricePerHour();
		
		double priceVehicle = (minutes * pricePerHour)/60;
		double priceParts = 0;
		for(Substitution s: substitutions) {
			priceParts += s.getSparePart().getPrice() * s.getQuantity();
		}
		return priceVehicle + priceParts;
		
	}
	
	public void setMinutes(int i) {
		this.minutes = i;
	}

}
