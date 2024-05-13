package com.webapp.service;

import com.webapp.dto.FilmDto;
import com.webapp.dto.MessageDto;
import com.webapp.dto.RequestFilmAddDto;
import com.webapp.exceptioin.ResourceAlreadyExistsException;
import com.webapp.exceptioin.ResourceNotAllowedException;
import com.webapp.exceptioin.ResourceNotFoundException;
//import com.webapp.kafka.MailProducer;
import com.webapp.mapper.FIlmMapper;
import com.webapp.model.FilmEntity;
import com.webapp.model.GenreEntity;
import com.webapp.model.UserEntity;
import com.webapp.repository.FilmRepository;
import com.webapp.repository.GenreRepository;
import com.webapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final UserService userService;

    //    private final MailProducer producer;
    public boolean isFilmFree(String token) {
        FilmEntity film = filmRepository.findFilmByToken(token);
        return !film.getSubscription();
    }

    public boolean isFilmExist(String token) {
        FilmEntity film = filmRepository.findFilmByToken(token);
        return film != null;
    }


    public List<FilmDto> getAllFilm() {
        List<FilmDto> filmDtos = new ArrayList<>();
        List<FilmEntity> filmEntities = filmRepository.findAll();
        for (FilmEntity film : filmEntities) {
            filmDtos.add(FIlmMapper.MAPPER.toDTO(film));
        }
        return filmDtos;
    }

    public MessageDto deleteFilm(String token) {
        FilmEntity filmEntity = filmRepository.findFilmByToken(token);

        if (filmEntity == null) {
            throw new ResourceNotFoundException("Movie not found");
        }
        filmRepository.deleteAllByToken(token);

//        ProducerRecord<Long, String> record = new ProducerRecord<>("del_film", filmEntity.getId(), filmEntity.getPath());
//        producer.send(record);
        return new MessageDto("Movie was deleted");
    }

    public MessageDto getFilm(String token, String login) {
        FilmEntity film = filmRepository.findFilmByToken(token);
        Optional<UserEntity> user = userRepository.findUserByLogin(login);
        if (film == null) {
            throw new ResourceNotFoundException("The movie doesn't exist");
        }
        if (film.getSubscription()) {
            Date subscriptionEndDate;
            try {
                String subscriptionEndDates = user.get().getSubscriptionEndDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                subscriptionEndDate = dateFormat.parse(subscriptionEndDates);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            if (user.get().getSubscriptionEndDate() != null && subscriptionEndDate.after(new Timestamp(System.currentTimeMillis()))) {
                userService.addFilmToHistory(film.getId(), login);
                return new MessageDto("Watching a movie");
            }
            throw new ResourceNotAllowedException("Access is denied");
        }
        userService.addFilmToHistory(film.getId(), login);
        return new MessageDto("Watching a movie");
    }


    public List<FilmEntity> findFilmByYear(String year) {
        return filmRepository.findFilmByYear(year);
    }

    public FilmEntity findFilmByName(String name) {
        return filmRepository.findFilmByName(name);
    }

    public MessageDto addFilm(RequestFilmAddDto filmDto) {
        FilmEntity filmEntity = filmRepository.findFilmByName(filmDto.getName());

        if (filmEntity != null) {
            throw new ResourceAlreadyExistsException("This movie already exists");
        }

        FilmEntity newFilmEntity = new FilmEntity();
        GenreEntity genreEntity = new GenreEntity();
        Set<String> genreDtos = filmDto.getGenres();

        for (String genre : genreDtos) {
            genreEntity = genreRepository.findByName(genre);
            if (genreEntity == null) {
                throw new ResourceNotFoundException("Genre not found");
            }
            newFilmEntity.getGenres().add(genreEntity);
            genreEntity.getFilms().add(newFilmEntity);
        }

        newFilmEntity.setName(filmDto.getName());
        newFilmEntity.setYear(filmDto.getYear());
        newFilmEntity.setSubscription(filmDto.getSubscription());
        newFilmEntity.setDescription(filmDto.getDescription());
        newFilmEntity.setToken(DigestUtils.md5Hex(filmDto.toString()));

        filmRepository.save(newFilmEntity);
        genreRepository.save(genreEntity);
        return new MessageDto("Movie added successfully");
    }

}
