package com.example.banto.Entitys;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class SoldItems {
	@Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

	@Column(name="ITEM_NAME", nullable=false)
    private String itemName;
	
	@Column(name="AMOUNT", nullable=false)
    private Integer amount;
	
	@Column(name="OPTION_INFO", nullable=false)
    private String optionInfo;
	
	@Column(name="ADD_PRICE", nullable=false)
    private Integer addPrice;
	
	@Column(name="ITEM_PRICE", nullable=false)
    private Integer itemPrice;
	
	@Column(name="SOLD_PRICE", nullable=false)
    private Integer soldPrice;
	
	@Column(name="SOLD_DATE", nullable=false, insertable = false, columnDefinition = "date default sysdate")
    private LocalDateTime soldDate;
	
	@Column(name="DELIVER_INFO", nullable=false)
    @Enumerated(EnumType.STRING)  // Enum 값을 문자열로 저장
    private DeliverType deliverInfo;
	
	@Column(name="ITEM_PK")
    private Integer itemPk;
	
	@Column(name="OPTION_PK")
    private Integer optionPk;
	
	@Column(name="STORE_PK")
	private Integer storePk;
	
	@JsonIgnore
	@ManyToOne
    @JoinColumn(name="BUYER_PK")
    private Users user;
	
}
