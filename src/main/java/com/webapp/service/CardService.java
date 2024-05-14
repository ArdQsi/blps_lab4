package com.webapp.service;

import com.webapp.dto.CardDto;
import com.webapp.exceptioin.ResourceNotAllowedException;
import com.webapp.model.CardEntity;
import com.webapp.model.UserEntity;
import com.webapp.repository.CardRepository;
import com.webapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public void saveCard(CardDto cardDto){
        Optional<UserEntity> userEntity = userRepository.findUserByLogin(cardDto.getLogin());
        if (!checkCard(cardDto) || cardDto.getAmount()<0) {
            throw new ResourceNotAllowedException("Balance is negative or not valid card");
        }
        CardEntity card = new CardEntity();
        card.setNumber(cardDto.getNumber());
        card.setMonth(cardDto.getMonth());
        card.setYear(cardDto.getYear());
        card.setName(cardDto.getName());
        card.setSurname(cardDto.getSurname());
        card.setUser(userEntity.get());

        cardRepository.save(card);
        userService.updateBalance(userEntity.orElse(null), cardDto.getAmount());
    }

    private boolean checkCard(CardDto cardDto){
        LocalDate date = LocalDate.now();
        String regex_cardNumber = "(^$|[0-9]{16})";
        String regex_cardCVC = "(^$|[0-9]{3})";

        if (!cardDto.getNumber().matches(regex_cardNumber)) {
            return false;
        }
        if (!cardDto.getCvc().toString().matches(regex_cardCVC)) {
            return false;
        }
        if(!(cardDto.getMonth()<=12 && cardDto.getMonth()>=1)){
            return false;
        }
        if(cardDto.getYear()<date.getYear()){
            return false;
        }
        if(cardDto.getYear()==date.getYear() && cardDto.getMonth()<date.getMonthValue()){
            return false;
        }
        return true;
    }

    public List<CardEntity> getCardById(Long id){
        List<Long> ids = List.of(id);
        return cardRepository.findAllById(ids);
    }
}
