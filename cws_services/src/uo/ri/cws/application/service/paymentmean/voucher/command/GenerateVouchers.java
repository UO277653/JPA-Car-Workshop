package uo.ri.cws.application.service.paymentmean.voucher.command;

import java.util.ArrayList;  
import java.util.List;
import java.util.UUID;

import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.repository.InvoiceRepository;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Associations;
import uo.ri.cws.domain.Client;
import uo.ri.cws.domain.Invoice;
import uo.ri.cws.domain.Recommendation;
import uo.ri.cws.domain.Vehicle;
import uo.ri.cws.domain.Voucher;
import uo.ri.cws.domain.WorkOrder;

public class GenerateVouchers implements Command<Integer> {

	private ClientRepository crepo = Factory.repository.forClient();
	private PaymentMeanRepository pmeanrepo = Factory.repository.forPaymentMean();
	private InvoiceRepository invrepo = Factory.repository.forInvoice();
	
	@Override
	public Integer execute() throws BusinessException {
		int res = 0;
		
		res += generateVouchers20() + generateVouchers25() + generateVouchers30();
		
		return res;
	}

	private Integer generateVouchers20() {
		int sum = 0;
		List<WorkOrder> validWorkOrders;
		
		List<Client> clients = crepo.findWithThreeUnusedWorkOrders();
		
		for(Client c: clients) {
			
			validWorkOrders = new ArrayList<WorkOrder>();
			
			for(Vehicle v: c.getVehicles()) {
				for(WorkOrder w: v.getWorkOrders()) {
					if(w.canBeUsedForVoucher()) {
						validWorkOrders.add(w);
					}
				}
			}
			
			for(int i = 0; i < (validWorkOrders.size() / 3); i++) {
				WorkOrder w1 = validWorkOrders.get(3*i);
				WorkOrder w2 = validWorkOrders.get(3*i + 1);
				WorkOrder w3 = validWorkOrders.get(3*i + 2);
				
				Voucher voucher = new Voucher(UUID.randomUUID().toString(), "By three workorders", 20.0);
				pmeanrepo.add(voucher);
				
				Associations.Pay.link(voucher, c);
				
				w1.markAsUsedForVoucher();
				w2.markAsUsedForVoucher();
				w3.markAsUsedForVoucher();
				
				sum++;
			}
		}
		return sum;
	}

	private Integer generateVouchers25() {
		int sum = 0;
		List<Client> clients = crepo.findAll();
		List<Recommendation> validRecommendations;
		
		for(Client client: clients) {
			
			validRecommendations = new ArrayList<Recommendation>();
			if(client.eligibleForRecommendationVoucher()) {
				for(Recommendation r: client.getRecommendations()) {
					Client c = r.getRecommended();
					if(!c.getWorkOrdersAvailableForVoucher().isEmpty()) {
						validRecommendations.add(r);
					}
				}
				
				for(int i = 0; i < validRecommendations.size()/3; i++) {
					
					Recommendation r1 = validRecommendations.get(3*i);
					Recommendation r2 = validRecommendations.get(3*i+1);
					Recommendation r3 = validRecommendations.get(3*i+2);
					
					Voucher voucher = new Voucher(UUID.randomUUID().toString(), "By recommendation", 25.0);
					pmeanrepo.add(voucher);
					
					r1.markAsUsed();
					r2.markAsUsed();
					r3.markAsUsed();
					
					Associations.Pay.link(voucher, client);
					sum++;
				}
			}
		}
		return sum;
	}

	private Integer generateVouchers30() {
		int sum = 0;
		
		List<Invoice> invoices = invrepo.findUnusedWithBono500();
		
		for(Invoice i: invoices) {
			for(WorkOrder w: i.getWorkOrders()) {
				
				Client c = w.getVehicle().getClient();
				
				Voucher voucher = new Voucher(UUID.randomUUID().toString(), "By invoice over 500", 30.0);
				pmeanrepo.add(voucher);
				
				i.markAsUsed();
				
				Associations.Pay.link(voucher, c);
				sum++;
			}
		}
		
		return sum;
	}
}
