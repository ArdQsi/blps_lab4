package com.webapp.delegators;

import com.webapp.dto.CardDto;
import com.webapp.service.CardService;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named("checkCard")
@RequiredArgsConstructor
public class CheckCard implements JavaDelegate {
    private final CardService cardService;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long id = Long.valueOf(delegateExecution.getVariable("idUser").toString());
        String number = (String) delegateExecution.getVariable("number");
        Long month = Long.valueOf(delegateExecution.getVariable("month").toString());
        Long year = Long.valueOf(delegateExecution.getVariable("year").toString());
        String name = (String) delegateExecution.getVariable("name");
        String surname = (String) delegateExecution.getVariable("surname");
        Long cvc = Long.valueOf(delegateExecution.getVariable("cvc").toString());
        Long amount = Long.valueOf(delegateExecution.getVariable("amount").toString());
        CardDto cardDto = new CardDto(id, number, month, year, name, surname, cvc, amount);
        delegateExecution.setVariable("isCorrectCard", cardService.saveCard(cardDto));
    }
}
