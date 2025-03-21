package com.example.banto.Entitys;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.banto.DTOs.UserDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"wallets", "sellers", "sellerAuths", "favorites", "carts", "soldItems", "groupItemPays", "comments", "qnas"})
public class Users {

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name="EMAIL", nullable=false, unique=true)
    private String email;

    @JsonIgnore
    @Column(name="PW")
    private String pw;

    @Column(name="NAME", nullable=false)
    private String name;

    @Column(name="ADDR")
    private String addr;

    @Column(name="PHONE")
    private String phone;

    @Column(name = "REG_DATE", nullable=false, insertable = false, columnDefinition = "date default sysdate")
    private LocalDateTime regDate;

    @Column(name="SNS_AUTH", nullable=false)
    private Boolean snsAuth;

    // 1 : 1 Relation
    @JsonIgnore
    @OneToOne(mappedBy="user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Wallets wallets;

    @JsonIgnore
    @OneToOne(mappedBy="user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Sellers sellers;

    // 1 : N Relation(Cascade = REMOVE)
    @JsonIgnore
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SellerAuths> sellerAuths;
    
    @JsonIgnore
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Favorites> favorites;
    
    @JsonIgnore
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Carts> carts;

    // 1 : N Relation(Cascade = null)
    @JsonIgnore
    @OneToMany(mappedBy="user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<SoldItems> soldItems;

    @JsonIgnore
    @OneToMany(mappedBy="user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<GroupItemPays> groupItemPays;

    @JsonIgnore
    @OneToMany(mappedBy="user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Comments> comments;

    @JsonIgnore
    @JsonManagedReference  // 이쪽만 JSON 변환 대상이 됨
    @OneToMany(mappedBy="user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<QNAs> qnas;

    public static Users toEntity(UserDTO dto) {
        return Users.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .pw(dto.getPw())
                .name(dto.getName())
                .addr(dto.getAddr())
                .phone(dto.getPhone())
                .regDate(dto.getRegDate())
                .snsAuth(dto.getSnsAuth())
                .build();
    }
//
//    public Users setSeller(int id, String email, String name, String addr, String phone) {
//    	return Users.builder()
//    			.id(id)
//    			.email(email)
//    			.name(name)
//    			.addr(addr)
//    			.phone(phone)
//    			.build();
//    }
}
