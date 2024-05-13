package com.webapp.delegators;

import com.webapp.exceptioin.ResourceNotFoundException;
import com.webapp.model.UserEntity;
import com.webapp.repository.UserRepository;
import jakarta.inject.Named;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

@Named("sabstructMoney")
@RequiredArgsConstructor
public class SabstructMoney implements JavaDelegate {
    @Value("${SUBSCRIPTION_PRICE}")
    private int price;
    private final UserRepository userRepository;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String login = (String) delegateExecution.getVariable("login");
        Optional<UserEntity> user = userRepository.findUserByLogin(login);
        if (user.get().getBalance() <= 0) {
            throw new ResourceNotFoundException("Balance is empty!");
        }
        if (user.get().getBalance() <= price) {
            throw new ResourceNotFoundException("Lack of founds to pay!");
        }

        user.get().setBalance(user.get().getBalance() - price);
        userRepository.save(user.get());
    }
}
