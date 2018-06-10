package com.gm.reactive.elasticsearch.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class User {

    private String firstName;

    private String lastName;

    private String email;

    private String username;

    private String sex;

    private String telephoneNumber;

    private String dateOfBirth;

}
