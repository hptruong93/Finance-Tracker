package purchases;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

import queryAgent.QueryAgent;
import queryAgent.QueryBuilder;

public class DataManager {
	private static QueryBuilder queryBuilder = new QueryBuilder();

	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(2014, 5, 22, 00, 00, 00);
		Date date = cal.getTime();

		DataManager manager = new DataManager();
		 Purchase a = new Purchase("salmon", "fish", 747, "g", 9.22f);
//		 Purchase b = new Purchase("beef", "food", 463, "g", 9.28f);
		 Set<Purchase> set = new HashSet<Purchase>();
		 set.add(a);
//		 set.add(b);
		 PurchaseSet toAdd = new PurchaseSet("metro", date, set);
		 manager.addPurchaseSet(toAdd);
		try {
		} finally {
			QueryAgent.closeFactory();
		}
	}

	/**
	 * Update a purchase instance
	 * 
	 * @param updated
	 *            the instance that will be updated in the database
	 */
	public void updatePurchase(final Purchase updated) {
		QueryAgent<Boolean> update = new QueryAgent<Boolean>() {
			@Override
			public Boolean queryActivity(Session session) {
				session.update(updated);
				return true;
			}
		};
		update.query();
	}

	/**
	 * Retrieve a set of purchases from database given filtering condition
	 * 
	 * @param condition
	 *            filtering condition (HQL syntax). Empty string "" indicates
	 *            get all purchases
	 * @param parameters
	 *            parameters to fill in condition if necessary. null parameters
	 *            will be ignored
	 * @return a list of purchases from database that satisfy filtering
	 *         condition
	 */
	@SuppressWarnings("unchecked")
	public List<Purchase> getPurchases(final String condition, final List<Object> parameters, final List<Criterion> criteria) {
		QueryAgent<List<Purchase>> query = new QueryAgent<List<Purchase>>() {
			@Override
			public List<Purchase> queryActivity(Session session) {
				String query = queryBuilder.build("Purchase", new String[]{}, condition);
				Query q = queryBuilder.build(session, query, parameters, criteria, Purchase.class);
				List<Purchase> output = (List<Purchase>) (List<?>) q.list();
				return output;
			}
		};
		
		return query.query();
	}

	/**
	 * Retrieve a single instance of purchase
	 * 
	 * @param id
	 *            id of the purchase instance
	 * @return the purchase instance of given id
	 */
	public Purchase getPurchase(final int id) {
		QueryAgent<Purchase> query = new QueryAgent<Purchase>() {
			@Override
			public Purchase queryActivity(Session session) {
				Purchase output = (Purchase) session.get(Purchase.class, id);
				return output;
			}
		};
		return query.query();
	}

	/**
	 * Delete an instance of purchase from the database
	 * 
	 * @param id
	 *            id of the purchase that will be removed.
	 * @return if deletion succeeded
	 */
	public boolean deletePurchase(final int id) {
		QueryAgent<Boolean> delete = new QueryAgent<Boolean>() {
			@Override
			public Boolean queryActivity(Session session) {
				Purchase toDelete = (Purchase) session.get(Purchase.class, id);
				session.delete(toDelete);
				return true;
			}
		};
		return delete.query();
	}
	
	/**
	 * Delete an instance of purchase from the database
	 * 
	 * @param toDelete
	 *            purchase instance that will be removed from database
	 * @return if deletion succeeded
	 */
	public boolean deletePurchase(final Purchase toDelete) {
		QueryAgent<Boolean> delete = new QueryAgent<Boolean>() {
			@Override
			public Boolean queryActivity(Session session) {
				session.delete(toDelete);
				return true;
			}
		};
		return delete.query();
	}
	/************************************************************************************/
	/**
	 * Add a purchaseSet into database
	 * 
	 * @param set
	 *            a purchaseSet instance
	 * @return integer indicating the id of the instance in the database
	 */
	public int addPurchaseSet(final PurchaseSet set) {
		QueryAgent<Integer> save = new QueryAgent<Integer>() {
			@Override
			public Integer queryActivity(Session session) {
				return (Integer) session.save(set);
			}
		};
		return save.query();
	}

	/**
	 * Update a change for a purchaseSet into the database
	 * 
	 * @param updated
	 *            the purchaseSet that will be updated in the database
	 */
	public void updatePurchaseSet(final PurchaseSet updated) {
		QueryAgent<Boolean> update = new QueryAgent<Boolean>() {
			@Override
			public Boolean queryActivity(Session session) {
				session.update(updated);
				return true;
			}
		};
		update.query();
	}

	/**
	 * Retrieve a set of purchaseSet from database given filtering condition
	 * 
	 * @param condition
	 *            filtering condition (HQL syntax). Empty string "" indicates
	 *            get all purchases
	 * @param parameters
	 *            parameters to fill in condition if necessary. null parameters
	 *            will be ignored
	 * @return a list of purchaseSet from database that satisfy filtering
	 *         condition
	 */
	@SuppressWarnings("unchecked")
	public List<PurchaseSet> getPurchaseSet(final String condition, final List<Object> parameters) {
		QueryAgent<List<PurchaseSet>> query = new QueryAgent<List<PurchaseSet>>() {
			@Override
			public List<PurchaseSet> queryActivity(Session session) {
				Query query = queryBuilder.build(session, "PurchaseSet", new String[] {}, condition, parameters);
				List<PurchaseSet> output = (List<PurchaseSet>) (List<?>) query.list();
				return output;
			}
		};
		return query.query();
	}

	/**
	 * Retrieve a single instance of purchaseSet
	 * 
	 * @param id
	 *            id of the purchaseSet instance
	 * @return the purchaseSet instance of given id
	 */
	private PurchaseSet getPurchaseSet(final int id) {
		QueryAgent<PurchaseSet> query = new QueryAgent<PurchaseSet>() {
			@Override
			public PurchaseSet queryActivity(Session session) {
				PurchaseSet output = (PurchaseSet) session.get(PurchaseSet.class, id);
				Hibernate.initialize(output.getPurchases());
				return output;
			}
		};
		return query.query();
	}

	/**
	 * Delete an instance of purchase set from the database
	 * 
	 * @param id
	 *            id of the purchase set that will be removed.
	 * @return if deletion succeeded
	 */
	public boolean deletePurchaseSet(final int id) {
		QueryAgent<Boolean> delete = new QueryAgent<Boolean>() {
			@Override
			public Boolean queryActivity(Session session) {
				PurchaseSet toDelete = (PurchaseSet) session.get(PurchaseSet.class, id);
				session.delete(toDelete);
				return true;
			}
		};
		
		return delete.query();
	}
	
	/**
	 * Delete an instance of purchase set from the database
	 * 
	 * @param toDelete
	 *            instance that will be removed from database
	 * @return if deletion succeeded
	 */
	public boolean deletePurchaseSet(final PurchaseSet toDelete) {
		QueryAgent<Boolean> delete = new QueryAgent<Boolean>() {
			
			@Override
			public Boolean queryActivity(Session session) {
				session.delete(toDelete);
				return true;
			}
		};
		
		return delete.query();
	}
}
