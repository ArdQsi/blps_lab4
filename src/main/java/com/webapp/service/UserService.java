package com.webapp.service;

import com.webapp.auth.AuthenticationRequest;
import com.webapp.auth.RegisterRequest;
import com.webapp.dto.AuthenticationResponseDto;
import com.webapp.dto.MessageDto;
import com.webapp.exceptioin.ResourceNotAllowedException;
import com.webapp.exceptioin.ResourceNotFoundException;
import com.webapp.exceptioin.ResourceAlreadyExistsException;
import com.webapp.kafka.MailProducer;
import com.webapp.model.FilmEntity;
import com.webapp.model.Role;
import com.webapp.model.UserEntity;
import com.webapp.repository.FilmRepository;
import com.webapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${SUBSCRIPTION_PRICE}")
    private int price;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MailProducer producer;

    @Transactional
    public MessageDto updateSubscription(String login) {
        Optional<UserEntity> user = userRepository.findUserByLogin(login);

        if (user.get().getBalance() <= 0) {
            throw new ResourceNotFoundException("Balance is empty!");
        }
        if (user.get().getBalance() <= price) {
            throw new ResourceNotFoundException("Lack of founds to pay!");
        }
        user.get().setBalance(user.get().getBalance() - price);
        userRepository.save(user.get());
        updateSubscriptionEndDate(user.get().getLogin());
        return new MessageDto("Subscription extended");
    }

    public void sendMessage(String topic, Long id, String email){
        ProducerRecord<Long, String> record = new ProducerRecord<>(topic, id, email);
        producer.send(record);
    }

    public void updateBalance(UserEntity user, Long amount) {
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);
    }

    public boolean isActualSubscription(String login){
        Optional<UserEntity> user = userRepository.findUserByLogin(login);
        if (user.get().getSubscriptionEndDate()!=null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate endDate = LocalDate.parse(user.get().getSubscriptionEndDate(), formatter);
            LocalDate currentDate = LocalDate.now();
            if (endDate.isAfter(currentDate)) {
                return true;
            }
        }
        return false;
    }

    public void addFilmToHistory(Long filmId, String login) {
        Optional<UserEntity> user = userRepository.findUserByLogin(login);
        FilmEntity film = filmRepository.findFilmById(filmId);

        if (user.get().getUserFilm().stream().noneMatch(f -> Objects.equals(f.getId(), film.getId()))) {
            user.get().getUserFilm().add(film);
            userRepository.save(user.get());
        }
    }


    public void automaticDebitingOfMoney() {
        final var subscriptions = userRepository.findAll();
        for (var subscription : subscriptions){
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate endDate = LocalDate.parse(subscription.getSubscriptionEndDate(), formatter);
                LocalDate currentDate = LocalDate.now();
                if (currentDate.isEqual(endDate)) {
                    updateSubscription(subscription.getLogin());
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Long register(RegisterRequest registerRequest) {
        Optional<UserEntity> userEntity = userRepository.findUserByLogin(registerRequest.getLogin());
        if (!userEntity.isPresent()) {
            UserEntity user = UserEntity.builder()
                    .firstname(registerRequest.getFirstname())
                    .lastname(registerRequest.getLastname())
                    .email(registerRequest.getEmail())
                    .login(registerRequest.getLogin())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(Role.ROLE_USER)
                    .build();
            userRepository.save(user);
            String jwtToken = jwtService.generateToken(user);

//            return AuthenticationResponseDto.builder()
//                    .token(jwtToken)
//                    .build();
            return user.getId();
        } else {
            throw new ResourceAlreadyExistsException("User already exist");
        }
    }

//    public AuthenticationResponseDto authenticate(AuthenticationRequest authenticationRequest) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        authenticationRequest.getEmail(),
//                        authenticationRequest.getPassword()
//                )
//        );
//        var user = userRepository.findUserByEmail(authenticationRequest.getEmail())
//                .orElseThrow();
//        String jwtToken = jwtService.generateToken(user);
//        return AuthenticationResponseDto.builder()
//                .token(jwtToken)
//                .build();
//    }

    public void updateSubscriptionEndDate(String login) {
        Optional<UserEntity> user = userRepository.findUserByLogin(login);
        LocalDate currentDate;
        if (user.get().getSubscriptionEndDate() == null) {
            currentDate = LocalDate.now();
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            currentDate = LocalDate.parse(user.get().getSubscriptionEndDate(), formatter);
        }
        LocalDate futureDate = currentDate.plusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = futureDate.format(formatter);
        user.get().setSubscriptionEndDate(formattedDate);
        userRepository.save(user.get());
    }

    public void cancelUpdateSubscriptionEndDate(String login) {
        Optional<UserEntity> user = userRepository.findUserByLogin(login);
        if (user.get().getSubscriptionEndDate()!=null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate endDate = LocalDate.parse(user.get().getSubscriptionEndDate(), formatter);
//            endDate.minusDays(30);
            LocalDate newEndDate = endDate.minusDays(30);
            user.get().setSubscriptionEndDate(newEndDate.toString());
            userRepository.save(user.get());
        }
    }

    public MessageDto addModerator(Long id) {
        UserEntity userEntity = userRepository.findUserById(id);
        if (userEntity == null)
            throw new ResourceNotFoundException("This user does not exist");
        if (userEntity.getRole() == Role.ROLE_MODERATOR) {
            throw new ResourceNotAllowedException("The user already has the user moderator");
        }
        userEntity.setRole(Role.ROLE_MODERATOR);
        userRepository.save(userEntity);
        return new MessageDto("Moderator added successfully");
    }

    public MessageDto removeModerator(Long id) {
        UserEntity userEntity = userRepository.findUserById(id);
        if (userEntity == null)
            throw new ResourceNotFoundException("This user does not exist");
        if (userEntity.getRole() == Role.ROLE_USER) {
            throw new ResourceNotAllowedException("The user already has the user role");
        }
        userEntity.setRole(Role.ROLE_USER);
        userRepository.save(userEntity);
        return new MessageDto("Moderator successfully removed");
    }
}
