package com.webapp.delegators;

import com.webapp.repository.UserRepository;
import com.webapp.service.UserService;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Named("automaticSubtractMoney")
@RequiredArgsConstructor
public class AutomaticSubtractMoney implements JavaDelegate {
    private final UserService userService;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        userService.automaticDebitingOfMoney();
    }
}
