package userInterface.controller.visualizer;

import userInterface.controller.QueryController.QueryResult;

public interface IDataVisualizer {
	public abstract void visualize(QueryResult result);
}
