package uo.ri.cws.application.service.paymentmean.crud.command;

import java.time.LocalDate;

import alb.util.assertion.ArgumentChecks;
import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.service.paymentmean.PaymentMeanCrudService.CardDto;
import uo.ri.cws.application.util.BusinessChecks;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Associations;
import uo.ri.cws.domain.Client;
import uo.ri.cws.domain.CreditCard;

public class AddCard implements Command<CardDto> {

	private ClientRepository crep = Factory.repository.forClient();
	private PaymentMeanRepository pMeanRep = Factory.repository.forPaymentMean();
	private CardDto card;
	
	public AddCard(CardDto card) {
		ArgumentChecks.isNotNull(card);
		ArgumentChecks.isNotNull(card.cardExpiration);
		ArgumentChecks.isNotEmpty(card.id);
		ArgumentChecks.isNotEmpty(card.cardNumber);
		ArgumentChecks.isNotEmpty(card.cardType);
		ArgumentChecks.isNotEmpty(card.clientId);
		
		this.card = card;
	}

	@Override
	public CardDto execute() throws BusinessException {
		
		BusinessChecks.exists(crep.findById(card.clientId));
		
		if(pMeanRep.findCreditCardByNumber(card.cardNumber).isPresent()) {
			throw new BusinessException("Error: a card with the same number "
					+ "already exists");
		}
		
		if(card.cardExpiration.isBefore(LocalDate.now())) {
			throw new BusinessException("The card is expired");
		}
		
		Client c = crep.findById(card.clientId).get();
		CreditCard cardEntity = new CreditCard(card.cardNumber, card.cardType, card.cardExpiration);
		
		pMeanRep.add(cardEntity);
		
		Associations.Pay.link(cardEntity, c);
		
		//card.id = cardEntity.getClient().getId();
		
		card.clientId = cardEntity.getClient().getId();
		
		return card;
	}

}
