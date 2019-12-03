package server.integration;

import server.model.File;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The DAO for handling file operations for the database
 */
public class FileDAO {
    private final EntityManagerFactory entityManagerFactory;
    private final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<>();

    public FileDAO(){
        entityManagerFactory = Persistence.createEntityManagerFactory("homework3PersistentUnit");
    }

    /**
     *  Returns the file of the given filename
     * @param filename (STRING) the file name
     * @return (FILE) the file of the file name
     */
    public File getFile(String filename){
        EntityManager entityManager = openEntityManager();
        Query query = entityManager.createQuery("SELECT file FROM Files file WHERE file.filename=:filename");
        query.setParameter("filename", filename);
        File file;
        try {
            file = (File) query.getSingleResult();
        } catch (NoResultException e) {
            file = null;
        }
        entityManager.close();
        return file;
    }

    /**
     *  Stored file information on the database
     * @param file (FILE) the file to be stored
     */
    public void storeFile(File file){
        EntityManager entityManager = beginTransaction();
        entityManager.persist(file);
        commitTransaction();
    }

    /**
     *  Updated file information on the database
     * @param file (FILE) the new file information
     */
    public void updateFile(File file){
        EntityManager entityManager = beginTransaction();
        entityManager.merge(file);
        commitTransaction();
    }

    /**
     *  Deletes a file from the database
     * @param file (FILE) the file to be deleted
     */
    public void removeFile(File file){
        EntityManager entityManager = beginTransaction();
        entityManager.remove(entityManager.merge(file));
        entityManager.flush();
        commitTransaction();
        entityManager.close();
    }

    /**
     *  Gets all the files registered on the database
     * @return (LIST) of files or empty array list
     */
    public List listFiles(){
        EntityManager entityManager = openEntityManager();
        Query query = entityManager.createQuery("SELECT f FROM Files f");
        List<File> files;
        try {
            files = query.getResultList();
        } catch (NoResultException e) {
            files = new ArrayList<>();
        }
        entityManager.close();
        return files;
    }

    private EntityManager openEntityManager(){
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
