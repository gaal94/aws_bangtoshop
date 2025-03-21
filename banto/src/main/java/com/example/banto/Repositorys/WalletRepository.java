package com.example.banto.Repositorys;

import java.util.Optional;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.banto.Entitys.Wallets;

public interface WalletRepository extends JpaRepository<Wallets, Integer> {

	Optional<Wallets> findByUser_Id(Integer userId);

}
