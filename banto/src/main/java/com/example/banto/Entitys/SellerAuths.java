package com.example.banto.Entitys;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.example.banto.DTOs.ApplyDTO;
import com.example.banto.DTOs.StoreDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerAuths {
    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    @Column(name="AUTH", nullable=false)
    @Enumerated(EnumType.STRING)  // Enum 값을 문자열로 저장
    private ApplyType auth;
    
    @Column(name="APPLY_DATE", nullable=false, insertable = false, columnDefinition = "date default sysdate")
    private LocalDateTime applyDate;
    
    @Column(name="SIGN_DATE")
    private LocalDateTime signDate;

    @Column(name="STORE_NAME")
    private String storeName;

    @Column(name="BUSI_NUM")
    private String busiNum;

    @ManyToOne
    @JoinColumn(name="USER_PK")
    private Users user;
    
    /*public static SellerAuths toEntity(ApplyDTO dto) {
        return SellerAuths.builder()
                .id(dto.getId())
                .auth(dto.getAuth())
                .applyDate(dto.getApplyDate())
                .signDate(dto.getSignDate())
                .build();
    }*/
}
