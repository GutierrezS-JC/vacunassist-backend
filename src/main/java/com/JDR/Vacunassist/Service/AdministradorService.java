package com.JDR.Vacunassist.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.JDR.Vacunassist.Dto.AdministradorDTO;
import com.JDR.Vacunassist.Dto.PermisoDTO;
import com.JDR.Vacunassist.Dto.RolDTO;
import com.JDR.Vacunassist.Excepciones.ResourceNotFoundException;
import com.JDR.Vacunassist.Model.Administrador;
import com.JDR.Vacunassist.Model.Permiso;
import com.JDR.Vacunassist.Repository.AdministradorRepository;

@Service
public class AdministradorService {

	@Autowired 
	private AdministradorRepository administradorRepository;
	
	public AdministradorDTO devolverAdminPorId(Integer id) throws ResourceNotFoundException {
		Administrador admin = administradorRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("No se encontro un admin para este id: " + id));
		return mapearAdmin(admin);
	}
	
	public List<AdministradorDTO> devolverAdministradores() {
		List<Administrador> adminList = administradorRepository.findAll();
		List<AdministradorDTO> response = this.convertirAdmin(adminList);
		return response;		
	}

	private List<AdministradorDTO> convertirAdmin(List<Administrador> adminList) {
		return adminList.stream().map(admin -> mapearAdmin(admin)).collect(Collectors.toList());
	}

	//Convierte el objeto admin de la BD en un objeto custom DTO definido por nosotros okk
	private AdministradorDTO mapearAdmin(Administrador admin) {
		AdministradorDTO adminDTO = new AdministradorDTO();
		List<PermisoDTO> listaPermisosDTO = new ArrayList<>();
		
		for(Permiso permiso : admin.getRol().getPermisos()) {
			PermisoDTO permisoNuevo = new PermisoDTO(permiso.getId(), permiso.getNombrePermiso());
			listaPermisosDTO.add(permisoNuevo);
		}
		
		adminDTO.setId(admin.getId());
		adminDTO.setDni(admin.getDni());
		adminDTO.setEmail(admin.getEmail());
		adminDTO.setNombre(admin.getNombre());
		adminDTO.setApellido(admin.getApellido());
		adminDTO.setFechaNacimiento(admin.getFechaNacimiento());
		adminDTO.setRol(new RolDTO(admin.getRol().getId(), admin.getRol().getNombreRol(), listaPermisosDTO));
		
		return adminDTO;		
	}

	public AdministradorDTO devolverAdminPorDNI(Integer dni) throws ResourceNotFoundException {
		Administrador admin = administradorRepository.findByDni(dni);
		if(admin != null) {
			return mapearAdmin(admin);
		}
		else return null;
	}
}
