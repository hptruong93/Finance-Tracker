package Importer;

import java.util.Iterator;

import Almonds.Parse;
import Almonds.ParseObject;

public class ParseInterface {

	private static final String APP_ID = "FbjYWRnoV8UpaElXTq8YOaAXBCdKcWSJiNMDQMvL";
	private static final String REST_KEY = "LXpFglDrVTDbaicksKpkkpHcHtz7TH3RP9WQtMxa";
	private static Integer count = 1;
	
	private static final String TABLE_NAME = "FinanceRecords";
	private static final String DATE_FIELD = "Date";
	private static final String DESCRIPTION_FIELD = "Description";
	private static final String TYPE_FIELD = "Type";
	private static final String QUANTITY_FIELD = "Quantity";
	private static final String UNIT_FIELD = "Unit";
	private static final String COST_FIELD = "Cost";
	
	public static void main(String[] args) {
		ParseInterface parse = new ParseInterface();
		Importer importer = new Importer("D:\\test.xlsx");
		Iterator<DataSet> iterator = importer.getIterator();
		while (iterator.hasNext()) {
			DataSet next = iterator.next();
			parse.upLoad(next);
		}
	}

	public ParseInterface() {
		init();
	}

	private static void init() {
		synchronized (count) {
			if (count == 1) {
				Parse.initialize(APP_ID, REST_KEY);
				count++;
			} else {
				throw new IllegalStateException("Cannot create more than 1 interface. Already initialized!");
			}
		}
	}

	public DataUnit query(DataUnit queryInfo) {
		return null;
	}
	
	public boolean upload(DataUnit unit) {
		ParseObject uploading = new ParseObject(TABLE_NAME);
		uploading.put(DATE_FIELD, unit.getDate());
		uploading.put(DESCRIPTION_FIELD, unit.getDescription());
		uploading.put(TYPE_FIELD, unit.getType());
		uploading.put(QUANTITY_FIELD, unit.getQuantity());
		uploading.put(UNIT_FIELD, unit.getUnit());
		uploading.put(COST_FIELD, unit.getCost());
		uploading.saveInBackground();
		return true;
	}
	
	public boolean upLoad(DataSet dataSet) {
		if (dataSet.isConcluded()) {
			Iterator<DataUnit> iter = dataSet.getIterator();
			while (iter.hasNext()) {
				DataUnit unit = iter.next();
				upload(unit);
			}
		} else {
			throw new IllegalStateException("Data set is not concluded!");
		}
		return false;
	}
}