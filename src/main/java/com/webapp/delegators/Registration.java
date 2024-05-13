package com.webapp.delegators;

import com.webapp.auth.RegisterRequest;
import com.webapp.dto.AuthenticationResponseDto;
import com.webapp.service.UserService;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named("registration")
@RequiredArgsConstructor
public class Registration implements JavaDelegate {
    private final UserService userService;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String firstname = (String) delegateExecution.getVariable("firstname");
        String lastname = (String) delegateExecution.getVariable("lastname");
        String password = (String) delegateExecution.getVariable("password");
        String email = (String) delegateExecution.getVariable("email");
        String login = (String) delegateExecution.getVariable("login");
        System.out.println(login);
        RegisterRequest registerRequest = new RegisterRequest(firstname, lastname, email, login, password);
        userService.register(registerRequest);
    }
}
