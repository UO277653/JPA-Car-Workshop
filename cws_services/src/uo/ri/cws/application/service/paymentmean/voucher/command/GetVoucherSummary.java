package uo.ri.cws.application.service.paymentmean.voucher.command;

import java.util.ArrayList;
import java.util.List;

import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.service.paymentmean.PaymentMeanCrudService.VoucherDto;
import uo.ri.cws.application.service.paymentmean.VoucherService.VoucherSummaryDto;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Client;

public class GetVoucherSummary implements Command<List<VoucherSummaryDto>> {

	private ClientRepository crepo = Factory.repository.forClient();
	
	@Override
	public List<VoucherSummaryDto> execute() throws BusinessException {
		
		List<VoucherSummaryDto> res = new ArrayList<VoucherSummaryDto>();
		VoucherSummaryDto summary;
		int issued;
		double totalAmount, availableBalance, consumed;
		
		List<Client> clients = crepo.findAll();
		
		
		for (Client client : clients) {
			summary = new VoucherSummaryDto();
			issued = 0;
			totalAmount = 0;
			availableBalance = 0;
			consumed = 0;
			
			List<VoucherDto> vouchers = new FindVouchersByClientId(client.getId()).execute();
			
			if(!vouchers.isEmpty()) {
				for(VoucherDto voucher: vouchers) {
					issued++;
					consumed += voucher.accumulated;
					availableBalance += voucher.balance;
					totalAmount += voucher.accumulated + voucher.balance;
				}
				
				summary.dni = client.getDni();
				summary.name = client.getName();
				summary.surname = client.getSurname();
				summary.issued = issued;
				summary.consumed = consumed;
				summary.availableBalance = availableBalance;
				summary.totalAmount = totalAmount;
				
				res.add(summary);
			}
		}
		
		return res;
	}

}
