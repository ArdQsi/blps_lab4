package com.webapp.delegators;

import com.webapp.service.UserService;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named("registrationSend")
@RequiredArgsConstructor
public class RegistrationSend implements JavaDelegate {
    private final UserService userService;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long id = Long.valueOf(delegateExecution.getVariable("idUser").toString());
        String email = (String) delegateExecution.getVariable("email");
        userService.sendMessage("reg_mail", id, email);
    }
}
