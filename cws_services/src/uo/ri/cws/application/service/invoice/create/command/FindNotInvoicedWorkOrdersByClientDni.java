package uo.ri.cws.application.service.invoice.create.command;

import java.util.List;

import alb.util.assertion.ArgumentChecks;
import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.repository.WorkOrderRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.service.invoice.DtoAssembler;
import uo.ri.cws.application.service.invoice.InvoicingService.InvoicingWorkOrderDto;
import uo.ri.cws.application.util.BusinessChecks;
import uo.ri.cws.application.util.command.Command;

public class FindNotInvoicedWorkOrdersByClientDni implements Command<List<InvoicingWorkOrderDto>> {

	private ClientRepository crepo = Factory.repository.forClient();
	private WorkOrderRepository worepo = Factory.repository.forWorkOrder();
	
	private String dni;
	
	public FindNotInvoicedWorkOrdersByClientDni(String dni) {
		ArgumentChecks.isNotEmpty(dni);
		
		this.dni = dni;
	}

	@Override
	public List<InvoicingWorkOrderDto> execute() throws BusinessException {
		
		BusinessChecks.exists(crepo.findByDni(dni));
		
		return DtoAssembler.toWorkOrderDtoList(worepo.findNotInvoicedWorkOrdersByClientDni(dni));
	}

}
