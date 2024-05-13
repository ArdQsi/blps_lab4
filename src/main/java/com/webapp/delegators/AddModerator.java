package com.webapp.delegators;

import com.webapp.service.UserService;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named("addModerator")
@RequiredArgsConstructor
public class AddModerator implements JavaDelegate {
    private final UserService userService;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Long id = (Long) delegateExecution.getVariable("id");
        userService.addModerator(id);
    }
}
