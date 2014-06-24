package userInterface.controller.visualizer;

import javafx.scene.Node;
import userInterface.controller.QueryController.QueryResult;

public interface IDataVisualizer {
	public abstract void visualize(QueryResult result);
	public abstract Node getTool();
}
