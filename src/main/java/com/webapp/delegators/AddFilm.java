package com.webapp.delegators;

import com.webapp.dto.FilmDto;
import com.webapp.dto.RequestFilmAddDto;
import com.webapp.repository.FilmRepository;
import com.webapp.service.FilmService;
import com.webapp.service.UserService;
import jakarta.inject.Named;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Named("addFilm")
@RequiredArgsConstructor
public class AddFilm implements JavaDelegate {

    private final FilmService filmService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String name = (String) delegateExecution.getVariable("name");
        String year = (String) delegateExecution.getVariable("year");
        String description = (String) delegateExecution.getVariable("description");
        Boolean subscription = (Boolean) delegateExecution.getVariable("subscription");
        String genres_str = (String) delegateExecution.getVariable("genres");
        String[] arr_genres = genres_str.split(",");
        Set<String> genres = new HashSet<>(Arrays.asList(arr_genres));
        RequestFilmAddDto filmDto = new RequestFilmAddDto(name,year,description,subscription, genres);
        filmService.addFilm(filmDto);
    }
}
