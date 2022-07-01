package uo.ri.cws.application.service.mechanic.crud.command;

import java.util.List;

import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.MechanicRepository;
import uo.ri.cws.application.service.mechanic.MechanicCrudService.MechanicDto;
import uo.ri.cws.application.util.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Mechanic;

public class FindAllMechanics implements Command<List<MechanicDto>>{

	private MechanicRepository repo = Factory.repository.forMechanic();
	
	public List<MechanicDto> execute() {
		
//		String query = "SELECT m FROM Mechanic m"; // JPQL, not SQL
//		
//		List<Mechanic> list = Jpa.getManager().createQuery(query, Mechanic.class)
//								.getResultList();
		
		List<Mechanic> list = repo.findAll();
		
		return DtoAssembler.toMechanicDtoList(list);
	}

}
