package com.arthall.modam.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.arthall.modam.dto.UserDto;
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입 처리
    public void registerUser(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setLoginId(userDto.getLoginId());
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword())); // 비밀번호 암호화
        userEntity.setName(userDto.getName());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPhoneNumber(userDto.getPhoneNumber());
        userEntity.setRole(UserEntity.Role.USER);
        userRepository.save(userEntity);
    }

    public boolean login(String loginId, String rawPassword) {
        Optional<UserEntity> user = userRepository.findByLoginId(loginId);
    
        if (user.isPresent()) {
            System.out.println("사용자 찾음: " + user.get().getLoginId());
            if (passwordEncoder.matches(rawPassword, user.get().getPassword())) {
                System.out.println("비밀번호 일치");
                return true;
            } else {
                System.out.println("비밀번호 불일치");
            }
        } else {
            System.out.println("사용자 없음: " + loginId);
        }
        return false;
    }
    

    // UserDetailsService 인터페이스의 loadUserByUsername 메서드 구현
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        System.out.println("Authenticating user: " + loginId); // 디버깅용 로그 추가

        UserEntity userEntity = userRepository.findByLoginId(loginId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with loginId: " + loginId));
        
        System.out.println("User found: " + userEntity.getLoginId()); // 디버깅용 로그 추가

        return User.builder()
                .username(userEntity.getLoginId())
                .password(userEntity.getPassword())
                .roles(userEntity.getRole().name())  // 역할 설정
                .build();
    }
}