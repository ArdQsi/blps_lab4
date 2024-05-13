package com.webapp.delegators;

import com.webapp.exceptioin.ResourceAlreadyExistsException;
import com.webapp.model.FilmEntity;
import com.webapp.repository.FilmRepository;
import com.webapp.service.FilmService;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named("checkIfFilmExist")
@RequiredArgsConstructor
public class CheckIfFilmExist implements JavaDelegate {
    private final FilmService filmService;
    private final FilmRepository filmRepository;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String filmName = (String) delegateExecution.getVariable("film");
        FilmEntity film = filmRepository.findFilmByName(filmName);
        delegateExecution.setVariable("checkForExistence", filmService.isFilmExist(film.getToken()));
        delegateExecution.setVariable("checkForFree", filmService.isFilmFree(film.getToken()));
        delegateExecution.setVariable("tokenFilm", film.getToken());
    }
}
