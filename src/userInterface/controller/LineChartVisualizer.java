package userInterface.controller;

import javafx.scene.chart.LineChart;

public class LineChartVisualizer implements IDataVisualizer {

	private LineChart tool;
	
	public LineChartVisualizer(LineChart tool) {
		this.tool = tool;
	}
	
	@Override
	public void visualize(Object data) {
		
	}

}
