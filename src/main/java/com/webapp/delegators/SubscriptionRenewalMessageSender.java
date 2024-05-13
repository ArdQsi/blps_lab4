package com.webapp.delegators;


import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named("subscriptionRenewalMessageSender")
@RequiredArgsConstructor
public class SubscriptionRenewalMessageSender implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        delegateExecution.getProcessEngineServices().getRuntimeService()
                .createMessageCorrelation("subscriptionRenewalMessage")
                .setVariable("login", delegateExecution.getVariable("login"))
                .setVariable("email", delegateExecution.getVariable("email"))
                .correlate();
    }
}
