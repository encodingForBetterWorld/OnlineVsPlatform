package soft.chess.common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import soft.chess.dao.HibernateSessionFactory;
import soft.chess.domain.Announcement;
import soft.chess.domain.Player;
import soft.chess.domain.RoomBean;


public class BaseDao {

	// query
	protected Object queryObject(String hql) {
		Session session = HibernateSessionFactory.getSession();
		Query query = session.createQuery(hql);
		Object object = query.uniqueResult();
		return object;
	}

	protected <T>List<T> queryList(String hql) {
		Session session = HibernateSessionFactory.getSession();
		Query query = session.createQuery(hql);
		List<T> list = (List<T>)query.list();
		return list;
	}
	protected <T>Set<T> querySet(String hql) {
		Session session = HibernateSessionFactory.getSession();
		Query query = session.createQuery(hql);
		Set<T> set = (Set<T>)query.list();
		return set;
	}
	//
	public static void main(String[] args) {
		BaseDao dao = new BaseDao();
		Set<Player> players=new HashSet<>();
		RoomBean playRoom=new RoomBean();
		Player play=(Player) dao.queryList("from Player").get(0);
//		Announcement ann=new Announcement();
//		ann.setContent("this is a test");
//		ann.setCreater(play);
//		ann.setCreatetime(System.currentTimeMillis());
//		dao.saveObject(ann);
//		players.add(play);	
//		playRoom.setCreater(play);
//		playRoom.setJoiner(play);
//		playRoom.setAudiences(players);
//		playRoom.setCreatetime(System.currentTimeMillis());
//		dao.saveOrUpdateObject(playRoom);
//		dao.saveObject(play);
//		dao.queryList("from TbAccessionForm");
	}

	// filter
	public List filterSet(Set set, String hql) {
		Session session = HibernateSessionFactory.getSession();
		Query query = session.createFilter(set, hql);
		List list = query.list();
		return list;
	}

	// save
	public boolean saveObject(Object obj) {
		boolean isSave = true;
		Session session = HibernateSessionFactory.getSession();
		Transaction tr = session.beginTransaction();
		try {
			session.save(obj);
			tr.commit();
		} catch (HibernateException e) {
			isSave = false;
			e.printStackTrace();
		}
		return isSave;
	}
	//查询并返回插入后的对象的ID
	public long saveAndGetId(BaseEntity obj) {
		long pid=-1;
		Session session = HibernateSessionFactory.getSession();
		Transaction tr = session.beginTransaction();
		try {
			session.save(obj);
			tr.commit();
			pid=obj.getId();
		} catch (HibernateException e) {
			e.printStackTrace();
		}
		return pid;
	}
	// update
	public boolean updateObject(Object obj) {
		boolean isUpdate = true;
		Session session = HibernateSessionFactory.getSession();
		Transaction tr = session.beginTransaction();
		try {
			session.update(obj);
			tr.commit();
		} catch (HibernateException e) {
			isUpdate = false;
			tr.rollback();
			e.printStackTrace();
		}
		return isUpdate;
	}

	// saveOrUpdate
	public boolean saveOrUpdateObject(Object obj) {
		boolean isUpdate = true;
		Session session = HibernateSessionFactory.getSession();
		Transaction tr = session.beginTransaction();
		try {
			session.saveOrUpdate(obj);
			tr.commit();
		} catch (HibernateException e) {
			isUpdate = false;
			tr.rollback();
			e.printStackTrace();
		}
		return isUpdate;
	}

	// delete
	public boolean deleteObject(Object obj) {
		boolean isDelete = true;
		Session session = HibernateSessionFactory.getSession();
		Transaction tr = session.beginTransaction();
		try {
			session.delete(obj);
			tr.commit();
		} catch (HibernateException e) {
			isDelete = false;
			tr.rollback();
			e.printStackTrace();
		}
		return isDelete;
	}

	// deleteOfBatch
	public boolean deleteOfBatch(String hql) {
		boolean isDelete = true;
		Session session = HibernateSessionFactory.getSession();
		Transaction tr = session.beginTransaction();
		try {
			Query query = session.createQuery(hql);
			query.executeUpdate();
			tr.commit();
		} catch (HibernateException e) {
			isDelete = false;
			tr.rollback();
			e.printStackTrace();
		}
		return isDelete;
	}

}
