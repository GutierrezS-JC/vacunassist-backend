package com.JDR.Vacunassist.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.JDR.Vacunassist.Dto.PacienteDTO;
import com.JDR.Vacunassist.Dto.PacienteRequest;
import com.JDR.Vacunassist.Dto.PermisoDTO;
import com.JDR.Vacunassist.Dto.RolDTO;
import com.JDR.Vacunassist.Dto.VacunadorDTO;
import com.JDR.Vacunassist.Dto.VacunasAnterioresRequest;
import com.JDR.Vacunassist.Dto.VacunatorioDTO;
import com.JDR.Vacunassist.Dto.ValidarPaciente;
import com.JDR.Vacunassist.Dto.ZonaDTO;
import com.JDR.Vacunassist.Model.Paciente;
import com.JDR.Vacunassist.Model.Permiso;
import com.JDR.Vacunassist.Model.Rol;
import com.JDR.Vacunassist.Model.Turno;
import com.JDR.Vacunassist.Model.Vacuna;
import com.JDR.Vacunassist.Model.Vacunador;
import com.JDR.Vacunassist.Model.Vacunatorio;
import com.JDR.Vacunassist.Model.Zona;
import com.JDR.Vacunassist.Repository.PacienteRepository;
import com.JDR.Vacunassist.Repository.RolRepository;
import com.JDR.Vacunassist.Repository.TurnoRepository;
import com.JDR.Vacunassist.Repository.VacunaRepository;
import com.JDR.Vacunassist.Repository.VacunatorioRepository;
import com.JDR.Vacunassist.Repository.ZonaRepository;

@Service
public class PacienteService {

	@Autowired
	PacienteRepository pacienteRepository;

	@Autowired
	RolRepository rolRepository;
	
	@Autowired
	ZonaRepository zonaRepository;
	
	@Autowired
	VacunatorioRepository vacunatorioRepository;
	
	@Autowired
	VacunaRepository vacunaRepository;
	
	@Autowired
	TurnoRepository turnoRepository;
	
	public List<Integer> getDnisPacientes() {
		List<Paciente> pacientes = pacienteRepository.findAll();
		List<Integer> response = new ArrayList<>();
		if(pacientes != null) {
			for(Paciente paciente : pacientes) {
				response.add(paciente.getDni());
			}
			return response;
		}
		else {
			return null;
		}
	}

	public List<String> getEmailsPacientes() {
		List<Paciente> pacientes = pacienteRepository.findAll();
		List<String> response = new ArrayList<>();
		if(pacientes!=null) {
			for(Paciente paciente : pacientes) {
				response.add(paciente.getEmail());
			}
			return response;
		}
		else {
			return null;
		}
	}
	
	public List<PacienteDTO> devolverPacientePorDni(Integer dni) {
		List<PacienteDTO> listaResponse = new ArrayList<>();
		Paciente paciente = pacienteRepository.findByDni(dni);
		if(paciente!= null) {
			PacienteDTO pacienteDto = mapearPaciente(paciente);
			listaResponse.add(pacienteDto);
			return listaResponse;
		}
		else return null;
	}
	
	public List<PacienteDTO> devolverPacientesEnZona(Integer zonaId) {
		List<Object[]> query = pacienteRepository.getPacientesEnZona(zonaId);
		List<PacienteDTO> response = new ArrayList<>();
		for(Object[] vacunDB : query) {
			Paciente pacienteBuscado = pacienteRepository.findByDni((Integer) vacunDB[1]);
			if(pacienteBuscado!= null) {
				response.add(this.mapearPaciente(pacienteBuscado));
			}
		}
		return (response.size() == 0 ?  null :  response);
	}
	
	public List<PacienteDTO> getPacientes() {
		List<Paciente> pacienteList = pacienteRepository.findAll();
		List<PacienteDTO> response = this.convertirPaciente(pacienteList);
		return response;	
	}

	private List<PacienteDTO> convertirPaciente(List<Paciente> pacienteList) {
		return pacienteList.stream().map(paciente -> mapearPaciente(paciente)).collect(Collectors.toList());
	}
	
	private PacienteDTO mapearPaciente(Paciente paciente) {
		List<PermisoDTO> listaPermisosDTO = new ArrayList<>();
		for(Permiso permiso : paciente.getRol().getPermisos()) {
			PermisoDTO permisoNuevo = new PermisoDTO(permiso.getId(), permiso.getNombrePermiso());
			listaPermisosDTO.add(permisoNuevo);
		}
		
		VacunatorioDTO vacunatorioDTO = new VacunatorioDTO(paciente.getZona().getVacunatorio().getId(), paciente.getZona().getVacunatorio().getNombre());
		ZonaDTO zonaNueva = new ZonaDTO(paciente.getZona().getId(), paciente.getZona().getNombreZona(), vacunatorioDTO);
		
		PacienteDTO pacienteDTO = new PacienteDTO(paciente.getId(), paciente.getDni(), paciente.getEmail(), paciente.getNombre(), paciente.getApellido(),
				paciente.getFechaNacimiento(), paciente.getEsDeRiesgo(), new RolDTO(paciente.getRol().getId(), paciente.getRol().getNombreRol(), listaPermisosDTO),
				zonaNueva);
		
		return pacienteDTO;
	}
	
	public Boolean validarPacienteBoolean(ValidarPaciente validarPaciente) {
		Paciente pacienteBuscado = pacienteRepository.findByEmailAndPassword(validarPaciente.getEmail(), validarPaciente.getPassword());
		if(pacienteBuscado != null) {
			return true;
		}
		else {			
			return false;
		}
	}
	
	public PacienteDTO validarPacienteConCodigo(ValidarPaciente validarPaciente) {
		Paciente pacienteBuscado = pacienteRepository.findByEmailAndPasswordAndCodigo(validarPaciente.getEmail(), validarPaciente.getPassword(), validarPaciente.getCodigo());
		if(pacienteBuscado != null) {
			PacienteDTO pacienteDTO = this.mapearPaciente(pacienteBuscado);
			return pacienteDTO;
		}
		else {			
			return null;
		}
	}
	
	private LocalDate convertirALocalDate(Date dateToConvert) {
	    return LocalDate.ofInstant(
	      dateToConvert.toInstant(), ZoneId.systemDefault());
	}
	
    private String compareDatesStr(String date1, String date2) {
        if (date1 == null)
            if (date2 == null)
                return "not seen yet";
            else
                return date2;
        else if (date2 == null)
            return date1;
        else
            return date1.compareTo(date2) > 0 ? date1 : date2;
    }
	
	public Integer cargarPaciente(PacienteRequest pacienteRequest) {
		
		Paciente pacienteExiste = pacienteRepository.findByDni(pacienteRequest.getDni());
		if(pacienteExiste == null) {
			if(!pacienteRequest.getNombre().trim().isBlank() && !pacienteRequest.getApellido().trim().isBlank() && !pacienteRequest.getEmail().isBlank() 
					&& !pacienteRequest.getPassword().isBlank() && !pacienteRequest.getZonaId().toString().isBlank() 
					&& !pacienteRequest.getZonaId().toString().isBlank()) {
				
				Rol rolPaciente = rolRepository.findById(3).get();
				Zona zonaPaciente = zonaRepository.findById(pacienteRequest.getZonaId()).get();
				
				boolean seVacunoPreviamenteCovid = false;
				boolean seVacunoPreviamenteGripe = false;
				LocalDate fechaVacunaGripeAnterior = null;
				LocalDate fechaVacunaCovidAnterior = null;
				String fechaMaxCovid =  "1900-01-01";
				
				Integer codigoUnico = pacienteRepository.getUltimoCodigoCreado();
				codigoUnico = codigoUnico + 1;
				
				// Si es un viejisto aunque no haya seleccionado la opcion de que es de riesgo aca lo seteamos como TRUE //
				if(convertirALocalDate(new Date()).getYear() - pacienteRequest.getFechaNacimiento().getYear() >= 60 ){
					pacienteRequest.setEsRiesgo(true);
				}
				
				Paciente paciente = new Paciente(pacienteRequest.getDni(), pacienteRequest.getEmail(), pacienteRequest.getPassword(), codigoUnico,
						pacienteRequest.getNombre(), pacienteRequest.getApellido(), pacienteRequest.getFechaNacimiento(), pacienteRequest.getEsRiesgo(), rolPaciente, zonaPaciente);
				
				Paciente nuevoPaciente = pacienteRepository.saveAndFlush(paciente);
	
				if(!pacienteRequest.getListaVacunasAnteriores().isEmpty()) {
					for(VacunasAnterioresRequest vacunaAnterior : pacienteRequest.getListaVacunasAnteriores()) {		

						Vacuna vacunaAnteriorBuscada= vacunaRepository.findById(vacunaAnterior.getVacunaId()).get();
						Vacunatorio vacunatorioAnterior = vacunatorioRepository.getVacunatorioByZona(vacunaAnterior.getZonaId());
						
						//Vemos si dentro de las vacunas previas tiene una correspondiente al COVID
						if(vacunaAnteriorBuscada.getId() == 1 || vacunaAnteriorBuscada.getId() == 2 || vacunaAnteriorBuscada.getId() == 3){
							seVacunoPreviamenteCovid = true;
							fechaMaxCovid = compareDatesStr(fechaMaxCovid, vacunaAnterior.getFechaAplicacion().toLocalDate().toString());
						}
						
						//Vemos si dentro de las vacunas previas tiene una correspondiente a la Gripe
						if(vacunaAnteriorBuscada.getId() == 4) {
							seVacunoPreviamenteGripe = true;
							fechaVacunaGripeAnterior = vacunaAnterior.getFechaAplicacion().toLocalDate();
						}
						
						System.out.println("Fecha covid setMax: " + fechaMaxCovid);
						fechaVacunaCovidAnterior = LocalDate.parse(fechaMaxCovid);
						
						Turno turnoPasado = new Turno(0, LocalDateTime.of(LocalDate.now(), LocalTime.now()), vacunaAnterior.getFechaAplicacion(), true, vacunaAnteriorBuscada ,vacunatorioAnterior, nuevoPaciente);
						turnoRepository.save(turnoPasado);
					}
					System.out.println("Fecha covid max definitiva: " + fechaMaxCovid);
				}
				
				// == EMPIEZO CONDICIONES PARA TURNOS == //
				
				// SI ES MAYOR A 60 //
				if(convertirALocalDate(new Date()).getYear() - pacienteRequest.getFechaNacimiento().getYear() >= 60 ){
					LocalDateTime fecha = LocalDateTime.of(LocalDate.now(), LocalTime.now());
					System.out.println("(GRIPE) Fecha incial - 159: " + fecha);
					Vacunatorio vacunatorioElegido = vacunatorioRepository.getVacunatorioByZona(pacienteRequest.getZonaId());
					
					if(!seVacunoPreviamenteGripe || (seVacunoPreviamenteGripe && ChronoUnit.MONTHS.between(fechaVacunaGripeAnterior, convertirALocalDate(new Date())) >= 12)) {
						fecha = fecha.plusMonths(3);
						System.out.println("(GRIPE) Fecha plus 3 meses - 164: " + fecha);
						Vacuna vacunaGripe = vacunaRepository.findByNombre("Gripe");
						
						String fechaQuery = fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
						System.out.println("(GRIPE) Fecha Query Gripe - 168: " + fechaQuery);
						
						Object ultimoTurnoFecha = pacienteRepository.getUltimoTurnoEnFecha(fechaQuery, vacunatorioElegido.getId());
						Turno nuevoTurno;
						
						// Si no existe un turno en ese dia le asigno el primero - 10 AM - pero si existe entonces le sumo 10 minutos al turno anterior
						if(ultimoTurnoFecha == null) {
							nuevoTurno = new Turno(0, LocalDateTime.of(LocalDate.now(), LocalTime.now()),
									LocalDateTime.of(fecha.getYear(), fecha.getMonth(), fecha.getDayOfMonth(), 10, 0),
									null, vacunaGripe, vacunatorioElegido, nuevoPaciente);
						} else {
							System.out.println("(GRIPE) Fecha (Hora) devuelta por BD - 179: " + ultimoTurnoFecha.toString());
							
							LocalDateTime fechaPlus = LocalDateTime.parse(fechaQuery + " " + ultimoTurnoFecha.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
							fechaPlus =  LocalDateTime.of(fechaPlus.getYear(), fechaPlus.getMonth(), fechaPlus.getDayOfMonth(), fechaPlus.getHour(), (fechaPlus.getMinute() + 10));
							
							System.out.println("(GRIPE) Fecha plus 10 minutos por turno existente - 184: " + fechaPlus);
							nuevoTurno = new Turno(0, LocalDateTime.of(LocalDate.now(), LocalTime.now()),
									fechaPlus, null, vacunaGripe, vacunatorioElegido, nuevoPaciente);
						}
						turnoRepository.saveAndFlush(nuevoTurno);
					}
					
					if(!seVacunoPreviamenteCovid || (seVacunoPreviamenteCovid && ChronoUnit.MONTHS.between(fechaVacunaCovidAnterior, convertirALocalDate(new Date())) >= 3)) {
						fecha = fecha.plusDays(7);
						System.out.println("(COVID) Fecha plus 7 dias - 193: " + fecha);
						Vacuna vacunaCovid = vacunaRepository.getVacunaCovidRandom();

						String fechaQuery = fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
						System.out.println("(COVID) Fecha Query Covid - 197: " + fechaQuery);
						
						Object ultimoTurnoFecha = pacienteRepository.getUltimoTurnoEnFecha(fechaQuery, vacunatorioElegido.getId());
						Turno nuevoTurno;
						
						// Si no existe un turno en ese dia le asigno el primero - 10 AM - pero si existe entonces le sumo 10 minutos al turno anterior
						if(ultimoTurnoFecha == null) {
							nuevoTurno = new Turno(0, LocalDateTime.of(LocalDate.now(), LocalTime.now()),
									LocalDateTime.of(fecha.getYear(), fecha.getMonth(), fecha.getDayOfMonth(), 10, 0),
									null, vacunaCovid, vacunatorioElegido, nuevoPaciente);
						} else {
							System.out.println("(COVID) Fecha devuelta por BD - 208: " + ultimoTurnoFecha.toString());
							
							LocalDateTime fechaPlus = LocalDateTime.parse(fechaQuery + " " + ultimoTurnoFecha.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
							fechaPlus =  LocalDateTime.of(fechaPlus.getYear(), fechaPlus.getMonth(), fechaPlus.getDayOfMonth(), fechaPlus.getHour(), (fechaPlus.getMinute() + 10));

							System.out.println("(COVID) Fecha plus 10 minutos por turno existente - 213: " + fechaPlus.toString());
							
							nuevoTurno = new Turno(0, LocalDateTime.of(LocalDate.now(), LocalTime.now()),
									fechaPlus, null, vacunaCovid, vacunatorioElegido, nuevoPaciente);
						}
						
						turnoRepository.saveAndFlush(nuevoTurno);
					}
//					pacienteRepository.save(nuevoPaciente);
				}
				
				// SI ES MENOR A 18 //
				if(convertirALocalDate(new Date()).getYear() - pacienteRequest.getFechaNacimiento().getYear() < 18 ){
					LocalDateTime fecha = LocalDateTime.of(LocalDate.now(), LocalTime.now());
					Vacunatorio vacunatorioElegido = vacunatorioRepository.getVacunatorioByZona(pacienteRequest.getZonaId());
					
					if(pacienteRequest.getEsRiesgo()) {
						if(!seVacunoPreviamenteGripe || (seVacunoPreviamenteGripe && ChronoUnit.MONTHS.between(fechaVacunaGripeAnterior, convertirALocalDate(new Date())) >= 12)) {
							fecha = fecha.plusMonths(3);
							Vacuna vacunaGripe = vacunaRepository.findByNombre("Gripe");
							
							String fechaQuery = fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
							System.out.println("Fecha Query: " + fechaQuery);
						
							Object ultimoTurnoFecha = pacienteRepository.getUltimoTurnoEnFecha(fechaQuery, vacunatorioElegido.getId());
							Turno nuevoTurno;
							
							// Si no existe un turno en ese dia le asigno el primero - 10 AM - pero si existe entonces le sumo 10 minutos al turno anterior
							if(ultimoTurnoFecha == null) {
								nuevoTurno = new Turno(0, LocalDateTime.of(LocalDate.now(), LocalTime.now()),
										LocalDateTime.of(fecha.getYear(), fecha.getMonth(), fecha.getDayOfMonth(), 10, 0),
										null, vacunaGripe, vacunatorioElegido, nuevoPaciente);
							} else {
								LocalDateTime fechaPlus = LocalDateTime.parse(ultimoTurnoFecha.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
								fechaPlus =  LocalDateTime.of(fechaPlus.getYear(), fechaPlus.getMonth(), fechaPlus.getDayOfMonth(), fechaPlus.getHour(), (fechaPlus.getMinute() + 10));
								nuevoTurno = new Turno(0, LocalDateTime.of(LocalDate.now(), LocalTime.now()),
										fechaPlus, null, vacunaGripe, vacunatorioElegido, nuevoPaciente);
							}
							
//							Turno nuevoTurno = new Turno(0, LocalDateTime.of(LocalDate.now(), LocalTime.now()), fecha, null, vacunaGripe, vacunatorioElegido, nuevoPaciente);
							turnoRepository.save(nuevoTurno);
						}
					}
					else {
						if(!seVacunoPreviamenteGripe || (seVacunoPreviamenteGripe && ChronoUnit.MONTHS.between(fechaVacunaGripeAnterior, convertirALocalDate(new Date())) >= 12)) {
							fecha = fecha.plusMonths(6);
							Vacuna vacunaGripe = vacunaRepository.findByNombre("Gripe");
							
							Turno nuevoTurno = new Turno(0, LocalDateTime.of(LocalDate.now(), LocalTime.now()), fecha, null, vacunaGripe, vacunatorioElegido, nuevoPaciente);
							turnoRepository.save(nuevoTurno);
						}
					}
					pacienteRepository.save(nuevoPaciente);
				}
								
				// SI ES MAYOR O TIENE 18 Y ES MENOR QUE 60 //
				if(convertirALocalDate(new Date()).getYear() - pacienteRequest.getFechaNacimiento().getYear() >= 18 &&
					convertirALocalDate(new Date()).getYear() - pacienteRequest.getFechaNacimiento().getYear()<= 59){
					
					LocalDateTime fecha = LocalDateTime.of(LocalDate.now(), LocalTime.now());
					Vacunatorio vacunatorioElegido = vacunatorioRepository.getVacunatorioByZona(pacienteRequest.getZonaId());
					
					if(pacienteRequest.getEsRiesgo()) {
						if(!seVacunoPreviamenteGripe || (seVacunoPreviamenteGripe && ChronoUnit.MONTHS.between(fechaVacunaGripeAnterior, convertirALocalDate(new Date())) >= 12)) {
							fecha = fecha.plusMonths(3);
							Vacuna vacunaGripe = vacunaRepository.findByNombre("Gripe");
							
							Turno nuevoTurno = new Turno(0, LocalDateTime.of(LocalDate.now(), LocalTime.now()), fecha, null, vacunaGripe, vacunatorioElegido, nuevoPaciente);
							turnoRepository.save(nuevoTurno);
						}
						
						if(!seVacunoPreviamenteCovid || (seVacunoPreviamenteCovid && ChronoUnit.MONTHS.between(fechaVacunaCovidAnterior, convertirALocalDate(new Date())) >= 3)) {
							fecha = fecha.plusDays(7);
							Vacuna vacunaCovid = vacunaRepository.getVacunaCovidRandom();
							
							Turno nuevoTurno = new Turno(0, LocalDateTime.of(LocalDate.now(), LocalTime.now()), fecha, null, vacunaCovid, vacunatorioElegido, nuevoPaciente);
							turnoRepository.save(nuevoTurno);
						}
					}
					else {
						if(!seVacunoPreviamenteGripe || (seVacunoPreviamenteGripe && ChronoUnit.MONTHS.between(fechaVacunaGripeAnterior, convertirALocalDate(new Date())) >= 12)) {
							fecha = fecha.plusMonths(6);
							Vacuna vacunaGripe = vacunaRepository.findByNombre("Gripe");
							
							Turno nuevoTurno = new Turno(0, LocalDateTime.of(LocalDate.now(), LocalTime.now()), fecha, null, vacunaGripe, vacunatorioElegido, nuevoPaciente);
							turnoRepository.save(nuevoTurno);
						}
					}
					pacienteRepository.save(nuevoPaciente);
				}
				//Devuelvo el codigo despues de haber asignado los turnos correspondientes
				return nuevoPaciente.getCodigo();
			} 
			//Existe un input vacio
			else {
				return null;
			}
		}
		//Paciente ya existe en el sistema
		else {
			return null;
		}
	}
	
}
