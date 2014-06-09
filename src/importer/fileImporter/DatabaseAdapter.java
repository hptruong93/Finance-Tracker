package importer.fileImporter;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import purchases.Purchase;
import purchases.PurchaseSet;
import utilities.functional.Mapper;

public class DatabaseAdapter {
	private static PurchaseSet convert(DataSet dataSet) {
		Date date = null;

		Set<Purchase> purchaseSet = new HashSet<Purchase>();
		Iterator<DataUnit> it = dataSet.getIterator();
		while (it.hasNext()) {
			DataUnit next = it.next();
			if (date == null) {
				date = next.getDate();
			}
			
			Purchase singlePurchase = new Purchase(next.getDescription(), next.getType(), 
					(float) next.getQuantity(), next.getUnit(), (float) next.getCost());
			purchaseSet.add(singlePurchase);
		}
		
		return new PurchaseSet("N/R", date, purchaseSet);
	}
	
	public static List<PurchaseSet> load(String filePath) {
		Importer importer = new Importer(filePath);
		Iterator<DataSet> iterator = importer.getIterator();
		
		List<PurchaseSet> output = (new Mapper<DataSet, PurchaseSet>() {

			@Override
			public PurchaseSet map(DataSet input) {
				return convert(input);
			}
			
		}).map(iterator);
		return output;
	}
}
