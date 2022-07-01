package uo.ri.cws.application.service.paymentmean.crud.command;

import alb.util.assertion.ArgumentChecks;
import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.service.paymentmean.PaymentMeanCrudService.VoucherDto;
import uo.ri.cws.application.util.BusinessChecks;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Associations;
import uo.ri.cws.domain.Client;
import uo.ri.cws.domain.Voucher;

public class AddVoucher implements Command<VoucherDto> {

	private ClientRepository crep = Factory.repository.forClient();
	private PaymentMeanRepository pMeanRep = Factory.repository.forPaymentMean();
	private VoucherDto voucher;
	
	public AddVoucher(VoucherDto voucher) {
		ArgumentChecks.isNotNull(voucher);
		ArgumentChecks.isNotEmpty(voucher.code);
		ArgumentChecks.isNotEmpty(voucher.description);
		ArgumentChecks.isNotEmpty(voucher.clientId);
		ArgumentChecks.isTrue(voucher.balance >= 0);
		
		// code, description or client id 
		this.voucher = voucher;
	}

	@Override
	public VoucherDto execute() throws BusinessException {
		
		BusinessChecks.exists(crep.findById(voucher.clientId));
		
		if(pMeanRep.findVoucherByCode(voucher.code).isPresent()) {
			throw new BusinessException("Error: a voucher with the same code "
					+ "already exists");
		}
		
		Client c = crep.findById(voucher.clientId).get();
		Voucher voucherEntity = new Voucher(voucher.code, voucher.description, voucher.balance);
		
		pMeanRep.add(voucherEntity);
		
		Associations.Pay.link(voucherEntity, c);
		
		voucher.id = voucherEntity.getId();
		
		return voucher;
	}

}
