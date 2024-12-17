package com.example.shopapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "roles")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(name="name",nullable = false)
    private String name;
    public static String ADMIN = "ADMIN";
    public static String USER = "USER";
}
