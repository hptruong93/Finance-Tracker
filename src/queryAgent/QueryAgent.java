package queryAgent;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import utilities.Log;

public abstract class QueryAgent<T> {
	
	private static final SessionFactory factory;
	
	static {
		try {
			Configuration configuration = new Configuration();
			configuration.configure();
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
			factory = configuration.buildSessionFactory(serviceRegistry);
		} catch (Throwable ex) {
			Log.exception(ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	/**
	 * Close the factory facilitating all managers. This must be done before the
	 * system terminates.
	 */
	public static void closeFactory() {
		if (factory != null) {
			factory.close();
		}
	}
	
	public T query() {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			T output = queryActivity(session);
			tx.commit();
			return output;
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
			Log.exception(e);
			return null;
		} finally {
			session.close();
		}
	}
	
	public abstract T queryActivity(Session session);
}
