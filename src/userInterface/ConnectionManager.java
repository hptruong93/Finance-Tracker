package userInterface;

import utilities.functional.Verifier;
import databaseAgent.QueryAgent;
import databaseAgent.dataAnalysis.Feature;

public class ConnectionManager {

	public static final int DATABASE = 0;
	public static final int SERVER = 1;
	public static final int FEATURE = 2;
	
	private Boolean[] connections;
	
	public ConnectionManager() {
		connections = new Boolean[3];
	}

	public boolean startConnections() {
		try {
			connections[DATABASE] = QueryAgent.openFactory();
			connections[SERVER] = true;
			return isConnected();
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean startConnections(int component) {
		try {
			if (component == SERVER) {
				connections[SERVER] = true;
			} else if (component == DATABASE) {
				connections[DATABASE] = QueryAgent.openFactory();
			} else if (component == FEATURE) {
				connections[FEATURE] = Feature.loadFeatures();
			} else {
				throw new IllegalArgumentException("Component does not exist");
			}
			return connections[component];
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean closeConnections() {
		QueryAgent.closeFactory();
		connections[DATABASE] = false;
		connections[SERVER] = false;
		connections[FEATURE] = false;
		return isDisconnected();
	}
	
	public boolean closeConnections(int component) {
		if (component == SERVER) {
			connections[SERVER] = false;
		} else if (component == DATABASE) {
			QueryAgent.closeFactory();
			connections[DATABASE] = false;
		} else if (component == FEATURE) {
			connections[FEATURE] = false;
		} else {
			throw new IllegalArgumentException("Component does not exist");
		}
		return connections[component];
	}
	
	public boolean isConnected() {
		return new Verifier<Boolean>() {
			@Override
			public boolean verifyAction(Boolean item) {
				return item;
			}
		}.verify(connections);
	}

	public boolean isConnected(int component) {
		try {
			return connections[component];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Component does not exist");
		}
	}
	
	public boolean isDisconnected() {
		return new Verifier<Boolean>() {
			@Override
			public boolean verifyAction(Boolean item) {
				return !item;
			}
		}.verify(connections);
	}
	
	public boolean isDisconnected(int component) {
		try {
			return !connections[component];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Component does not exist");
		}
	}
}