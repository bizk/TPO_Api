package entitys;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import modelo.Persona;

@Entity
@Table(name="personas")
public class PersonaEntity {
	@Id
	@Column(name="documento")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String documento;
	@Column(name="nombre")
	private String nombre;
	
	public String toString() {
		return new String(this.documento + " " + this.nombre);
	}
	
	public Persona toPersona() {
		return new Persona(this.documento,this.nombre);
	}
	
}
