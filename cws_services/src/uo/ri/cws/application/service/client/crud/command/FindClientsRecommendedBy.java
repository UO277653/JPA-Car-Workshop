package uo.ri.cws.application.service.client.crud.command;

import java.util.List;
import java.util.Optional;

import alb.util.assertion.ArgumentChecks;
import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.repository.RecommendationRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.service.client.ClientCrudService.ClientDto;
import uo.ri.cws.application.util.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Client;
import uo.ri.cws.domain.Mechanic;

public class FindClientsRecommendedBy implements Command<List<ClientDto>> {

	private ClientRepository crepo = Factory.repository.forClient();
	
	private String sponsorID;
	
	public FindClientsRecommendedBy(String sponsorID) {
		ArgumentChecks.isNotEmpty(sponsorID);
		
		this.sponsorID = sponsorID;
	}

	@Override
	public List<ClientDto> execute() throws BusinessException {
		
		List<Client> list = crepo.findSponsoredByClient(sponsorID);
		
		return DtoAssembler.toClientDtoList(list);
	}

}
