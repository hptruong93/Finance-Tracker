package userInterface.controller;

import userInterface.ConnectionManager;
import databaseAgent.DataManager;
import databaseAgent.QueryManager;
import databaseAgent.ServerDataManager;
import databaseAgent.ServerQueryManager;
import databaseAgent.queryBuilder.QueryBuilder;
import databaseAgent.queryBuilder.TranslatorFactory;

public class DataController {
	protected QueryManager queryManager;
	protected DataManager dataManager;
	protected QueryBuilder queryBuilder;
	protected ConnectionManager connectionManager;
	
	// Private constructor prevents instantiation from other classes
	private DataController() {
		queryManager = new ServerQueryManager();
		dataManager = new ServerDataManager();
		queryBuilder = new QueryBuilder(TranslatorFactory.getTranslator(TranslatorFactory.FEATURED_TRANSLATOR));
		connectionManager = new ConnectionManager();
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
	
	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}
}
