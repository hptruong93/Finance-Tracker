package databaseAgent;

import java.util.List;

import org.hibernate.Session;

import databaseAgent.queryComponents.RestrictionFragment;
import databaseAgent.queryComponents.TableFragment;

public abstract class QueryManager extends QueryAgent<Object> {
	@Override
	public abstract Object queryActivity(Session session);

	public abstract void addFrom(TableFragment newComer);
	
	public abstract void setFrom(TableFragment newComer);
	
	public abstract void setFrom(List<TableFragment> newComers);
	
	public abstract void setDefaultFrom();
	
	public abstract void setDefaultField();

	public abstract boolean addExplicitField(final String name);
	
	public abstract boolean addField(final String name);

	public abstract boolean addField(String... names);

	public abstract boolean removeField(int index);
	
	public abstract boolean removeField(String name);
	
	public abstract void clearFields();

	public abstract int addConstraint(RestrictionFragment condition);
	
	public abstract int addGroupBy(String groupBy);
	
	public abstract int addHaving(RestrictionFragment having);
	
	public abstract int addOrderBy(String input);
	
	public abstract boolean removeConstraint(int index);
	
	public abstract void removeAllConstraints();
	/***************************Getters and setters**********************************/

	public abstract String getFromString();
	
	public abstract List<String> getConstraintStrings();
	
	public abstract int getMaxResult();
	
	public abstract boolean setMaxResult(int maxResult);

	public abstract List<String> getFields();
	
	public abstract List<TableFragment> getFrom();

	public abstract List<String> getGroupBy();

	public abstract List<RestrictionFragment> getCriteria();

	public abstract List<RestrictionFragment> getHaving();

	public abstract List<String> getOrderBy();
}
