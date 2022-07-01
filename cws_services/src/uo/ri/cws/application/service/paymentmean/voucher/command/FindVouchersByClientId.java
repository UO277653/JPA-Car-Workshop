package uo.ri.cws.application.service.paymentmean.voucher.command;

import java.util.ArrayList;
import java.util.List;

import alb.util.assertion.ArgumentChecks;
import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.service.paymentmean.PaymentMeanCrudService.VoucherDto;
import uo.ri.cws.application.util.DtoAssembler;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.PaymentMean;
import uo.ri.cws.domain.Voucher;

public class FindVouchersByClientId implements Command<List<VoucherDto>> {

	private PaymentMeanRepository pMeanRep = Factory.repository.forPaymentMean();
	private String id;
	
	public FindVouchersByClientId(String id) {
		ArgumentChecks.isNotEmpty(id);
		this.id = id;
	}

	@Override
	public List<VoucherDto> execute() throws BusinessException {
		List<VoucherDto> res = new ArrayList<VoucherDto>();
		
		List<Voucher> vouchers = pMeanRep.findVouchersByClientId(id);
		
		for (Voucher voucher : vouchers) {
			res.add(DtoAssembler.toDto(voucher));
		}
		
		return res;
	}

}
