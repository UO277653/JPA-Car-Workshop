package uo.ri.cws.application.service.client.crud.command;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import alb.util.assertion.ArgumentChecks;
import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.repository.RecommendationRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.util.BusinessChecks;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Associations;
import uo.ri.cws.domain.Client;
import uo.ri.cws.domain.Mechanic;
import uo.ri.cws.domain.PaymentMean;
import uo.ri.cws.domain.Recommendation;

public class DeleteClient implements Command<Void> {

	private ClientRepository crepo = Factory.repository.forClient();
	private PaymentMeanRepository pmeanrepo = Factory.repository.forPaymentMean();
	private RecommendationRepository recommendationsrepo = Factory.repository.forRecomendacion();
	
	private String idClient;
	
	public DeleteClient(String idClient) {
		ArgumentChecks.isNotEmpty(idClient);
		
		this.idClient = idClient;
	}

	@Override
	public Void execute() throws BusinessException {
		
		Optional<Client> oc = crepo.findById(idClient); 
		BusinessChecks.exists(oc, "The client does not exist");
		Client c = oc.get();
		checkCanBeRemoved(c);
		
		List<PaymentMean> payments = pmeanrepo.findByClientId(idClient);
		
		for(PaymentMean payment: payments) {
			pmeanrepo.remove(payment);
		}
		
		Set<Recommendation> recommendations = c.getRecommendations();
		
		for (Recommendation recommendation : recommendations) {
			recommendationsrepo.remove(recommendation);
		}
		
		//Associations.Recommend.unlink(c.getRecommended());
		
		crepo.remove(c);
		
		return null;
	}

	private void checkCanBeRemoved(Client c) throws BusinessException {
		BusinessChecks.isTrue(c.getVehicles().isEmpty(), 
				"The client has vehicles registered");
	}

}
