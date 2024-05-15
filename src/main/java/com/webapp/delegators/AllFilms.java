package com.webapp.delegators;

import com.webapp.dto.FilmDto;
import com.webapp.service.FilmService;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.List;

@Named("allfilms")
@RequiredArgsConstructor
public class AllFilms implements JavaDelegate {

    private final FilmService filmService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        List<FilmDto> films = filmService.getAllFilm();
        int i = 0;
        for (FilmDto film : films) {
            delegateExecution.setVariable("film "+i, film.getName());
            i++;
        }
    }
}
