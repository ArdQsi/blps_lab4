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
        Long id = Long.valueOf(delegateExecution.getVariable("idUser").toString());
        String tokenFilm = (String) delegateExecution.getVariable("tokenFilm");
        filmService.getFilm(tokenFilm, id);
    }
}
