package uo.ri.cws.application.service.mechanic.crud.command;

import java.util.Optional;

import alb.util.assertion.ArgumentChecks;
import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.MechanicRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.util.BusinessChecks;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Mechanic;

public class DeleteMechanic implements Command<Void>{

	private String mechanicId;
	private MechanicRepository repo = Factory.repository.forMechanic();

	public DeleteMechanic(String mechanicId) {
		ArgumentChecks.isNotEmpty(mechanicId);
		this.mechanicId = mechanicId;
	}

	public Void execute() throws BusinessException {
		
		// Name in persistence.xml
		
		Optional<Mechanic> om = repo.findById(mechanicId); 
		
		//Jpa.getManager().find(Mechanic.class, mechanicId);
		// Second argument is the primary key of the table
		
		BusinessChecks.exists(om, "The mechanic does not exist");
		Mechanic m = om.get();
		checkCanBeRemoved(m);
		//Jpa.getManager().remove(m);
		repo.remove(m);
		
		return null;
	}

	private void checkCanBeRemoved(Mechanic m) throws BusinessException {
		
		BusinessChecks.isNotNull(m, "The mechanic does not exist");
		BusinessChecks.isTrue(m.getInterventions().isEmpty(), 
				"The mechanic has interventions");
		BusinessChecks.isTrue(m.getAssigned().isEmpty(), 
				"The mechanic has work orders");
	}

}
