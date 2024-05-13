package com.webapp.delegators;

import com.webapp.service.FilmService;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named("deleteFilm")
@RequiredArgsConstructor
public class DeleteFilm implements JavaDelegate {
    private final FilmService filmService;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String token = (String) delegateExecution.getVariable("token");
        filmService.deleteFilm(token);
    }
}
