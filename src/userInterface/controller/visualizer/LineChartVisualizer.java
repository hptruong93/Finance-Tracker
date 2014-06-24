package userInterface.controller.visualizer;

import java.util.Date;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Dialogs;
import userInterface.StageMaster;
import userInterface.controller.QueryController.QueryResult;
import utilities.Log;
import extfx.scene.chart.DateAxis;

public class LineChartVisualizer implements IDataVisualizer {

	@SuppressWarnings("rawtypes")
	private LineChart tool;
	
	public LineChartVisualizer() {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void visualize(QueryResult result) {
		List<Object> rows = (List<Object>) result.getResult();
		if (rows == null || rows.size() == 0) {
			tool = new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());
		} else {//Determine the type
			if (rows.size() == 1) {//We can safely assume that it is single data
				tool = new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());
				Dialogs.showErrorDialog(StageMaster.primaryStage(), "Cannot visualize only one result with line chart...");
			} else {
				try {
					if (!rows.get(0).getClass().isArray()) {//Auto indexing
						viewSingleData(result);
					} else {// First column is indexing
						viewMultipleData(result);
					}
					tool.setTitle("Query Result");
					tool.getXAxis().setAutoRanging(true);
					tool.getYAxis().setAutoRanging(true);
				} catch (Exception e) {
					Log.exception(e);
					Dialogs.showErrorDialog(StageMaster.primaryStage(), "Error while trying to visualize data. See log for more detail");
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void viewMultipleData(QueryResult result) {
		List<Object> data = (List<Object>) result.getResult();
		//Check type of the first column
		Axis x, y;
		x = getAxis(((Object[])data.get(0))[0]);

		//Using the type of the second column for y axis type
		y = getAxis(((Object[])data.get(0))[1]);
		
		tool = new LineChart(x, y);
		x.setLabel(result.getFields().get(0));
		y.setLabel("Value");

		//Creating series
		for (int i = 1; i < ((Object[])data.get(0)).length; i++) {
			XYChart.Series series = new XYChart.Series();
			series.setName(result.getFields().get(i));

			// Populating the series with data
			for (Object eachRow : data) {
				Object[] rowData = ((Object[]) eachRow);
				series.getData().add(new XYChart.Data(rowData[0], rowData[i] instanceof Number ? rowData[i] : rowData[i].toString()));
			}

			tool.getData().add(series);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void viewSingleData(QueryResult result) {
		List<Object> data = (List<Object>) result.getResult();
		//Check type
		tool = new LineChart<Number, Number>(new NumberAxis(), getAxis(data.get(0)));
		
		tool.getYAxis().setLabel(result.getFields().get(0));
		
		// Defining a series
		XYChart.Series series = new XYChart.Series();
		series.setName(result.getFields().get(0));

		// Populating the series with data
		int index = 0;
		for (Object eachRow : data) {
			series.getData().add(new XYChart.Data(++index, eachRow instanceof Number ? eachRow : eachRow.toString()));
		}

		tool.getData().add(series);
	}
	
	private Axis getAxis(Object sample) {
		if (sample instanceof Number) {
			return new NumberAxis();
		} else if (sample instanceof Date || sample instanceof java.sql.Date) {
			return new DateAxis();
		} else {
			return new CategoryAxis();
		}
	}
	
	@Override
	public Node getTool() {
		return tool;
	}
}
