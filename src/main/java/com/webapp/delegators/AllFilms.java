package com.webapp.delegators;

import com.webapp.service.FilmService;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named("allfilms")
@RequiredArgsConstructor
public class AllFilms implements JavaDelegate {

    private final FilmService filmService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        delegateExecution.setVariable("films", filmService.getAllFilm().toString());
    }
}
