package purchases;

import java.util.Calendar;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class PurchaseSetManager {
	private static SessionFactory factory;

	public static void main(String[] args) {
		try {
			Configuration configuration = new Configuration();
		    configuration.configure();
		    ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
		            configuration.getProperties()).build();
		    factory = configuration.buildSessionFactory(serviceRegistry);
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(2014, 5, 22, 00, 00, 00);
		Date date = cal.getTime();
		
		PurchaseSetManager manager = new PurchaseSetManager();
//		Purchase a = new Purchase("fish", "food", 328, "g", 3.28f);
//		Purchase b = new Purchase("beef", "food", 463, "g", 9.28f);
//		Set<Purchase> set = new HashSet<Purchase>();
//		set.add(a);
//		set.add(b);
//		PurchaseSet toAdd = new PurchaseSet("provigo", date, set);
//		manager.addPurchaseSet(toAdd);
		manager.deletePurchaseSet(3);
	}
	
	public int addPurchaseSet(PurchaseSet set) {
		Session session = factory.openSession();
		Transaction tx = null;
		Integer id = null;
		try {
			tx = session.beginTransaction();
			id = (Integer) session.save(set);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return id;
	}
	
	public boolean deletePurchaseSet(int id) {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			PurchaseSet toDelete = (PurchaseSet) session.get(PurchaseSet.class, id);
			session.delete(toDelete);
			tx.commit();
			return true;
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
			return false;
		} finally {
			session.close();
		}
	}
}
