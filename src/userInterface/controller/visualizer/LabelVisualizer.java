package userInterface.controller.visualizer;

import java.util.ArrayList;

import javafx.scene.control.Label;
import utilities.RecursivePrinter;

public class LabelVisualizer implements IDataVisualizer {

	private Label tool;
	
	public LabelVisualizer(Label tool) {
		this.tool = tool;
	}
	
	@Override
	public void visualize(Object data) {
		ArrayList<?> temp = (ArrayList<?>) data;
		tool.setText(new RecursivePrinter(true).print(temp));
	}
}
