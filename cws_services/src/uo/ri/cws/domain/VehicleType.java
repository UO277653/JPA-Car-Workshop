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
@Table(name = "TVehicleTypes")
public class VehicleType extends BaseEntity{
	// natural attributes
	@Column(unique = true) private String name;
	private double pricePerHour;

	// accidental attributes
	@OneToMany(mappedBy = "vehicleType") private Set<Vehicle> vehicles = new HashSet<>();

	VehicleType(){}
	
	public VehicleType(String name) {
		ArgumentChecks.isNotEmpty(name); 
		this.name = name;
	}
	


	public VehicleType(String name, double pricePerHour) {
		this(name);
		// ArgumentChecks.i; check Price?
		this.pricePerHour = pricePerHour;
	}



	@Override
	public String toString() {
		return "VehicleType [name=" + name + ", pricePerHour=" + pricePerHour
				+ "]";
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		VehicleType other = (VehicleType) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}



	public String getName() {
		return name;
	}



	public double getPricePerHour() {
		return pricePerHour;
	}



	public Set<Vehicle> getVehicles() {
		return new HashSet<>( vehicles );
	}

	Set<Vehicle> _getVehicles() {
		return vehicles;
	}

	

}
