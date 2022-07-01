package uo.ri.cws.application.service.client.crud.command;

import java.util.Optional;

import alb.util.assertion.ArgumentChecks;
import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.service.client.ClientCrudService.ClientDto;
import uo.ri.cws.application.util.BusinessChecks;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Address;
import uo.ri.cws.domain.Client;

public class UpdateClient implements Command<Void> {

	private ClientRepository crepo = Factory.repository.forClient();
	private ClientDto client;
	
	public UpdateClient(ClientDto client) {
		ArgumentChecks.isNotNull(client);
		ArgumentChecks.isNotEmpty(client.id);
		
		this.client = client;
	}

	@Override
	public Void execute() throws BusinessException {
		Optional<Client> oc = crepo.findById(client.id);
		BusinessChecks.exists(oc, "Client does not exist");
		Client c = oc.get();
		
		// BusinessChecks.hasVersion(c, client.version);
		
		c.setAddress(new Address(client.addressStreet, 
				client.addressCity, client.addressZipcode));
		c.setEmail(client.email);
		c.setPhone(client.phone);
		c.setName(client.name);
		c.setSurname(client.surname);
		
		return null;
	}

}
