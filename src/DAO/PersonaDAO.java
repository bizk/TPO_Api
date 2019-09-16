package DAO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.Transaction;

import entitys.PersonaEntity;
import modelo.Edificio;
import modelo.Persona;
import utils.ConnectionUtils;

class PersonaDAO {
	private List<Persona> personas;
	private Session session;
	 
    public PersonaDAO() {
    	if (session == null) this.session = ConnectionUtils.getSession();
    }
	
	public List<Persona> getAll(){
		session.beginTransaction();

		List<PersonaEntity> results = session.createCriteria(PersonaEntity.class).list();
		//This function converts the results from entitys into a list
		this.personas = results.stream().map(x -> toNegocio(x))
				.collect(Collectors.toCollection(ArrayList<Persona>::new));

		return this.personas;
	}
	
	public Persona getById(String documento) {
		session.beginTransaction();
		try {
			PersonaEntity personaEntity = (PersonaEntity) session.get(PersonaEntity.class, documento);
			return toNegocio(personaEntity);
		} catch (Exception exception) {
			System.out.println("No existe ninguna persona con el dni: " + documento);
		}
		return null;
	}
	
	public void save(Persona persona) {
		Transaction transaction = null; 
		try {
			transaction = session.beginTransaction();
			transaction.begin();
			PersonaEntity personaEntity = new PersonaEntity(persona.getDocumento(),persona.getNombre());
			session.saveOrUpdate(personaEntity);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		}
	}
	
	public void delete(Persona persona) {
		Transaction transaction = null; 
		try {
			transaction = session.beginTransaction();
			transaction.begin();
			PersonaEntity personaEntity = new PersonaEntity(persona.getDocumento(),persona.getNombre());
			session.delete(personaEntity);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		}
	}

	static PersonaEntity toEntity(Persona usuario) {
		return new PersonaEntity(usuario.getDocumento(),
									usuario.getNombre());
	}

	static Persona toNegocio(PersonaEntity usuario) {
		return new Persona(usuario.getDocumento(),
							usuario.getNombre());
	}
}
