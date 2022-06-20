package com.JDR.Vacunassist.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.JDR.Vacunassist.Dto.PacienteDTO;
import com.JDR.Vacunassist.Dto.PacienteRequest;
import com.JDR.Vacunassist.Dto.VacunadorDTO;
import com.JDR.Vacunassist.Dto.ValidarPaciente;
import com.JDR.Vacunassist.Dto.ValidarVacunador;
import com.JDR.Vacunassist.Excepciones.ResourceNotFoundException;
import com.JDR.Vacunassist.Repository.PacienteRepository;
import com.JDR.Vacunassist.Service.PacienteService;

@RestController
@CrossOrigin
public class PacienteController {

	@Autowired
	PacienteService pacienteService;
	
	@Autowired
	PacienteRepository pacienteRepository;
	
	@GetMapping("/getPacientes")
	public List<PacienteDTO> getPacientes(){
		return pacienteService.getPacientes();
	}
	
	@GetMapping("/getPacienteByDni/{dni}")
	public List<PacienteDTO> getPacientePorDni(@PathVariable(name="dni") Integer dni) throws ResourceNotFoundException{
		return pacienteService.devolverPacientePorDni(dni);
	}
	
	@GetMapping("/getPacientesEnZona")
	public List<PacienteDTO> getPacientesEnZona(@RequestParam("zonaId") Integer zonaId){
		return pacienteService.devolverPacientesEnZona(zonaId);
	}
	
	@GetMapping("/getDnisPacientes")
	public List<Integer> getDnisPacientes(){
		return pacienteService.getDnisPacientes();
	}
	
	@GetMapping("/getEmailsPacientes")
	public List<String> getEmailsPacientes(){
		return pacienteService.getEmailsPacientes();
	}
	
	// THIS FIRST FOR LOGIN
	@PostMapping("/validarPacienteBooleanPost")
	public ResponseEntity<Boolean> validarPacienteBooleanPost(@RequestBody ValidarPaciente validarPaciente) throws ResourceNotFoundException{
		return ResponseEntity.ok(pacienteService.validarPacienteBoolean(validarPaciente));
	}
	
	// THIS SECOND FOR LOGIN 
	@PostMapping("/validarPacientePost")
	public ResponseEntity<PacienteDTO> validarPacientePost(@RequestBody ValidarPaciente validarPaciente) throws ResourceNotFoundException{
		return ResponseEntity.ok(pacienteService.validarPacienteConCodigo(validarPaciente));
	}
	
	@PostMapping("/cargarPaciente")
	public ResponseEntity<Integer> cargarPaciente(@RequestBody PacienteRequest pacienteRequest ){
		return ResponseEntity.ok(pacienteService.cargarPaciente(pacienteRequest));
	}
	
}
