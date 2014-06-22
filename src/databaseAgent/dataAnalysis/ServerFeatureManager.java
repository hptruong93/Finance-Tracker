package databaseAgent.dataAnalysis;

import java.util.ArrayList;
import java.util.List;

import utilities.IJsonable;
import utilities.functional.Filter;
import utilities.functional.Mapper;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

public class ServerFeatureManager implements FeatureManager, IJsonable {
	
	List<Feature> features;
	
	public ServerFeatureManager() {
		this.features = new ArrayList<Feature>();
	}
	
	@Override
	public int add(Feature feature) {
		this.features.add(feature);
		return features.size() - 1;
	}

	@Override
	public Feature remove(int index) {
		return this.features.remove(index);
	}

	@Override
	public boolean remove(Feature feature) {
		return this.features.remove(feature);
	}

	@Override
	public void clear() {
		this.features.clear();
	}
	
	@Override
	public Feature get(int index) {
		return features.get(index);
	}

	@Override
	public List<String> getNames() {
		return new Mapper<Feature, String>() {
			@Override
			public String map(Feature input) {
				return input.getName();
			}}.map(features);
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.array(new Mapper<Feature, JsonRootNode>(){
			@Override
			public JsonRootNode map(Feature input) {
				return input.jsonize();
			}}.map(new Filter<Feature>(){
				@Override
				public boolean filter(Feature item) {
					return item != null;
				}}.filter(features)));
	}
}
