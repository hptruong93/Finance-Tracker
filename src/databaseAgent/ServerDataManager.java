package databaseAgent;

import java.util.HashSet;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import purchases.Purchase;
import purchases.PurchaseSet;

public class ServerDataManager extends DataManager {
	/**
	 * Add a purchase into database. The purchase must already belongs to a purchaseSet. Otherwise
	 * exception will be thrown.
	 * @param purchase the purchase that will be added
	 * @return id of the purchase in the database
	 */
	@Override
	public int addPurchase(final Purchase purchase) {
		if (purchase.getPurchaseSet() == null) {
			throw new IllegalArgumentException("Purchase must be in purchaseSet to be added.");
		}
		
		QueryAgent<Integer> save = new QueryAgent<Integer>() {
			@Override
			public Integer queryActivity(Session session) {
				return (Integer) session.save(purchase);
			}
		};
		return save.query();
	}
	
	/**
	 * Update a purchase instance
	 * 
	 * @param updated
	 *            the instance that will be updated in the database
	 */
	@Override
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
	 * Retrieve a single instance of purchase
	 * 
	 * @param id
	 *            id of the purchase instance
	 * @return the purchase instance of given id
	 */
	@Override
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
	@Override
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
	@Override
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
	@Override
	public int addPurchaseSet(final PurchaseSet set) {
		QueryAgent<Integer> save = new QueryAgent<Integer>() {
			@Override
			public Integer queryActivity(Session session) {
				PurchaseSet temp = new PurchaseSet(set.getLocation(), set.getDate(), new HashSet<Purchase>());
				Integer returning = (Integer) session.save(temp);
				for (Purchase p : set.getPurchases()) {
					p.setPurchaseSet(temp);
					temp.getPurchases().add(p);
					session.save(p);
				}
				
				return returning;
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
	@Override
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
	 * Retrieve a single instance of purchaseSet
	 * 
	 * @param id
	 *            id of the purchaseSet instance
	 * @return the purchaseSet instance of given id
	 */
	@Override
	public PurchaseSet getPurchaseSet(final int id) {
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
	@Override
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
	@Override
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
