package com.webapp.delegators;

import com.webapp.service.UserService;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named("checkSubscription")
@RequiredArgsConstructor
public class CheckSubscription implements JavaDelegate {
    private final UserService userService;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long id = Long.valueOf(delegateExecution.getVariable("idUser").toString());
        delegateExecution.setVariable("isActualSubscription", userService.isActualSubscription(id));
    }
}
