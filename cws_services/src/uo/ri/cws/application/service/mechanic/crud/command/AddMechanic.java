package uo.ri.cws.application.service.mechanic.crud.command;

import java.util.Optional;

import alb.util.assertion.ArgumentChecks;
import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.MechanicRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.service.mechanic.MechanicCrudService.MechanicDto;
import uo.ri.cws.application.util.BusinessChecks;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Mechanic;

public class AddMechanic implements Command<MechanicDto>{

	private MechanicDto dto;
	private MechanicRepository repo = Factory.repository.forMechanic();

	public AddMechanic(MechanicDto dto) {
		ArgumentChecks.isNotNull(dto);
		this.dto = dto;
	}

	public MechanicDto execute() throws BusinessException {

		checkNotRepeated(dto);
		Mechanic m = new Mechanic(dto.dni);
		m.setName(dto.name);
		m.setSurname(dto.surname);
		
		repo.add(m);
		
		dto.id = m.getId();
		return dto;
	}

	private void checkNotRepeated(MechanicDto dto) throws BusinessException {
		
		Optional<Mechanic> mech = repo.findByDni(dto.dni);
		
		if(mech.isPresent()) {
			throw new BusinessException("Error: a mechanic with this dni already exists");
		}
	}

}
