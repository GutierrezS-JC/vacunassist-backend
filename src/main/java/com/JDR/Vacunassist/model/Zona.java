package com.JDR.Vacunassist.Model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Zona {

	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id; 
	
	@NotNull
	@Column(unique=true)
	private String nombreZona;
	
	@OneToOne(mappedBy = "zona")
    private Vacunatorio vacunatorio;
	
	@OneToMany(mappedBy = "zona")
	private Set<VacunadorZona> vacunadores = new HashSet<>();
	
	@OneToMany(mappedBy = "zona")
	private Set<PacienteZona> pacientes = new HashSet<>();
	
	public Zona() {
		
	}
	
	public Zona(int id, String nombreZona) {
		super();
		this.id = id;
		this.nombreZona = nombreZona;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombreZona() {
		return nombreZona;
	}

	public void setNombreZona(String nombreZona) {
		this.nombreZona = nombreZona;
	}

	public Vacunatorio getVacunatorio() {
		return vacunatorio;
	}

	public void setVacunatorio(Vacunatorio vacunatorio) {
		this.vacunatorio = vacunatorio;
	}

	public Set<VacunadorZona> getVacunadores() {
		return vacunadores;
	}

	public void setVacunadores(Set<VacunadorZona> vacunadores) {
		this.vacunadores = vacunadores;
	}

	public Set<PacienteZona> getPacientes() {
		return pacientes;
	}

	public void setPacientes(Set<PacienteZona> pacientes) {
		this.pacientes = pacientes;
	}
	
}
