package uo.ri.cws.application.service.invoice.create.command;

import java.util.List;
import java.util.Optional;

import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.service.invoice.InvoicingService.InvoicingPaymentMeanDto;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Client;
import uo.ri.cws.domain.PaymentMean;

public class FindPaymentMeansByClientDni implements Command<List<InvoicingPaymentMeanDto>> {

	private PaymentMeanRepository pmeanrep = Factory.repository.forPaymentMean();
	private ClientRepository crepo = Factory.repository.forClient();
	
	private String dni;
	
	public FindPaymentMeansByClientDni(String dni) {

		this.dni = dni;
	}

	@Override
	public List<InvoicingPaymentMeanDto> execute() throws BusinessException {
		
		Optional<Client> c = crepo.findByDni(dni);
		
		if(c.isPresent()) {
			Client client = c.get();
			List<PaymentMean> payments = pmeanrep.findPaymentMeansByClientId(client.getDni());
			
			// return DtoAssembler.toD
		}
		
		return null;
	}

}
