package com.arthall.modam.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import com.arthall.modam.dto.UserDto;
import com.arthall.modam.entity.NoticesEntity;
import com.arthall.modam.service.BbsService;
import com.arthall.modam.service.UserService;

@Controller
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final MailService mailService;
    @Autowired
    private BbsService bbsService;

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
    public String loginUser(@RequestParam("loginId") String loginId, @RequestParam("password") String password,
            RedirectAttributes redirectAttributes, Model model) {
        UserEntity user = userService.getUserByLoginId(loginId);

        // 사용자가 존재하지 않을 경우
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("loginError", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "login";
        }

        // 계정이 BANNED 상태일 경우 처리
        if (user.getStatus() == UserEntity.Status.BANNED) {
            model.addAttribute("loginError", "계정이 정지되었습니다. 관리자에게 문의하세요.");
            return "login";
        }

        // 로컬 사용자 로그인 세션 처리
        return "redirect:/"; // 로그인 성공 시 메인 페이지로 이동
    }

    // =================================개인정보 수정 폼
    // 표시====================================
    @GetMapping("/registeruserEdit")
    public String showRegisterUserEditPage(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // 인증되지 않은 사용자는 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }
        Object principal = authentication.getPrincipal();
        String loginId = null;
        // 소셜 로그인 사용자
        if (principal instanceof DefaultOAuth2User) {
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) principal;
            loginId = (String) oAuth2User.getAttributes().get("loginId");
        }
        // 로컬 로그인 사용자
        else if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            loginId = userDetails.getUsername();
        }

        if (loginId == null) {
            throw new RuntimeException("로그인 ID를 찾을 수 없습니다.");
        }

        // 로그인된 사용자 정보 가져오기
        UserEntity userEntity = userService.getUserByLoginId(loginId);
        model.addAttribute("user", userEntity);
        return "registeruserEdit";
    }

    // ===============================개인정보 수정 처리=============================
    @PostMapping("/registeruserEdit")
    public String updateUserInfo(@ModelAttribute UserDto userDto, Authentication authentication,
            RedirectAttributes redirectAttributes, Model model) {
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
            }
            if (!userDto.getEmail().matches("^[a-zA-Z0-9._]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}$")) {
                model.addAttribute("emailError", "이메일 형식이 올바르지 않습니다.");
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
            } else if (!userDto.getPhoneNumber().matches("^\\d{10,11}$")) {
                model.addAttribute("phoneError", "전화번호 형식이 올바르지 않습니다.");
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
    public String findAccount(Model model) {
        return ("findAccount");
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
    public String findPassword(@RequestParam("loginId") String loginId, @RequestParam("email") String email,
            Model model) {
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

    // 일반 사용자용 공지사항 목록 조회 (페이지네이션 적용)
    @GetMapping("/userNoticeList")
    public String showUserNoticeList(Model model, @RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 5; // 페이지당 표시할 공지사항 수
        Page<NoticesEntity> notices = bbsService.getNotices(page, pageSize);

        model.addAttribute("notices", notices); // Page 객체 자체를 모델에 추가
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", notices.getTotalPages()); // 전체 페이지 수 전달

        return "userNoticeList"; // 일반 사용자용 템플릿 반환
    }

    // 공지사항 상세 조회 페이지
    @GetMapping("/userNoticeView")
    public String showNoticeView(@RequestParam("id") int id, Model model) {
        NoticesEntity notice = bbsService.getNoticeById(id).orElse(null);
        if (notice == null) {
            return "redirect:/userNoticeList"; // 공지사항이 없을 경우 목록으로 리다이렉트
        }
        model.addAttribute("notice", notice);
        return "userNoticeView"; // 일반 사용자용 템플릿 반환
    }

}
