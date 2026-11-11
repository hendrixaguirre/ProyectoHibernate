package ni.edu.uam.proyectohibernate.dao;

import jakarta.persistence.EntityManager;
import ni.edu.uam.proyectohibernate.database.JPAUtil;
import ni.edu.uam.proyectohibernate.model.Categoria;

import java.util.List;

public class CategoriaDAO {

    public List<Categoria> listar(){
        EntityManager em = JPAUtil.getEntityManager();
        try{
            return em.createQuery("select c from Categoria c", Categoria.class).getResultList();
        }finally {
            em.close();
        }
    }


}
