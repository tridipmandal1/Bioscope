//package com.bioscope.backend.v01.entities;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.util.UUID;
//
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//public class PaymentHistoryEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private UUID id;
//
//    private String transactionId;
//
//    private Double amount;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private UserEntity user;
//}
