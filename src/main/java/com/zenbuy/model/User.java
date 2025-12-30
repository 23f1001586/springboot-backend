package com.zenbuy.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String password; // Nullable for OAuth users

    @Column(nullable = false)
    private String name;

    @Column(name = "provider")
    private String provider; // google, github, linkedin, or local

    @Column(name = "provider_id")
    private String providerId; // OAuth provider's user ID

    @Lob
    @Column(name = "profile_pic", columnDefinition = "LONGTEXT")
    private String profilePic; // Base64 encoded image or URL

    @Column(name = "address", columnDefinition = "TEXT")
    private String address; // Combined address

    @Column(name = "flat_no")
    private String flatNo;

    @Column(name = "locality")
    private String locality;

    @Column(name = "city")
    private String city;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "age")
    private Integer age;

    @Column(name = "role", nullable = false)
    private String role = "USER"; // USER or ADMIN
}

