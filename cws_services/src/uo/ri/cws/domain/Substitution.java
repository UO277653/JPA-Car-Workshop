package uo.ri.cws.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import alb.util.assertion.ArgumentChecks;
import uo.ri.cws.domain.base.BaseEntity;

@Entity
@Table(name = "TSubstitutions",
		uniqueConstraints = {
		@UniqueConstraint(columnNames = {"SPAREPART_ID", "INTERVENTION_ID"}
		)
})
public class Substitution extends BaseEntity{
	// natural attributes
	private int quantity;

	// accidental attributes
	@ManyToOne private SparePart sparePart;
	@ManyToOne private Intervention intervention;
	
	Substitution(){}

	public Substitution(SparePart sparePart, Intervention intervention) {
		ArgumentChecks.isNotNull(sparePart);
		ArgumentChecks.isNotNull(intervention);
		
		Associations.Sustitute.link(sparePart, this, intervention);
		
		// this.sparePart = sparePart;
		// this.intervention = intervention;
	}

	public Substitution(SparePart sparePart, Intervention intervention,
			int quantity) {
		this(sparePart, intervention);
		
		ArgumentChecks.isTrue(quantity > 0);
		this.quantity = quantity;
	}

	void _setSparePart(SparePart sparePart) {
		this.sparePart = sparePart;
	}

	void _setIntervention(Intervention intervention) {
		this.intervention = intervention;
	}

	public int getQuantity() {
		return quantity;
	}

	public SparePart getSparePart() {
		return sparePart;
	}

	public Intervention getIntervention() {
		return intervention;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((intervention == null) ? 0 : intervention.hashCode());
		result = prime * result
				+ ((sparePart == null) ? 0 : sparePart.hashCode());
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
		Substitution other = (Substitution) obj;
		if (intervention == null) {
			if (other.intervention != null)
				return false;
		} else if (!intervention.equals(other.intervention))
			return false;
		if (sparePart == null) {
			if (other.sparePart != null)
				return false;
		} else if (!sparePart.equals(other.sparePart))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Substitution [sparePart=" + sparePart + ", intervention="
				+ intervention + "]";
	}
	
	public double getAmount() {
		return intervention.getAmount();
	}

}
