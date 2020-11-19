package com.dbms.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    int userId;
    String userName;
    String email;
    LocalDateTime createdOn;
}
