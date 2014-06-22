package databaseAgent;

import purchases.Purchase;
import purchases.PurchaseSet;

public abstract class DataManager {
	
	/**
	 * Add a purchase into database. The purchase must already belongs to a purchaseSet. Otherwise
	 * exception will be thrown.
	 * @param purchase the purchase that will be added
	 * @return id of the purchase in the database
	 */
	public abstract int addPurchase(final Purchase purchase);
	
	/**
	 * Update a purchase instance
	 * 
	 * @param updated
	 *            the instance that will be updated in the database
	 */
	public abstract void updatePurchase(final Purchase updated);

	/**
	 * Retrieve a single instance of purchase
	 * 
	 * @param id
	 *            id of the purchase instance
	 * @return the purchase instance of given id
	 */
	public abstract Purchase getPurchase(final int id);

	/**
	 * Delete an instance of purchase from the database
	 * 
	 * @param id
	 *            id of the purchase that will be removed.
	 * @return if deletion succeeded
	 */
	public abstract boolean deletePurchase(final int id);
	
	/**
	 * Delete an instance of purchase from the database
	 * 
	 * @param toDelete
	 *            purchase instance that will be removed from database
	 * @return if deletion succeeded
	 */
	public abstract boolean deletePurchase(final Purchase toDelete);
	/************************************************************************************/
	/**
	 * Add a purchaseSet into database
	 * 
	 * @param set
	 *            a purchaseSet instance
	 * @return integer indicating the id of the instance in the database
	 */
	public abstract int addPurchaseSet(final PurchaseSet set);

	/**
	 * Update a change for a purchaseSet into the database
	 * 
	 * @param updated
	 *            the purchaseSet that will be updated in the database
	 */
	public abstract void updatePurchaseSet(final PurchaseSet updated);

	/**
	 * Retrieve a single instance of purchaseSet
	 * 
	 * @param id
	 *            id of the purchaseSet instance
	 * @return the purchaseSet instance of given id
	 */
	public abstract PurchaseSet getPurchaseSet(final int id);

	/**
	 * Delete an instance of purchase set from the database
	 * 
	 * @param id
	 *            id of the purchase set that will be removed.
	 * @return if deletion succeeded
	 */
	public abstract boolean deletePurchaseSet(final int id);
	
	/**
	 * Delete an instance of purchase set from the database
	 * 
	 * @param toDelete
	 *            instance that will be removed from database
	 * @return if deletion succeeded
	 */
	public abstract boolean deletePurchaseSet(final PurchaseSet toDelete);
}
