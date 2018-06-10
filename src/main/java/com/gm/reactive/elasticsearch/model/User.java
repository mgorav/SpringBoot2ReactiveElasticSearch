package com.gm.reactive.elasticsearch.model;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class User {

    private String firstName;

    private String lastName;

    private String email;

    private String username;

    private String sex;

    private String telephoneNumber;

    private String dateOfBirth;

}
