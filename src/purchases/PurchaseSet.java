package purchases;

import java.util.Date;
import java.util.Set;

public class PurchaseSet {
	private int id;
	private String location;
	private Date date;
	private Set<Purchase> purchases;
	
	public PurchaseSet() {}
	
	public PurchaseSet(String location, Date date, Set<Purchase> purchases) {
		this.location = location;
		this.date = date;
		this.purchases = purchases;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getLocation() {
		return this.location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public Date getDate() {
		return this.date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public void setPurchases(Set<Purchase> purchases) {
		this.purchases = purchases;
	}
	
	public Set<Purchase> getPurchases() {
		return this.purchases;
	}
}
