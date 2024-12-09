package com.arthall.modam.service;

import java.util.Optional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arthall.modam.dto.UserDto;
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.entity.RewardsEntity;
import com.arthall.modam.repository.UserRepository;
import com.arthall.modam.repository.RewardsRepository;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EnumType;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RewardsRepository RewardsRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
            RewardsRepository RewardsRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.RewardsRepository = RewardsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // id로 찾기찾기
    public UserEntity findById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
    }

    // ==================================회원가입 처리====================================
    // 아이디 중복 체크
    public boolean isLoginIdDuplicate(String loginId) {
        return userRepository.findByLoginId(loginId).isPresent();
    }

    // 이메일 중복 체크
    public boolean isEmailDuplicate(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // 전화번호 중복 체크
    public boolean isPhoneNumberDuplicate(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    public void registerUser(UserDto userDto) {
        System.out.println("회원가입 요청 처리 중: " + userDto);

        // 아이디 중복 체크
        if (isLoginIdDuplicate(userDto.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 이메일 중복 체크
        if (isEmailDuplicate(userDto.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }
        // 전화번호 중복 체크
        if (isPhoneNumberDuplicate(userDto.getPhoneNumber())) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encodedPassword);

        // DTO -> Entity 변환
        UserEntity userEntity = userDto.toEntity();
        userEntity.setRole(UserEntity.Role.USER); // 기본값 설정
        userEntity.setProvider("LOCAL"); // 로컬 사용자로 설정
        userEntity.setStatus(UserEntity.Status.ACTIVE); // 계정 상태 활성화

        // 저장
        userRepository.save(userEntity);
        System.out.println("새 사용자 등록: " + userDto.getLoginId());
        // Rewards 생성
        RewardsEntity rewards = new RewardsEntity();
        rewards.setUserId(userEntity.getId()); // 저장된 userEntity의 ID 사용
        rewards.setTotalPoint(BigDecimal.ZERO); // 초기 적립금 0
        rewards.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        RewardsRepository.save(rewards);
    }

    // loginId로 사용자 ID 가져오기
    public int getUserIdByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .map(UserEntity::getId)
                .orElseThrow(() -> {
                    System.err.println("사용자를 찾을 수 없습니다: loginId = " + loginId);
                    return new RuntimeException("사용자를 찾을 수 없습니다: loginId = " + loginId);
                });
    }

    // 추가 메서드 예시: loginId로 사용자 정보 가져오기
    public UserEntity getUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: loginId = " + loginId));
    }

    // ID 기반 포인트 조회
    public BigDecimal getUserPointsById(int userId) {
        System.out.println("포인트 조회 중: User ID = " + userId);
        return RewardsRepository.findByUserId(userId)
                .map(RewardsEntity::getTotalPoint)
                .orElse(BigDecimal.ZERO); // 포인트가 없는 경우 0 반환
    }

    // loginId 기반 포인트 조회
    public BigDecimal getUserPointsByLoginId(String loginId) {
        UserEntity user = getUserByLoginId(loginId);
        return getUserPointsById(user.getId());
    }

    // 새 예외 클래스 추가
    public static class UserBannedException extends RuntimeException {
        public UserBannedException(String message) {
            super(message);
        }
    }

    // UserDetailsService의 메서드 구현 (Spring Security)
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다."));

        // BANNED 상태 확인
        if (userEntity.getStatus() == UserEntity.Status.BANNED) {
            throw new UserBannedException("계정이 정지되었습니다. 관리자에게 문의하세요.");
        }

        return User.builder()
                .username(userEntity.getLoginId())
                .password(userEntity.getPassword())
                .roles(userEntity.getRole().name())
                .build();
    }

    // =================================개인정보 수정
    // ======================================
    // UserService 내 추가
    public void updateUser(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

    // ============================관리자 계정 자동 생성===================================
    @PostConstruct
    public void createAdminUser() {
        if (userRepository.findByLoginId("admin").isEmpty()) {
            UserEntity adminUser = new UserEntity();
            adminUser.setLoginId("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123")); // 기본 비밀번호
            adminUser.setName("관리자");
            adminUser.setEmail("admin@example.com");
            adminUser.setPhoneNumber("010-1234-5678");
            adminUser.setStatus(UserEntity.Status.ACTIVE);
            adminUser.setRole(UserEntity.Role.ADMIN); // 관리자 권한 설정

            userRepository.save(adminUser);
            System.out.println("관리자 계정 생성 완료: admin");
        }
    }

    public boolean login(String loginId, String rawPassword) {
        return userRepository.findByLoginId(loginId)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPassword())) // 비밀번호 비교
                .orElse(false); // 사용자가 존재하지 않을 경우 false 반환
    }

    public boolean isAdmin(UserEntity user) {
        return user.getRole() == UserEntity.Role.ADMIN;
    }

    public UserEntity saveUser(UserEntity user) {
        UserEntity savedUser = userRepository.save(user);

        // 사용자 저장 시 적립금 기본값 설정
        RewardsEntity rewards = new RewardsEntity();
        rewards.setUserId(savedUser.getId());
        rewards.setTotalPoint(BigDecimal.ZERO);
        RewardsRepository.save(rewards);

        return savedUser;
    }

    // 이름과 이메일로 사용자 검색
    public Optional<UserEntity> findByNameAndEmail(String name, String email) {
        return userRepository.findByNameAndEmail(name, email);
    }

    // 아이디와 이메일로 사용자 검색
    public Optional<UserEntity> findByLoginIdAndEmail(String loginId, String email) {
        return userRepository.findByLoginIdAndEmail(loginId, email);
    }

    // 회원 ID로 회원 조회
    public UserEntity getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다. ID: " + userId));
    }

    // 회원 검색
    public Page<UserEntity> searchUsers(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            return userRepository.findAll(pageable); // 키워드가 없으면 전체 검색
        } else {
            return userRepository.findByNameContainingIgnoreCase(keyword, pageable); // 키워드로 검색
        }
    }

    // 회원 정보 수정
    public void updateUser(int userId, String loginId, String name, String email, String phoneNumber, String role,
            String status) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다. ID: " + userId));

        user.setLoginId(loginId);
        user.setName(name);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setRole(UserEntity.Role.valueOf(role.toUpperCase())); // 역할 설정 (ADMIN, USER 등)
        user.setStatus(UserEntity.Status.valueOf(status.toUpperCase())); // 상태 설정 (ACTIVE, BANNED)
        userRepository.save(user);
    }

    // 회원 삭제
    public void deleteUser(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("삭제할 회원 정보를 찾을 수 없습니다. ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    // 총 가입자 수 조회
    public long getTotalUsers() {
        return userRepository.countTotalUsers();
    }

    // 최근 1주일 동안 가입한 사용자 조회
    public Page<UserEntity> getUsersRegisteredInLastWeek(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        return userRepository.findUsersRegisteredInLastWeek(oneWeekAgo, pageable);
    }
}
