package userInterface.controller;

import purchases.DataManager;
import dataAnalysis.DataQuery;

public class MainController {
	protected DataQuery dataQuery;
	protected DataManager manager;

	// Private constructor prevents instantiation from other classes
	private MainController() {
		dataQuery = new DataQuery();
		manager = new DataManager();
	}

	/**
	 * SingletonHolder is loaded on the first execution of
	 * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
	 * not before.
	 */
	private static class MainControllerHolder {
		private static final MainController INSTANCE = new MainController();
	}

	public static MainController getInstance() {
		return MainControllerHolder.INSTANCE;
	}
}
