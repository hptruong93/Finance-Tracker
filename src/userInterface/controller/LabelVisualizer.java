package userInterface.controller;

import java.util.ArrayList;

import javafx.scene.control.Label;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class LabelVisualizer implements IDataVisualizer {

	private Label tool;
	
	public LabelVisualizer(Label tool) {
		this.tool = tool;
	}
	
	@Override
	public void visualize(Object data) {
		ArrayList<?> temp = (ArrayList<?>) data;

		StringBuilder output = new StringBuilder();
		
		for (Object c : temp) {
			String out = ReflectionToStringBuilder.toString(c);
			output.append(out);
		}
		tool.setText(output.toString());
	}
}
