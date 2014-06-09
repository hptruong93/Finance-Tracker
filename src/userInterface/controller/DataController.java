package userInterface.controller;

import purchases.DataManager;
import queryAgent.QueryManager;

public class DataController {
	protected QueryManager queryManager;
	protected DataManager dataManager;

	// Private constructor prevents instantiation from other classes
	private DataController() {
		queryManager = new QueryManager();
		dataManager = new DataManager();
	}

	/**
	 * SingletonHolder is loaded on the first execution of
	 * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
	 * not before.
	 */
	private static class DataControllerHolder {
		private static final DataController INSTANCE = new DataController();
	}

	public static DataController getInstance() {
		return DataControllerHolder.INSTANCE;
	}
}
