package com.example.banto.Entitys;

import java.util.List;

import com.example.banto.DTOs.SellerDTO;
import com.example.banto.DTOs.StoreDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"seller", "items"})
public class Stores {
    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name="STORE_NAME", nullable=false)
    private String name;

    @Column(name="BUSI_NUM", nullable=false)
    private String busiNum;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="SELLER_PK")
    private Sellers seller;

    @JsonIgnore
    @OneToMany(mappedBy="store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)  // 'Items' 엔티티에서 'store' 필드를 기준으로 관계를 매핑
    private List<Items> items;
    
    public static Stores toEntity(StoreDTO dto) {
        return Stores.builder()
                .id(dto.getId())
                .name(dto.getName())
                .seller(dto.getSeller())
                .busiNum(dto.getBusiNum())
                .items(dto.getItems())
                .build();
    }
}
