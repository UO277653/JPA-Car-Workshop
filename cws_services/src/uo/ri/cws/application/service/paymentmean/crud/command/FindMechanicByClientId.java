package uo.ri.cws.application.service.paymentmean.crud.command;

import java.util.ArrayList;
import java.util.List;

import alb.util.assertion.ArgumentChecks;
import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.service.paymentmean.PaymentMeanCrudService.PaymentMeanDto;
import uo.ri.cws.application.util.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.PaymentMean;

public class FindMechanicByClientId implements Command<List<PaymentMeanDto>> {

	private String id;
	private PaymentMeanRepository pMeanRep = Factory.repository.forPaymentMean();
	
	public FindMechanicByClientId(String id) {
		ArgumentChecks.isNotEmpty(id);
		
		this.id = id;
	}

	@Override
	public List<PaymentMeanDto> execute() throws BusinessException {
		
		List<PaymentMean> pMeans = pMeanRep.findByClientId(id);
		
		List<PaymentMeanDto> res = new ArrayList<PaymentMeanDto>();
		
		for(PaymentMean pMean: pMeans) {
			res.add(DtoAssembler.toDto(pMean));
		}
		
		return res;
	}

}
