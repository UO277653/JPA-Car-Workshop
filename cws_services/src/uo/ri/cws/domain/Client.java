package uo.ri.cws.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import alb.util.assertion.ArgumentChecks;
import uo.ri.cws.domain.base.BaseEntity;

@Entity
@Table(name = "TClients")
public class Client extends BaseEntity{
	
	@Column(unique = true) private String dni;
	private String name = "no-name";
	private String surname = "no-surname";
	private String email = "no-email";
	private String phone = "no-phone";
	private Address address;
	
	// accidental
	@OneToMany(mappedBy = "client") private Set<Vehicle> vehicles = new HashSet<>();
	@OneToMany(mappedBy = "client") private Set<PaymentMean> paymentMeans = new HashSet<>();
	
	@Transient private Recommendation recommended; // One to one?
	@OneToMany(mappedBy = "sponsor") private Set<Recommendation> recommendations = new HashSet<>(); //
	
	public Client(){}
	
	public Client(String dni) {
		ArgumentChecks.isNotEmpty(dni);
		
		this.dni = dni;
	}

	public Client(String dni, String name, String surname) {
		this(dni);
		ArgumentChecks.isNotEmpty(name);
		ArgumentChecks.isNotEmpty(surname);
		this.name = name;
		this.surname = surname;
	}

	public String getDni() {
		return dni;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public Address getAddress() {
		return address;
	}
	
	@Override
	public String toString() {
		return "Client [dni=" + dni + ", name=" + name + ", surname=" + surname
				+ ", email=" + email + ", phone=" + phone + ", address="
				+ address + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dni == null) ? 0 : dni.hashCode());
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
		Client other = (Client) obj;
		if (dni == null) {
			if (other.dni != null)
				return false;
		} else if (!dni.equals(other.dni))
			return false;
		return true;
	}

	Set<Vehicle> _getVehicles() {
		return vehicles;
	}
	
	public Set<Vehicle> getVehicles() {
		return new HashSet<>(vehicles);
	}

	Set<PaymentMean> _getPaymentMeans() {
		return paymentMeans;
	}
	
	public Recommendation getRecommended() {
		return recommended;
	}
	
	void _setRecommended(Recommendation rec) {
		this.recommended = rec;
	}
	
	public Set<PaymentMean> getPaymentMeans() {
		return new HashSet<>(paymentMeans);
	}
	
	public Set<Recommendation> getSponsored() {
		return new HashSet<>(recommendations);
	}
	
	public Set<Recommendation> getRecommendations() {
		return new HashSet<>(recommendations);
	}
	
	Set<Recommendation> _getRecommendations() {
		return recommendations;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	public boolean eligibleForRecommendationVoucher() {
		
		if(recommendations.size() < 3) {
			return false;
		}
		
		if(getWorkOrdersAvailableForVoucher().isEmpty()) {
			return false;
		}
		
		List<Client> validReferrals = new ArrayList<Client>();
		
		for(Recommendation r: recommendations) {
			if(r.isUsedForVoucher()) {
				continue;
			}
			
			Client c = r.getRecommended();
			
			if (!c.getWorkOrdersAvailableForVoucher().isEmpty()) {
				validReferrals.add(c);
			}
		}
		
		if(validReferrals.size() >= 3) {
			return true;
		}
		
		return false;
	}

	public List<WorkOrder> getWorkOrdersAvailableForVoucher() {
		List<WorkOrder> res = new ArrayList<WorkOrder>();
		
		for(Vehicle v: vehicles) {
			for(WorkOrder w: v._getWorkOrders()) {
				if(w.canBeUsedForVoucher()) {
					res.add(w);
				}
			}
		}
		
		return res;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

}

