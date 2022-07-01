package uo.ri.cws.application.service.client.crud.command;

import java.util.List;

import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.service.client.ClientCrudService.ClientDto;
import uo.ri.cws.application.util.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Client;

public class FindAllClients implements Command<List<ClientDto>> {

	private ClientRepository crepo = Factory.repository.forClient();
	
	@Override
	public List<ClientDto> execute() throws BusinessException {
		
		List<Client> list = crepo.findAll();
		
		return DtoAssembler.toClientDtoList(list);
	}

}
