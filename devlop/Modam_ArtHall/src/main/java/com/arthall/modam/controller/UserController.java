package com.arthall.modam.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.arthall.modam.dto.UserDto;
import com.arthall.modam.entity.UserEntity;
import com.arthall.modam.service.MailService;
import com.arthall.modam.service.UserService;

@Controller
public class UserController {
    
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final MailService mailService;

    // 생성자 주입
    public UserController(PasswordEncoder passwordEncoder, UserService userService, MailService mailService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.mailService = mailService;
    }

    // 회원가입 폼 표시
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "register";
    }

    // HTML 회원가입 처리
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("userDto") UserDto userDto) {
        userService.registerUser(userDto);
        return "redirect:/login"; // 성공 시 로그인 페이지로 이동
    }

    // 로그인 폼 표시
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("loginId") String loginId, @RequestParam("password") String password, RedirectAttributes redirectAttributes, Model model) {
        UserEntity user = userService.getUserByLoginId(loginId);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            if (MailService.isTemporaryPassword(password)) { // 임시 비밀번호인지 확인
                redirectAttributes.addFlashAttribute("tempPasswordMessage", "임시 비밀번호로 로그인하셨습니다. 비밀번호를 변경해주세요.");
            }
            // 세션 저장 로직
            return "redirect:/";
        } else {
            model.addAttribute("loginError", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "login";
        }
    }
    
    // =================================개인정보 수정 폼 표시====================================
    @GetMapping("/registeruserEdit")
    public String showRegisterUserEditPage(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // 인증되지 않은 사용자는 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }

        // 로그인된 사용자 정보 가져오기
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String loginId = userDetails.getUsername();
        UserEntity userEntity = userService.getUserByLoginId(loginId);

        model.addAttribute("user", userEntity);
        return "registeruserEdit";
    }

    // 개인정보 수정 처리
    @PostMapping("/registeruserEdit")
    public String updateUserInfo(@ModelAttribute UserDto userDto, Authentication authentication, RedirectAttributes redirectAttributes, Model model
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // 로그인된 사용자 정보 가져오기
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String loginId = userDetails.getUsername();
        UserEntity existingUser = userService.getUserByLoginId(loginId);

        boolean hasError = false;

        // 비밀번호 업데이트
        if (userDto.getNewPassword() != null && !userDto.getNewPassword().isEmpty()) {
            if (!userDto.getNewPassword().equals(userDto.getConfirmPassword())) {
                model.addAttribute("errorMessage", "새로운 비밀번호가 일치하지 않습니다.");
                hasError = true;
            } else {
                existingUser.setPassword(passwordEncoder.encode(userDto.getNewPassword()));
            }
        }


        // 이름 업데이트
        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            existingUser.setName(userDto.getName());
        }

        // 이메일 중복 체크 및 업데이트
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            if (!userDto.getEmail().equals(existingUser.getEmail()) // 본인의 이메일은 중복으로 간주하지 않음
                && userService.isEmailDuplicate(userDto.getEmail())) {
                model.addAttribute("emailError", "이미 사용 중인 이메일입니다.");
                hasError = true;
            } else {
                existingUser.setEmail(userDto.getEmail());
            }
        }

        // 전화번호 중복 체크 및 업데이트
        if (userDto.getPhoneNumber() != null && !userDto.getPhoneNumber().isEmpty()) {
            if (!userDto.getPhoneNumber().equals(existingUser.getPhoneNumber()) // 본인의 전화번호는 중복으로 간주하지 않음
                && userService.isPhoneNumberDuplicate(userDto.getPhoneNumber())) {
                model.addAttribute("phoneError", "이미 사용 중인 전화번호입니다.");
                hasError = true;
            } else {
                existingUser.setPhoneNumber(userDto.getPhoneNumber());
            }
        }

        if (hasError) {
            model.addAttribute("user", existingUser);
            return "registeruserEdit"; // 에러가 있으면 수정 페이지로 리턴
        }

        userService.updateUser(existingUser);
        redirectAttributes.addFlashAttribute("successMessage", "개인정보가 성공적으로 수정되었습니다.");
        return "redirect:/registeruserEdit";
    }

    @GetMapping("/user/api/check-login-id")
    public ResponseEntity<Map<String, Boolean>> checkLoginId(@RequestParam(name = "loginId") String loginId) {
    boolean exists = userService.isLoginIdDuplicate(loginId);
    return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/user/api/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam(name = "email") String email) {
    boolean exists = userService.isEmailDuplicate(email);
    return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/user/api/check-phone")
    public ResponseEntity<Map<String, Boolean>> checkPhoneNumber(@RequestParam(name = "phone") String phoneNumber) {
    boolean exists = userService.isPhoneNumberDuplicate(phoneNumber);
    return ResponseEntity.ok(Map.of("exists", exists));
    }


// ================================아이디&비밀번호 찾기===============================
    @GetMapping("/findAccount")
    public String findAccount(Model model){
        return("findAccount");
    }

    @PostMapping("/find-id")
    public String findId(@RequestParam("name") String name, @RequestParam("email") String email, Model model) {
        Optional<UserEntity> optionalUser = userService.findByNameAndEmail(name, email);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            model.addAttribute("successMessage", user.getName() + "님의 아이디는 '" + user.getLoginId() + "' 입니다.");
        } else {
            model.addAttribute("errorMessage", "입력한 정보와 일치하는 계정을 찾을 수 없습니다.");
        }

        return "findAccount";
    }

    @PostMapping("/find-password")
    public String findPassword(@RequestParam("loginId") String loginId, @RequestParam("email") String email, Model model) {
        Optional<UserEntity> optionalUser = userService.findByLoginIdAndEmail(loginId, email);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            String temporaryPassword = MailService.generateTemporaryPassword();
            user.setPassword(passwordEncoder.encode(temporaryPassword));
            userService.updateUser(user);

            mailService.sendTemporaryPassword(email, temporaryPassword);
            model.addAttribute("successMessage", "임시 비밀번호가 이메일로 발송되었습니다.");
        } else {
            model.addAttribute("errorMessage", "입력한 정보와 일치하는 계정을 찾을 수 없습니다.");
        }

        return "findAccount";
    }
}
