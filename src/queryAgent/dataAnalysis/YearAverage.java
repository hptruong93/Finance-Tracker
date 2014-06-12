package queryAgent.dataAnalysis;

import utilities.DateUtility;
import argo.jdom.JsonNode;

public class YearAverage extends Feature {
	public YearAverage() {
		super();
		loadConfig(null);
	}
	
	@Override
	public void loadConfig(JsonNode node) {
		name = "Year average";
		description = "Get the average cost of the year";
		
		fields.add("AVG(cost)");
		criteria.add(builder.buildConstraint("YEAR(date)", "EQUAL", "" + DateUtility.getThisYear()));
	}
}
