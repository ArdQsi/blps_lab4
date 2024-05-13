package com.webapp.delegators;

import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named("registrationMessageSender")
@RequiredArgsConstructor
public class RegistrationMessageSender implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        delegateExecution.getProcessEngineServices().getRuntimeService()
                .createMessageCorrelation("regMessage")
                .setVariable("idUser", delegateExecution.getVariable("idUser"))
                .setVariable("email", delegateExecution.getVariable("email"))
                .correlate();
    }
}
