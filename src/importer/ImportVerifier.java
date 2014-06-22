package importer;

import java.sql.Date;
import java.util.Calendar;

import org.apache.commons.lang3.time.DateUtils;

import purchases.Type;
import utilities.DateUtility;

public class ImportVerifier {

	private Date startDate;

	public ImportVerifier() {
		Calendar today = Calendar.getInstance();
		today.clear(Calendar.HOUR);
		today.clear(Calendar.MINUTE);
		today.clear(Calendar.SECOND);
		startDate = new Date(today.getTimeInMillis());
	}
	
	public ImportVerifier(String startDate) {
		this.startDate = DateUtility.parseSQLDate(startDate);
		if (this.startDate == null) {
			throw new IllegalArgumentException("Invalid input date");
		}
	}

	/**
	 * Verify the purchase information Order of input parameters LOCATION, DATE,
	 * DESCRIPTION, TYPE, QUANTITY, UNIT, COST
	 * 
	 * @param parameters
	 * @return if the purchase information is valid to be added
	 */
	public boolean verifyPurchaseSet(String... parameters) {
		final int LOCATION = 0;
		final int DATE = 1;
		final int DESCRIPTION = 2;
		final int TYPE = 3;
		final int QUANTITY = 4;
		final int UNIT = 5;
		final int COST = 6;

		boolean output = true;

		for (int i = 0; i < parameters.length; i++) {
			switch (i) {
			case LOCATION:
				output &= verifyLocation(parameters[i]);
				break;
			case DATE:
				output &= verifyDate(parameters[i]);
				break;
			case DESCRIPTION:
				output &= true;
				break;
			case TYPE:
				output &= verifyType(parameters[i]);
				break;
			case QUANTITY:
				output &= verifyQuantity(parameters[i]);
				break;
			case UNIT:
				output &= verifyUnit(parameters[i]);
				break;
			case COST:
				output &= verifyCost(parameters[i]);
				break;
			default:
				break;
			}
		}
		return output;
	}

	public boolean verifyPurchase(String... strings) {
		String[] pipe = new String[strings.length + 2];
		for (int i = 0; i < strings.length; i++) {
			pipe[i + 2] = strings[i];
		}
		pipe[0] = "s";
		pipe[1] = "16/04/8192";
		return verifyPurchaseSet(pipe);
	}

	public boolean verifyLocation(String location) {
		return true;
	}

	public boolean verifyDate(String date) {
		Date adding = DateUtility.parseSQLDate(date);
		return DateUtils.truncatedCompareTo(adding, startDate, Calendar.MONTH) >= 0;
	}

	public boolean verifyType(String type) {
		return Type.PURCHASE_TYPES.contains(type);
	}

	public boolean verifyQuantity(String quantity) {
		try {
			Float.parseFloat(quantity);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}

	public boolean verifyUnit(String unit) {
		return Type.UNIT_TYPES.contains(unit);
	}

	public boolean verifyCost(String cost) {
		try {
			Float.parseFloat(cost);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}
}
