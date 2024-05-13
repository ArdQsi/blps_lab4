package com.webapp.delegators;

import com.webapp.repository.UserRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named("authorization")
@RequiredArgsConstructor
public class Authorization implements JavaDelegate {
    private final UserRepository userRepository;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String login = (String) delegateExecution.getVariable("login");
        System.out.println(login);
        userRepository.findUserByLogin(login).orElseThrow();
    }
}
