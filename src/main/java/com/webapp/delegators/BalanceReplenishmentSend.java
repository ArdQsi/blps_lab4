package com.webapp.delegators;

import com.webapp.model.UserEntity;
import com.webapp.repository.UserRepository;
import com.webapp.service.UserService;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.Optional;

@Named("balanceReplenishmentSend")
@RequiredArgsConstructor
public class BalanceReplenishmentSend implements JavaDelegate {
    private final UserService userService;
    private final UserRepository userRepository;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String login = (String) delegateExecution.getVariable("login");
        Optional<UserEntity> userEntity = userRepository.findUserByLogin(login);
        userService.sendMessage("balance_mail", userEntity.get().getId(), userEntity.get().getEmail());
    }
}
