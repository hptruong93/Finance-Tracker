package queryAgent;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

import utilities.StringUtility;
import utilities.functional.Mapper;

/**
 * Helper function to build HQL queries
 * 
 * @author HP
 * 
 */
public class QueryBuilder {

	/**
	 * Generate a join between two tables given two list of fields to compare
	 * 
	 * @param condition
	 *            join condition/field
	 * @param type
	 *            join type: INNER JOIN, LEFT JOIN, RIGHT JOIN
	 * @return String representing the join action
	 */
	public String joinCondition(Iterable<String> condition, String joiner) {
		Mapper<String, String> mapper = new Mapper<String, String>() {
			@Override
			public String map(String input) {
				if (input.length() == 0) {
					return "";
				} else if (input.startsWith("(") && input.endsWith(")")) {
					if ((input.length() - input.replace("(", "").length()) == 1) {// There
																					// is
																					// only
																					// one
																					// "("
						return input;
					}
				}
				return "(" + input + ")";
			}
		};
		return StringUtility.join(mapper.map(condition), " " + joiner + " ");
	}

	/**
	 * Join a list of condition using a joiner keyword
	 * 
	 * @param condition
	 *            list of condition
	 * @param joiner
	 *            'AND'/ 'OR'
	 * @return String representing the joint condition
	 */
	public String joinCondition(String[] condition, String joiner) {
		return joinCondition(Arrays.asList(condition), joiner);
	}

	/**
	 * Build an HQL query with parameters inserted
	 * 
	 * @param session
	 *            Hibernate session that client is using
	 * @param query
	 *            HQL query in string, with parameters
	 * @param parameters
	 *            mapping between parameters and their values. Null to specify
	 *            no parameter exists
	 * @return HQL query built using the session provided
	 */
	public Query build(Session session, String query, HashMap<String, Object> parameters) {
		Query q = session.createQuery(query);
		if (parameters != null) {
			for (String key : parameters.keySet()) {
				q.setParameter(key, parameters.get(key));
			}
		}
		return q;
	}

	/**
	 * Build an HQL query with table name, fields, conditions and parameters
	 * inserted
	 * 
	 * @param session
	 *            session Hibernate session that client is using
	 * @param tableName
	 *            name of the table that will be queried
	 * @param fields
	 *            fields that will be selected in the table. Note that 'AS'
	 *            keyword can also be inserted into the field name
	 * @param condition
	 *            query condition
	 * @param parameters
	 *            list of parameters, with the same ordering as those in query
	 *            string provided. Null to specify no parameter exists
	 * @return HQL query built using the session provided
	 */
	public Query build(Session session, String tableName, String[] fields, String condition, List<Object> parameters) {
		String queryString = this.build(tableName, fields, condition);
		return this.build(session, queryString, parameters);
	}

	/**
	 * Build an HQL query with table name, fields, conditions and parameters
	 * inserted
	 * 
	 * @param session
	 *            session Hibernate session that client is using
	 * @param tableName
	 *            name of the table that will be queried
	 * @param fields
	 *            fields that will be selected in the table. Note that 'AS'
	 *            keyword can also be inserted into the field name
	 * @param condition
	 *            query condition
	 * @param parameters
	 *            list of parameters, with the same ordering as those in query
	 *            string provided. Null to specify no parameter exists
	 * @param groupBy
	 *            GROUP BY appended at the end
	 * @param having
	 *            HAVING appended at the end. This comes before group by
	 * @return HQL query built using the session provided
	 */
	public Query build(Session session, String tableName, String[] fields, String condition, List<Object> parameters, String groupby, String having) {
		String queryString = this.build(tableName, fields, condition, groupby, having);
		return this.build(session, queryString, parameters);
	}

	/**
	 * Build an HQL query with parameters inserted
	 * 
	 * @param session
	 *            Hibernate session that client is using
	 * @param query
	 *            HQL query in string, with parameters
	 * @param parameters
	 *            list of parameters, with the same ordering as those in query
	 *            string provided. Null to specify no parameter exists
	 * @return HQL query built using the session provided
	 */
	public Query build(Session session, String query, List<Object> parameters) {
		Query q = session.createQuery(query);

		if (parameters != null) {
			int index = 0;
			Iterator<Object> it = parameters.iterator();
			while (index < parameters.size()) {
				q.setParameter(index, it.next());
				index++;
			}
		}
		return q;
	}

	/**
	 * Build an HQL query with parameters inserted
	 * 
	 * @param session
	 *            Hibernate session that client is using
	 * @param query
	 *            HQL query in string, with parameters
	 * @param parameters
	 *            list of parameters, with the same ordering as those in query
	 *            string provided. Null to specify no parameter exists
	 * @param conditions list of conditions
	 * @param reference class of the query
	 * @return HQL query built using the session provided
	 */
	public Query build(Session session, String query, List<Object> parameters, List<Criterion> conditions, Class<?> reference) {
		if (conditions != null && conditions.size() != 0) {
			Criteria cr = session.createCriteria(reference);
			for (Criterion c : conditions) {
				cr.add(c);
			}
		}
		Query q = session.createQuery(query);
		if (parameters != null) {
			int index = 0;
			Iterator<Object> it = parameters.iterator();
			while (index < parameters.size()) {
				q.setParameter(index, it.next());
				index++;
			}
		}
		return q;
	}
	
	/**
	 * Build a full HQL query
	 * 
	 * @param tableName
	 *            name of the table that will be queried by the interface
	 *            {@link joinTable}
	 * @param fields
	 *            fields that will be selected in the table. Note that 'AS'
	 *            keyword can also be inserted into the field name
	 * @param condition
	 *            query condition
	 * @return Full HQL query that can be used by QueryAgent.
	 */
	public String build(String tableName, Collection<String> fields, String condition) {
		String query;
		if (fields.size() > 0) {
			query = "SELECT " + StringUtility.join(fields, ", ") + " FROM " + tableName;
		} else {
			query = "FROM " + tableName;
		}

		if (condition.length() != 0) {
			query += " WHERE " + condition;
		}
		return query;
	}

	/**
	 * Build a full HQL query
	 * 
	 * @param tableName
	 *            name of the table that will be queried. This can be a join
	 *            table name generated by the interface {@link joinTable}
	 * @param fields
	 *            fields that will be selected in the table. Note that 'AS'
	 *            keyword can also be inserted into the field name
	 * @param condition
	 *            query condition
	 * @return Full HQL query that can be used by QueryAgent.
	 */
	public String build(String tableName, String[] fields, String condition) {
		return build(tableName, Arrays.asList(fields), condition);
	}

	/**
	 * Build a full HQL query
	 * 
	 * @param tableName
	 *            name of the table that will be queried. This can be a join
	 *            table name generated by the interface {@link joinTable}
	 * @param fields
	 *            fields that will be selected in the table. Note that 'AS'
	 *            keyword can also be inserted into the field name
	 * @param condition
	 *            query condition
	 * @param groupBy
	 *            GROUP BY appended at the end
	 * @param having
	 *            HAVING appended at the end. This comes before group by
	 * @return Full HQL query that can be used by QueryAgent.
	 */
	public String build(String tableName, Collection<String> fields, String condition, String groupBy, String having) {
		if (having.length() == 0 && groupBy.length() == 0) {
			return build(tableName, fields, condition);
		} else if (having.length() == 0) {
			return build(tableName, fields, condition) + " GROUP BY " + groupBy;
		} else if (groupBy.length() == 0) {
			return build(tableName, fields, condition) + " HAVING " + having;
		} else {
			return build(tableName, fields, condition) + " HAVING " + having + " GROUP BY " + groupBy;
		}
	}

	/**
	 * Build a full HQL query
	 * 
	 * @param tableName
	 *            name of the table that will be queried. This can be a join
	 *            table name generated by the interface {@link joinTable}
	 * @param fields
	 *            fields that will be selected in the table. Note that 'AS'
	 *            keyword can also be inserted into the field name
	 * @param condition
	 *            query condition
	 * @param groupBy
	 *            GROUP BY appended at the end
	 * @param having
	 *            HAVING appended at the end. This comes before group by
	 * @return Full HQL query that can be used by QueryAgent.
	 */
	public String build(String tableName, String[] fields, String condition, String groupBy, String having) {
		return build(tableName, Arrays.asList(fields), condition, groupBy, having);
	}
}
