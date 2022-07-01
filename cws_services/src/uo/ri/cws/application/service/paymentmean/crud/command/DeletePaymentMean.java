package uo.ri.cws.application.service.paymentmean.crud.command;

import java.util.Optional;

import alb.util.assertion.ArgumentChecks;
import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.util.BusinessChecks;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Cash;
import uo.ri.cws.domain.PaymentMean;

public class DeletePaymentMean implements Command<Void> {

	private PaymentMeanRepository pMeanRep = Factory.repository.forPaymentMean();
	private String id;
	
	public DeletePaymentMean(String id) {
		ArgumentChecks.isNotEmpty(id);

		this.id = id;
	}

	@Override
	public Void execute() throws BusinessException {
		
		Optional<PaymentMean> pMean = pMeanRep.findById(id);
		
		BusinessChecks.exists(pMean);
		
		BusinessChecks.isTrue(pMean.get().getCharges().isEmpty(), "Error: "
				+ "the payment has charges associated");
		
		BusinessChecks.isTrue(!(pMean.get() instanceof Cash), "Error: a cash"
				+ " payment mean cannot be removed");
		
		pMeanRep.remove(pMean.get());
		
		return null;
	}

}
