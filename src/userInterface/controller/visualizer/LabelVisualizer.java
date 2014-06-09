package userInterface.controller.visualizer;

import java.util.List;

import javafx.scene.control.Label;
import utilities.RecursivePrinter;

public class LabelVisualizer implements IDataVisualizer {

	private Label tool;
	
	public LabelVisualizer(Label tool) {
		this.tool = tool;
	}
	
	@Override
	public void visualize(Object data) {
		List<?> temp = (List<?>) data;
		tool.setText(new RecursivePrinter(true).print(temp));
	}
}
