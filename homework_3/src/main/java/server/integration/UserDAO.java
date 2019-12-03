package server.integration;

import common.Credentials;
import server.model.User;
import javax.persistence.*;

/**
 * The DAO for handling the Users database.
 */
public class UserDAO {
    private final EntityManagerFactory entityManagerFactory;
    private final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<>();

    public UserDAO(){
        entityManagerFactory = Persistence.createEntityManagerFactory("homework3PersistentUnit");
    }

    /**
     *  Registers a user.
     * @param user (USER) the user to be registered
     */
    public void register(User user){
        EntityManager entityManager = beginTransaction();
        entityManager.persist(user);
        commitTransaction();
    }

    /**
     *  Unregisters a user
     * @param user (USER) the user to be removed
     */
    public void unregister(User user){
        EntityManager entityManager = beginTransaction();
        User newUser = userExists(user.getUserName());
        entityManager.remove(entityManager.merge(newUser));
        entityManager.flush();
        commitTransaction();
        entityManager.close();
    }

    /**
     *  Checks if the user exists and returns the user object from database
     * @param credentials (CREDENTIALS) for the user
     * @return (USER) the state of user
     */
    public User login(Credentials credentials){
        EntityManager entityManager = openEntityManager();
        User user;
        Query query = entityManager.createQuery("SELECT user FROM Users user WHERE user.username=:username AND user.password=:password");
        query.setParameter("username", credentials.getUsername());
        query.setParameter("password", credentials.getPassword());
        try {
            user = (User) query.getSingleResult();
        }catch (NoResultException exception){
            user = null;
        }
        entityManager.close();
        return user;
    }

    /**
     *  Checks if the given username exists
     * @param username (STRING) the username of user
     * @return (USER) of the given username
     */
    public User userExists(String username){
        EntityManager entityManager = openEntityManager();
        User user;
        Query query = entityManager.createQuery("SELECT user FROM Users user WHERE user.username=:username");
        query.setParameter("username", username);
        try {
            user = (User) query.getSingleResult();
        }catch (NoResultException exception){
            user = null;
        }
        entityManager.close();
        return user;
    }

    public EntityManager openEntityManager(){
        return entityManagerFactory.createEntityManager();
    }

    private EntityManager beginTransaction() {
        EntityManager entityManager = openEntityManager();
        threadLocalEntityManager.set(entityManager);
        EntityTransaction transaction = entityManager.getTransaction();
        if (!transaction.isActive()) transaction.begin();
        return entityManager;
    }

    private void commitTransaction() {
        threadLocalEntityManager.get().getTransaction().commit();
    }
}
