package databaseAgent.dataAnalysis;

import java.util.List;

import argo.jdom.JsonRootNode;

public interface FeatureManager {
	public abstract int add(Feature feature);
	public abstract Feature remove(int index);
	public abstract boolean remove(Feature feature);
	public abstract void clear();
	
	public abstract Feature get(int index);
	public abstract List<String> getNames();
	public abstract JsonRootNode jsonize();
}
