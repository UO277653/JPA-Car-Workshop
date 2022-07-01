package uo.ri.cws.application.service.client.crud.command;

import java.util.Optional;

import alb.util.assertion.ArgumentChecks;
import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.repository.RecommendationRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.service.client.ClientCrudService.ClientDto;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Address;
import uo.ri.cws.domain.Associations;
import uo.ri.cws.domain.Cash;
import uo.ri.cws.domain.Client;
import uo.ri.cws.domain.Recommendation;

public class AddClient implements Command<ClientDto> {

	private ClientRepository crepo = Factory.repository.forClient();
	private PaymentMeanRepository pmeanrepo = Factory.repository.forPaymentMean();
	private RecommendationRepository recommendationrepo = Factory.repository.forRecomendacion();
	
	private ClientDto client;
	private String recommenderId;
	
	public AddClient(ClientDto client, String recommenderId) {
		ArgumentChecks.isNotNull(client);
		ArgumentChecks.isNotEmpty(client.dni);
		
		this.client = client;
		this.recommenderId = recommenderId;
	}

	@Override
	public ClientDto execute() throws BusinessException {
		
		checkNotRepeated(client);
		Client c = new Client(client.dni, client.name, client.surname);
		c.setAddress(new Address(client.addressStreet, 
				client.addressCity, client.addressZipcode));
		
		c.setEmail(client.email); // Hay que añadir estos?
		c.setPhone(client.phone);
		
		crepo.add(c);
		client.id = c.getId();
		
		if(!(recommenderId == null) && !recommenderId.isEmpty()) {
			Associations.Recommend.link(c, crepo.findById(recommenderId).get(), new Recommendation());
		}
		
		pmeanrepo.add(new Cash(c));
		
		return client;
	}
	
	private void checkNotRepeated(ClientDto dto) throws BusinessException {
		
		Optional<Client> client = crepo.findByDni(dto.dni);
		
		if(client.isPresent()) {
			throw new BusinessException("Error: a client with this dni already exists");
		}
	}

}
