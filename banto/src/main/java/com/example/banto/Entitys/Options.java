package com.example.banto.Entitys;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Options {
    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    @Column(name="ADD_PRICE", nullable=false)
    private Integer addPrice;
    
    @Column(name="OPTION_INFO", nullable=false)
    private String optionInfo;
    
    @Column(name="AMOUNT", nullable=false)
    private Integer amount;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="ITEM_PK")
    private Items item;
 
    // 'option' 필드가 Carts 엔티티에 존재해야 함
    @JsonIgnore
    @OneToMany(mappedBy="option", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Carts> carts;

    @JsonIgnore
    @OneToMany(mappedBy="option", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupBuyItems> eventItems;
}
