package importer.fileImporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import utilities.Log;

class DataUnit {
	
	private static final HashSet<String> NA_REP = new HashSet<String>(Arrays.asList("N/A", "NA", "Not Applicable"));
	private final Date date;
	private final String description;
	private final String type;
	private final double quantity;
	private final String unit;
	private double cost;

	protected DataUnit(Date date, String description, String type, String quantity, double cost) {
		this.date = date;
		this.description = description;
		this.type = type;
		ArrayList<Object> parseQuantity = parseQuantity(quantity);
		this.quantity = (Double) parseQuantity.get(0);
		this.unit = (String) parseQuantity.get(1);
		this.cost = cost;
	}

	// Parse a string and split the quanity and unit out. Return quantity in
	// double, then unit in String
	private static ArrayList<Object> parseQuantity(String input) {
		ArrayList<Object> output = new ArrayList<Object>();
		if (input == null) {
			throw new NullPointerException("Input cannot be null");
		} else if (input.length() == 0) {
			output.add(new Double(0));
			output.add("");
			return output;
		}

		String quantity = "", unit = "";
		input = input.trim();
		for (int i = 0; i < input.length(); i++) {

			if ((input.charAt(i) <= '9' && input.charAt(i) >= '0') || (input.charAt(i) == '.')) {
				quantity += input.charAt(i);
			} else {
				unit += input.charAt(i);
			}
		}

		try {
			output.add(Double.parseDouble(quantity));
		} catch (NumberFormatException ex) {
			output.add(0d);
			if (!NA_REP.contains(input)) {
				Log.info(DataUnit.class, "Error in parsing quantity " + input);	
			}
		}

		output.add(unit);
		return output;
	}

	public Date getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}

	public String getType() {
		return type;
	}

	public double getQuantity() {
		return quantity;
	}

	public String getUnit() {
		return unit;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
	
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("Date: " + Importer.FORMAT_DATE.format(date) + "\n");
		output.append("Description: " + description + "\n");
		output.append("Type: " + type+ "\n");
		output.append("Quantity: " + quantity+ "\n");
		output.append("Unit: " + unit+ "\n");
		output.append("Cost: " + cost + "\n");
		return output.toString();
	}
}