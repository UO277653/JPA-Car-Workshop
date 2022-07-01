package uo.ri.cws.application.service.invoice.create.command;

import java.util.Optional;

import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.InvoiceRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.service.invoice.InvoicingService.InvoiceDto;
import uo.ri.cws.application.util.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Invoice;

public class FindInvoiceByNumber implements Command<Optional<InvoiceDto>> {

	private InvoiceRepository invrepo = Factory.repository.forInvoice();
	private Long number;
	
	public FindInvoiceByNumber(Long number) {
		
		this.number = number;
	}

	@Override
	public Optional<InvoiceDto> execute() throws BusinessException {
		
		Optional<Invoice> om = invrepo.findByNumber(number);
		
		return om.isPresent() 
				? Optional.of(DtoAssembler.toDto(om.get())) 
				: Optional.empty();
	}

}
