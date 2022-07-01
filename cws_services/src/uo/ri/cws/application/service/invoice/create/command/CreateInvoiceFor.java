package uo.ri.cws.application.service.invoice.create.command;

import java.util.List;

import alb.util.assertion.ArgumentChecks;
import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.InvoiceRepository;
import uo.ri.cws.application.repository.WorkOrderRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.service.invoice.InvoicingService.InvoiceDto;
import uo.ri.cws.application.util.BusinessChecks;
import uo.ri.cws.application.util.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Invoice;
import uo.ri.cws.domain.WorkOrder;

public class CreateInvoiceFor implements Command<InvoiceDto>{

	private List<String> workOrderIds;
	private WorkOrderRepository wrkrsRepo = Factory.repository.forWorkOrder();
	private InvoiceRepository invsRepo = Factory.repository.forInvoice();

	public CreateInvoiceFor(List<String> workOrderIds) {
		ArgumentChecks.isNotNull( workOrderIds );
		ArgumentChecks.isTrue(workOrderIds.size() > 0);
		checkNullAndEmpty(workOrderIds);
		this.workOrderIds = workOrderIds;
	}

	private void checkNullAndEmpty(List<String> workOrderIds) {
		for(String s: workOrderIds) {
			ArgumentChecks.isNotEmpty(s);
		}
	}

	@Override
	public InvoiceDto execute() throws BusinessException {

		// Long number = invsRepo.getNextInvoiceNumber();
		Long number = (invsRepo.getNextInvoiceNumber() == null) ? 1 : invsRepo.getNextInvoiceNumber();
		List<WorkOrder> workOrders = wrkrsRepo.findByIds(workOrderIds);
		BusinessChecks.isTrue(workOrders.size() == workOrderIds.size(),
				"Some work order does not exist");
		
		Invoice i; 
		
		try {
			i = new Invoice(number, workOrders);
		} catch(IllegalStateException e) {
			throw new BusinessException(e.getMessage());
		}
		
		invsRepo.add(i);
		
		return DtoAssembler.toDto(i);
	}

}
