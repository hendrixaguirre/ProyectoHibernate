package ni.edu.uam.proyectohibernate.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ni.edu.uam.proyectohibernate.database.JPAUtil;
import ni.edu.uam.proyectohibernate.model.Categoria;
import ni.edu.uam.proyectohibernate.model.Libro;

import java.util.List;

public class LibroDAO {

    private Categoria obtenerCategoria(EntityManager em, Libro libro){

        //validamos que el libro no sea null
        if (libro.getCategoria()== null || libro.getCategoria().getId() == null){
            throw new RuntimeException("Error al obtener categoría");
        }
        Long categoriaId = libro.getCategoria().getId();
        Categoria categoria = em.find(Categoria.class, categoriaId);

        if(categoria == null){
            throw new RuntimeException("Error al obtener categoría");
        }
        return categoria;
    }

    public void guardar(Libro libro){
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try{
            //Iniciar la transacción en PostgreSQL
            tx.begin();

            Categoria categoria = obtenerCategoria(em, libro);
            libro.setCategoria(categoria);
            //Decirle a Hibernate que prepare la persistencia del objeto
            em.persist(libro);
            //Confirmar los cambios en la base de datos
            tx.commit();
        }catch (RuntimeException ex){
            if(tx.isActive()){
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public List<Libro> listar(){
        EntityManager em = JPAUtil.getEntityManager();
        try{
            return em.createQuery("SELECT l FROM Libro l JOIN FETCH l.categoria ORDER BY l.id", Libro.class).getResultList();
        }
        finally {
            em.close();
        }
    }

    public boolean modificar(Libro libro){
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try{
            tx.begin();
            Libro libroBD = em.find(Libro.class, libro.getId());

            if(libroBD == null){
                tx.rollback();
                return false;
            }

            Categoria categoria = obtenerCategoria(em, libro);
            libroBD.setCategoria(categoria);

            libroBD.setTitulo(libro.getTitulo());
            libroBD.setAutor(libro.getAutor());
            libroBD.setAnioPublicacion(libro.getAnioPublicacion());
            libroBD.setPrecio(libro.getPrecio());
            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            if(tx.isActive()){
                tx.rollback();
            }
            throw ex;
        }finally {
            em.close();
        }
    }

    public boolean eliminar(Long id){
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try{
            tx.begin();
            Libro libroBD = em.find(Libro.class, id);
            if(libroBD == null){
                tx.rollback();
                return false;
            }
            em.remove(libroBD);
            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            if(tx.isActive()){
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }

    }
}
