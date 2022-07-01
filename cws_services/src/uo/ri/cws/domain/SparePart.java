package uo.ri.cws.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import alb.util.assertion.ArgumentChecks;
import uo.ri.cws.domain.base.BaseEntity;

@Entity
@Table(name = "TSpareparts")
public class SparePart extends BaseEntity{
	// natural attributes
	@Column(unique = true) private String code;
	private String description;
	private double price;

	// accidental attributes
	
	// client y vehicle type, tambien son transient
	@OneToMany(mappedBy = "sparePart") private Set<Substitution> substitutions = new HashSet<>();

	SparePart(){}
	
	public SparePart(String code) {
		ArgumentChecks.isNotEmpty(code);
		this.code = code;
	}


	public SparePart(String code, String description, double price) {
		this(code);
		ArgumentChecks.isNotEmpty(code);
		ArgumentChecks.isTrue(price >= 0);
		
		this.description = description;
		this.price = price;
	}


	public String getCode() {
		return code;
	}


	public String getDescription() {
		return description;
	}


	public double getPrice() {
		return price;
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
		SparePart other = (SparePart) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "SparePart [code=" + code + ", description=" + description
				+ ", price=" + price + "]";
	}


	public Set<Substitution> getSustitutions() {
		return new HashSet<>( substitutions );
	}

	Set<Substitution> _getSubstitutions() {
		return substitutions;
	}

	

}
