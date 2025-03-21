package com.example.banto.DAOs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.banto.Entitys.*;
import com.example.banto.Repositorys.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.banto.Configs.EnvConfig;
import com.example.banto.DTOs.LoginDTO;
import com.example.banto.DTOs.PageDTO;
import com.example.banto.DTOs.ResponseDTO;
import com.example.banto.DTOs.UserDTO;
import com.example.banto.DTOs.WalletDTO;
import com.example.banto.JWTs.JwtUtil;
import jakarta.transaction.Transactional;

@Component
public class UserDAO {
	@Autowired
	UserRepository userRepository;
	@Autowired
	WalletRepository walletRepository;
	@Autowired
	GroupBuyPayRepository groupBuyPayRepository;
	@Autowired
	CommentRepository commentRepository;
	@Autowired
	QNARepository qnaRepository;
	@Autowired
	AuthDAO authDAO;
	@Autowired
	JwtUtil jwtUtil;
	@Autowired
	PayRepository payRepository;
	

	public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
	
	@Transactional
	public void sign(UserDTO dto) throws Exception{
		try {
			Optional<Users> userOpt = userRepository.findByEmail(dto.getEmail());
			Users user = null;
			if(dto.getSnsAuth() == false) {					
				if(userOpt.isPresent()) {
					throw new Exception("존재하는 회원 이메일입니다.");
				}
				BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
				dto.setPw(passwordEncoder.encode(dto.getPw()));
				user = Users.toEntity(dto);
				// 개인 지갑은 1:1이기 때문에 만들어주기
				WalletDTO walletDTO = new WalletDTO();
				walletDTO.setUser(user);
				walletRepository.save(Wallets.toEntity(walletDTO));
			}
			else {
				if(userOpt.isPresent()) {
					user = userOpt.get();
					user.setSnsAuth(true);
					user.setName(dto.getName());
				}
				else {
					user = Users.toEntity(dto);
					// 개인 지갑은 1:1이기 때문에 만들어주기
					WalletDTO walletDTO = new WalletDTO();
					walletDTO.setUser(user);
					walletRepository.save(Wallets.toEntity(walletDTO));
				}
			}
			userRepository.save(user);
		}catch(Exception e) {
			throw e;
		}
	}
	
	public ResponseDTO login(String email, String pw, Boolean snsAuth) throws Exception{
		try {
			Optional<Users> userOpt = userRepository.findByEmail(email);
			if(userOpt.isEmpty()) {
				throw new Exception("존재하지 않는 회원입니다.");
			}
			else {
				Users user = userOpt.get();
				if(user.getSnsAuth() == false) {
					if(pw == null) {
						throw new Exception("입력 오류");
					}
					// 비밀번호 불일치 시 예외
					BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
					if(!passwordEncoder.matches(pw, user.getPw())) {
						throw new Exception("비밀번호 불일치");
					}
				}
				LoginDTO loginDTO = new LoginDTO(jwtUtil.generateToken(user.getId()));
				return new ResponseDTO(loginDTO, null);
			}
		}catch(Exception e) {
			throw e;
		}
	}
	
	public ResponseDTO getUserListForRoot(Integer page) throws Exception{
		try {
			if(!authDAO.authRoot(SecurityContextHolder.getContext().getAuthentication())){
				throw new Exception("관리자 권한 오류");
			}
			// 10명씩 끊기
			Pageable pageable = PageRequest.of(page-1, 10, Sort.by("id").ascending());
			Page<Users>users = userRepository.findAll(pageable);
			List<UserDTO>userList = new ArrayList<UserDTO>();
			for(Users user : users) {
				UserDTO dto = UserDTO.toDTO(user);
				userList.add(dto);
			}
			return new ResponseDTO(userList, new PageDTO(users.getSize(), users.getTotalElements(), users.getTotalPages()));
		}catch(Exception e) {
			throw e;
		}
	}
	
	public ResponseDTO getUser() throws Exception{
		try {
			Users user = authDAO.auth(SecurityContextHolder.getContext().getAuthentication());
			return new ResponseDTO(UserDTO.toDTO(user), null);
		}catch(Exception e) {
			throw e;
		}
	}
	
	@Transactional
	public void modifyUser(UserDTO dto) throws Exception{
		try {
			Users user = authDAO.auth(SecurityContextHolder.getContext().getAuthentication());

			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			// 수정 로직
			user.setEmail((dto.getEmail() != null && !dto.getEmail().equals("")) ?
					dto.getEmail() : user.getEmail());
			user.setName((dto.getName() != null && !dto.getName().equals("")) ?
					dto.getName() : user.getName());
			user.setPw((dto.getPw() != null && !dto.getPw().equals("")) ?
					passwordEncoder.encode(dto.getPw()) : user.getPw());
			user.setPhone((dto.getPhone() != null && !dto.getPhone().equals("")) ?
					dto.getPhone() : user.getPhone());
			user.setAddr((dto.getAddr() != null && !dto.getAddr().equals("")) ?
					dto.getAddr() : user.getAddr());
			userRepository.save(user);
		}catch(Exception e) {
			throw e;
		}
	}

	public ResponseDTO getUserForRoot(Integer userId) throws Exception{
		try {
			if(!authDAO.authRoot(SecurityContextHolder.getContext().getAuthentication())){
				throw new Exception("관리자 권한 오류");
			}
			Optional<Users>user = userRepository.findById(userId);
			if(user.isEmpty()) {
				throw new Exception("조회할 유저가 없음");
			}
			return new ResponseDTO(UserDTO.toDTO(user.get()), null);
		}catch(Exception e) {
			throw e;
		}
	}
	
	@Transactional
	public void modifyUserForRoot(Integer userId, UserDTO dto) throws Exception{
		try {
			if(!authDAO.authRoot(SecurityContextHolder.getContext().getAuthentication())){
				throw new Exception("관리자 권한 오류");
			}
			Optional<Users>userOpt = userRepository.findById(userId);
			if(userOpt.isEmpty()) {
				throw new Exception("조회할 유저가 없음");
			}
			Users user = userOpt.get();
			// 수정 로직
			user.setEmail((dto.getEmail() != null && !dto.getEmail().equals("")) ?
					dto.getEmail() : user.getEmail());
			user.setName((dto.getName() != null && !dto.getName().equals("")) ?
					dto.getName() : user.getName());
			user.setPw((dto.getPw() != null && !dto.getPw().equals("")) ?
					dto.getPw() : user.getPw());
			user.setPhone((dto.getPhone() != null && !dto.getPhone().equals("")) ?
					dto.getPhone() : user.getPhone());
			user.setAddr((dto.getAddr() != null && !dto.getAddr().equals("")) ?
					dto.getAddr() : user.getAddr());
			userRepository.save(user);	
		}catch(Exception e) {
			throw e;
		}
	}
	
	@Transactional
	public void deleteMyself() throws Exception{
		try {
			Users user = authDAO.auth(SecurityContextHolder.getContext().getAuthentication());
			// 결제 내역 전부 null 처리
			List<SoldItems> soldItems = payRepository.findAllByUserId(user.getId());
			for(SoldItems payment : soldItems){
				payment.setUser(null);
				payRepository.save(payment);
			}
			// 그룹 결제 내역 전부 null 처리
			List<GroupItemPays> groupPays = groupBuyPayRepository.findByUserId(user.getId());
			for(GroupItemPays groupPayment : groupPays){
				groupPayment.setUser(null);
				groupBuyPayRepository.save(groupPayment);
			}
			Pageable pageable = Pageable.unpaged();
			// 후기 전부 null 처리
			Page<Comments> comments = commentRepository.findCommentsByUserId(user.getId(), pageable);
			for(Comments comment : comments){
				comment.setUser(null);
				commentRepository.save(comment);
			}
			// QNA 전부 null 처리
			Page<QNAs> qnas = qnaRepository.findAllByUserId(user.getId(), pageable);
			for(QNAs qna : qnas){
				qna.setUser(null);
				qnaRepository.save(qna);
			}

			// 추후에 다른 항목들도 삭제해야 될 수도 있음.
			userRepository.delete(user);
		}catch(Exception e) {
			throw e;
		}
	}
	
	@Transactional
	public void deleteUser(Integer userId) throws Exception{
		try {
			if(!authDAO.authRoot(SecurityContextHolder.getContext().getAuthentication())){
				throw new Exception("관리자 권한 오류");
			}

			Optional<Users> userOpt = userRepository.findById(userId);
			Optional<Wallets> walletOpt = walletRepository.findByUser_Id(userId);
			if(userOpt.isEmpty()){
				throw new Exception("유저 정보 오류");
			}
			Users user = userOpt.get();
			Wallets wallet = walletOpt.get();
			// 결제 내역 전부 null 처리
			List<SoldItems> soldItems = payRepository.findAllByUserId(user.getId());
			for(SoldItems payment : soldItems){
				payment.setUser(null);
				payRepository.save(payment);
			}
			// 그룹 결제 내역 전부 null 처리
			List<GroupItemPays> groupPays = groupBuyPayRepository.findByUserId(user.getId());

			for(GroupItemPays groupPayment : groupPays){
				groupPayment.setUser(null);
				groupBuyPayRepository.save(groupPayment);
			}
			Pageable pageable = Pageable.unpaged();
			// 후기 전부 null 처리
			Page<Comments> comments = commentRepository.findCommentsByUserId(user.getId(), pageable);
			for(Comments comment : comments){
				comment.setUser(null);
				commentRepository.save(comment);
			}
			// QNA 전부 null 처리
			Page<QNAs> qnas = qnaRepository.findAllByUserId(user.getId(), pageable);
			for(QNAs qna : qnas){
				qna.setUser(null);
				qnaRepository.save(qna);
			}
			System.out.println("!@3123");
			wallet.setUser(null);
			user.setWallets(null);
			walletRepository.delete(wallet);
			userRepository.delete(user);
		}catch(Exception e) {
			throw e;
		}
	}
	
	public ResponseDTO isSnsSigned(String email) throws Exception{
		try {
			Optional<Users> userOpt = userRepository.findByEmail(email);
			int response;
			if(userOpt.isEmpty()) {
				response = -1;
			}
			else if(userOpt.get().getSnsAuth() == false) {
				response = 0;
			}
			else {
				response = 1;
			}
			return new ResponseDTO(response, null);
		}catch(Exception e) {
			throw e;
		}
	}
}
