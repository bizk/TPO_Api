package DAO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.api.modelo.Edificio;

public class EdificioDAO {
    private List<Edificio> edificios;
    
    public EdificioDAO() {
    	this.edificios = new ArrayList<Edificio>(); 
    }
    
    public List<Edificio> getAll(){
		Transaction transaction = null;
		try {
    		Session session = ConnectionUtils.getSession();
			Transaction ts = session.beginTransaction();
			ts.begin();
			//List<EdificioEntity> results = (List<EdificioEntity>) session.createCriteria(EdificioEntity.class).list();
			List<EdificioEntity> results = (List<EdificioEntity>)session.createSQLQuery("SELECT * FROM edificios").addEntity(EdificioEntity.class).list();
			this.edificios = results.stream().map(x -> toNegocio(x))
					.collect(Collectors.toCollection(ArrayList<Edificio>::new));
	        ts.commit();
	        session.close();
		} catch (Exception e) {
			System.out.println("Problema para acceder a la db");
			e.printStackTrace();
		}
		return this.edificios;
    }

    public List<EdificioView> getAllViews(){
		if (edificios.isEmpty()) getAll();
		List<EdificioView> edificiosView = edificios.stream().map(x -> x.toView())
				.collect(Collectors.toCollection(ArrayList<EdificioView>::new));
        return edificiosView;
    }
    
    public Edificio getEdificio(int codigo) {
    	Transaction ts = null;
		try {
    		Session session = ConnectionUtils.getSession();
			ts = session.beginTransaction();
			EdificioEntity edificioEntity = (EdificioEntity)session.createSQLQuery("SELECT * FROM edificios WHERE codigo = :edificio_id")
						.addEntity(EdificioEntity.class).setParameter("edificio_id", codigo).uniqueResult();
			List<UnidadEntity> unidadesEntity = edificioEntity.getUnidades();
			Edificio edificio = toNegocio(edificioEntity);
			edificio.setUnidades(
					unidadesEntity.stream().map(x->UnidadDAO.toNegocioEdificio(x, edificio)).collect(Collectors.toCollection(ArrayList<Unidad>::new))
			);
			ts.commit();
			session.close();
			return edificio;
		} catch (Exception np) {
			System.out.println("No existe un edificio para dicho codigo");
		}
		return null;
   }

//    public void modificarEdificio(){
//        Edificio edificio = edificios.stream()
//                .filter(e -> e.getCodigo() == codigo)
//                .findAny().orElse(null);
//    }

    public void eliminarEdificio(int codigo){
        Edificio edificio = edificios.stream().filter(e -> e.getCodigo() == codigo)
                .findAny().orElse(null);
        if (edificio != null) {
            edificios.remove(edificio);
        }
    }

    static EdificioEntity toEntity(Edificio edificio) {
		return new EdificioEntity(edificio.getCodigo(),
									edificio.getNombre(),
									edificio.getDireccion());
	}

    static Edificio toNegocio(EdificioEntity edificioEntity) {
		Edificio edificio = new Edificio(edificioEntity.getCodigo(),
									edificioEntity.getNombre(),
									edificioEntity.getDireccion());
		edificio.setUnidades(edificioEntity.getUnidades().stream().map(x -> UnidadDAO.toNegocioEdificio(x, edificio))
									.collect(Collectors.toCollection(ArrayList<Unidad>::new)));
		return edificio;
    }
}