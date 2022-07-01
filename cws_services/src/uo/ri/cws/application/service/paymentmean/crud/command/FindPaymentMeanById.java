package uo.ri.cws.application.service.paymentmean.crud.command;

import java.util.Optional;

import alb.util.assertion.ArgumentChecks;
import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.service.paymentmean.PaymentMeanCrudService.PaymentMeanDto;
import uo.ri.cws.application.util.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.PaymentMean;

public class FindPaymentMeanById implements Command<Optional<PaymentMeanDto>> {

	private String id;
	private PaymentMeanRepository pMeanRep = Factory.repository.forPaymentMean();

	
	public FindPaymentMeanById(String id) {
		ArgumentChecks.isNotEmpty(id);
		
		this.id = id;
	}

	@Override
	public Optional<PaymentMeanDto> execute() throws BusinessException {
		Optional<PaymentMean> om = pMeanRep.findById(id);
		
		return om.isPresent() 
				? Optional.of(DtoAssembler.toDto(om.get())) 
				: Optional.empty();
	}

}
