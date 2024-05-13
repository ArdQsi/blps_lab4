package com.webapp.dto;

import com.webapp.model.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CardDto {
    private Long userId;
    private String number;
    private Long month;
    private Long year;
    private String name;
    private String surname;
    private Long cvc;
    private Long amount;
}
