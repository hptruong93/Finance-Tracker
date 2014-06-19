package userInterface.controller;

import purchases.DataManager;
import queryAgent.QueryBuilder;
import queryAgent.QueryManager;
import queryAgent.TranslatorFactory;

public class DataController {
	protected QueryManager queryManager;
	protected DataManager dataManager;
	protected QueryBuilder queryBuilder;
	
	// Private constructor prevents instantiation from other classes
	private DataController() {
		queryManager = new QueryManager();
		dataManager = new DataManager();
		queryBuilder = new QueryBuilder(TranslatorFactory.getTranslator(TranslatorFactory.FEATURED_TRANSLATOR));
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
