package com.nikitalipatov.handmadeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String surname;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;

    public UserDTO(String surname, String name, String phoneNumber, String address) {
        this.surname = surname;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}
