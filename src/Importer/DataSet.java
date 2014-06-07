package Importer;

import java.util.HashSet;
import java.util.Iterator;

public class DataSet {
	private final HashSet<DataUnit> items;
	private double totalCost;
	private double totalSubCost;
	private boolean concluded;
	
	//Create an empty DataSet knowing the totalCost
	public DataSet() {
		this.items = new HashSet<DataUnit>();
	}
	
	//Create DataSet with only one item
	public DataSet(DataUnit item, double cost) {
		this.items = new HashSet<DataUnit>();
		items.add(item);
		this.totalCost = cost;
	}
	
	public double getTotalSubCost() {
		if (!concluded) {
			throw new IllegalStateException("Set is not finalized!");
		}
		return totalSubCost;
	}
	
	public double getTotalCost() {
		if (!concluded) {
			throw new IllegalStateException("Set is not finalized!");
		}
		return totalCost;
	}
	
	public boolean isConcluded() {
		return concluded;
	}
	
	public Iterator<DataUnit> getIterator() {
		return this.items.iterator();
	}
	
	public void conclude() {
		if (!concluded) {
			concluded = true;
			
			totalSubCost = 0;
			for (DataUnit current : items) {
				totalSubCost += current.getCost();
			}
			
			for (DataUnit current : items) {
				current.setCost(totalCost * (current.getCost() / totalSubCost));
			}
		} else {
			throw new IllegalStateException("Set is already finalized!");
		}
	}
	
	//Return cost proportion in fraction (in [0,1] interval) of the given item
	//Return 0 if the item does not exist in the data set
	private double proportion(String item) {
		for (DataUnit current : items) {
			if (current.getDescription().contains(item)) {
				return (current.getCost() / totalSubCost);
			}
		}
		return 0;
	}

	protected void addItem(DataUnit item) {
		if (!concluded) {
			this.items.add(item);
		} else {
			throw new IllegalStateException("Set is already finalized!");
		}
	}
	
	protected int size() {
		if (!concluded) {
			throw new IllegalStateException("Set is not finalized!");
		}
		return this.items.size();
	}

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		for (DataUnit unit : items) {
			output.append(unit.toString() + "\n");
		}
		
		return output.toString();
	}
}