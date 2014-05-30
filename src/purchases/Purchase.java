package purchases;

public class Purchase {

	private int id;
	private String description;
	private String type;
	private int quantity;
	private String unit;
	private float cost;
	private PurchaseSet purchaseSet;
	
	public Purchase() {
	}
	
	public Purchase(String description, String type, int quantity, String unit, float cost) {
		this.description = description;
		this.type = type;
		this.quantity = quantity;
		this.unit = unit;
		this.cost = cost;
	}

	public void update(Purchase reference) {
		this.description = reference.description;
		this.type = reference.type;
		this.quantity = reference.quantity;
		this.unit = reference.unit;
		this.cost = reference.cost;
	}
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getUnit() {
		return this.unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public float getCost() {
		return this.cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}
	
	public PurchaseSet getPurchaseSet() {
		return this.purchaseSet;
	}

	public void setPurchaseSet(PurchaseSet purchaseSet) {
		this.purchaseSet = purchaseSet;
	}
}
