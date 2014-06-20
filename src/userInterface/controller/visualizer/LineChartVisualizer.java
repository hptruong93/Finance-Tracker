package userInterface.controller.visualizer;

import javafx.scene.chart.LineChart;
import userInterface.controller.QueryController.QueryResult;

public class LineChartVisualizer implements IDataVisualizer {

	private LineChart tool;
	
	public LineChartVisualizer(LineChart tool) {
		this.tool = tool;
	}
	
	@Override
	public void visualize(QueryResult result) {
		
	}

}
