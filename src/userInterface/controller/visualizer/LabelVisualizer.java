package userInterface.controller.visualizer;

import java.util.List;

import javafx.scene.control.Label;
import userInterface.controller.QueryController.QueryResult;
import utilities.RecursivePrinter;

public class LabelVisualizer implements IDataVisualizer {

	private Label tool;
	
	public LabelVisualizer(Label tool) {
		this.tool = tool;
	}
	
	@Override
	public void visualize(QueryResult result) {
		List<?> temp = (List<?>) result.getResult();
		tool.setText(new RecursivePrinter(true).print(temp));
	}
}
