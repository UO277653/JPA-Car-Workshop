package uo.ri.cws.application.service.client.crud.command;

import java.util.Optional;

import alb.util.assertion.ArgumentChecks;
import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.service.client.ClientCrudService.ClientDto;
import uo.ri.cws.application.util.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Client;

public class FindClientById implements Command<Optional<ClientDto>> {

	private ClientRepository crepo = Factory.repository.forClient();
	private String idClient;
	
	public FindClientById(String idClient) {
		ArgumentChecks.isNotEmpty(idClient);
		
		this.idClient = idClient;
	}

	@Override
	public Optional<ClientDto> execute() throws BusinessException {
		Optional<Client> om = crepo.findById(idClient);
		
		return om.isPresent() 
				? Optional.of(DtoAssembler.toDto(om.get())) 
				: Optional.empty();
	}

}
