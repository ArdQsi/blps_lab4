package com.webapp.delegators;

import com.webapp.model.UserEntity;
import com.webapp.repository.UserRepository;
import com.webapp.service.UserService;
import jakarta.inject.Named;
import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.sql.Timestamp;
import java.util.Calendar;

@Named("updateSubscription")
@AllArgsConstructor
public class UpdateSubscription implements JavaDelegate {
    private final UserService userService;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long id = Long.valueOf(delegateExecution.getVariable("idUser").toString());
        userService.updateSubscriptionEndDate(id);
    }
}
