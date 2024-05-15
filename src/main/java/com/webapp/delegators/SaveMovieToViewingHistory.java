package com.webapp.delegators;

import com.webapp.exceptioin.ResourceAlreadyExistsException;
import com.webapp.service.FilmService;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named("saveMovieToViewingHistory")
@RequiredArgsConstructor
public class SaveMovieToViewingHistory implements JavaDelegate {
    private final FilmService filmService;
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String login = (String) delegateExecution.getVariable("login");
        String tokenFilm = (String) delegateExecution.getVariable("tokenFilm");
        System.out.println(login);
        System.out.println(tokenFilm);
        filmService.getFilm(tokenFilm, login);
    }
}
