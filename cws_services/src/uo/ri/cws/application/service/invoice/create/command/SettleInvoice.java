package uo.ri.cws.application.service.invoice.create.command;

import java.util.Map;
import java.util.Map.Entry;

import alb.util.assertion.ArgumentChecks;
import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.ChargeRepository;
import uo.ri.cws.application.repository.InvoiceRepository;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.util.BusinessChecks;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Charge;
import uo.ri.cws.domain.Invoice;
import uo.ri.cws.domain.PaymentMean;

public class SettleInvoice implements Command<Void> {

	private InvoiceRepository invrepo = Factory.repository.forInvoice();
	private PaymentMeanRepository pmeanrepo = Factory.repository.forPaymentMean();
	private ChargeRepository chargerepo = Factory.repository.forCharge();
	
	private String invoiceId;
	private Map<String, Double> charges;
	
	public SettleInvoice(String invoiceId, Map<String, Double> charges) {
		// TODO Auto-generated constructor stub
		ArgumentChecks.isNotEmpty(invoiceId);
		ArgumentChecks.isNotNull(charges);
		
		this.invoiceId = invoiceId;
		this.charges = charges;
	}

	@Override
	public Void execute() throws BusinessException {
		
		BusinessChecks.exists(invrepo.findById(invoiceId));
		
		Invoice i = invrepo.findById(invoiceId).get();
		
		BusinessChecks.isTrue(i.isNotSettled());
		
		checkPaymentMeans();
		
		checkAmount(i);
		
		for (Map.Entry<String, Double> value: charges.entrySet()) {
			
			PaymentMean pm = pmeanrepo.findById(value.getKey()).get();
			
			chargerepo.add(new Charge(i, pm, value.getValue()));
		}
		
		i.settle();
		
		return null;
	}

	private void checkAmount(Invoice i) throws BusinessException {
		double total = 0;
		
		for (Map.Entry<String, Double> value: charges.entrySet()) {
			
			total += value.getValue();
		}
		
		BusinessChecks.isTrue(Math.abs( total - i.getAmount()) <= 0.01);
	}

	private void checkPaymentMeans() throws BusinessException {
		for (Map.Entry<String, Double> value: charges.entrySet()) {
			BusinessChecks.exists(pmeanrepo.findById(value.getKey()));
			BusinessChecks.isTrue(pmeanrepo.findById(value.getKey()).get().canPay(value.getValue()));
		}
	}

}
