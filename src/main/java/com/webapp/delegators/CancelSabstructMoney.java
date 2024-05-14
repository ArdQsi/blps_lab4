package com.webapp.delegators;

import com.webapp.model.UserEntity;
import com.webapp.repository.UserRepository;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

@Named("cancelSabstructMoney")
@RequiredArgsConstructor
public class CancelSabstructMoney implements JavaDelegate {
    @Value("${SUBSCRIPTION_PRICE}")
    private int price;
    private final UserRepository userRepository;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String login = (String) delegateExecution.getVariable("login");
        Optional<UserEntity> user = userRepository.findUserByLogin(login);
        user.get().setBalance(user.get().getBalance() + price);
        userRepository.save(user.get());
    }
}
