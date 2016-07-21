package com.amazon.db;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
@Named
public class GenericDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@PersistenceContext
	private EntityManager entityManager;

	public <T> T findById(T t, UUID uuid) {
		return (T) this.entityManager.find(t.getClass(), uuid);
	}

	public <T> void delete(T t) {
		this.entityManager.remove(t);
	}

	public <T> T update(T t) {
		return (T) this.entityManager.merge(t);
	}

	public <T> void insert(T t) {
		this.entityManager.persist(t);
	}

	public <T> List<T> findAll(Class clss) {
		return entityManager.createQuery("Select entity FROM " + clss.getSimpleName() + " entity").getResultList();
	}
}
