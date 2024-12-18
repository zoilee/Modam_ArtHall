# 🎙️SpringBoot-project-ModamArthall🎙️
### SpringBoot 기반 공연 예매시스템

## 📖목차
### 1. [소개]
### 2. [기술스택]
### 3. [구축기능]
### 4. [ERD]
### 5. [상세페이지]
### 6. [참고 자료]

## 👀소개
#### 프로젝트 개요
- ModamArthall 프로젝트는 **기업요구를 반영한 프로젝트로** 기업에서 요구하는 기능 구현을 우선하여 개발
- 목표: 사용자가 실시간으로 공연 좌석을 예약하고, 편리하게 예약 내용을 관리할 수 있는 온라인 예약 관리 시스템 구축
- 타겟층: 뮤지컬 및 공연을 즐기는 다양한 연령층
#### 주요 목표
- 사용자는 다양한 공연 정보를 한눈에 확인하고, **실시간으로 좌석을 예약하고 조회**할 수 있는 시스템 구축을 통해 편리한 예약 제공.
- 사용자를 위한 **쉽고 직관적인 인터페이스** 제공.
- **유저/관리자용** 로그인 및 계정관리 시스템 구현.
- **적립금과 기간별 환불 수수료 시스템**을 포함한 결제 기능 구현.
#### 현재 진행 상황
- 서버 배포 완료 후 수정이 필요한 부분은 수정 중
## 🛠️기술 스택

### 💻 Language
![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=java&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-ES6%2B-F7DF1E?style=flat&logo=javascript&logoColor=black)

### ⚙️ Backend
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.5.4-6DB33F?style=flat&logo=spring-boot&logoColor=white)

### 🎨 Frontend
![HTML5](https://img.shields.io/badge/HTML5-5C5F67-E34F26?style=flat&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6-1572B6?style=flat&logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-ES6%2B-F7DF1E?style=flat&logo=javascript&logoColor=black)

### 🗃️ Database
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat&logo=mysql&logoColor=white)
![RDS](https://img.shields.io/badge/AWS%20RDS-Database-527FFF?style=flat&logo=amazon-rds&logoColor=white)

### ☁️ Cloud
![AWS](https://img.shields.io/badge/AWS-Cloud-F28D00?style=flat&logo=amazon-aws&logoColor=white)
![RDS](https://img.shields.io/badge/AWS%20RDS-Database-527FFF?style=flat&logo=amazon-rds&logoColor=white)
## 🔍구축기능

- 유저 / 관리자 SIDE별 기능
- 회원가입, 로그인, 내정보수정 기능
    - (*Google, Naver, Kakao* 등 Synchro 로그인)
- 예약현황, 예약 조회 기능
    - 예약 일정 등록 및 관리 기능
- 공연 정보 및 좌석 선택 기능
- 결제 및 조회 기능 (*PG사 연동*)
    - 예약 취소 시 기간 별 수수료 부과 기능
    - 적립금 시스템
- 게시판 등록 및 관리 기능

## 📊ERD
![modamERD](https://github.com/user-attachments/assets/0e2f5463-801b-4ad9-bdd2-255c3843a20a)
## 📑상세페이지
- 로그인 페이지
![modamarthall store_login](https://github.com/user-attachments/assets/625f4d33-58d8-4301-8881-05218ff27c9f)
- 소셜 api로그인을 활용하여 카카오, 네이버, 구글 로그인 가능

| ![kakaologin](https://github.com/user-attachments/assets/17363930-983a-4f2d-955b-aeab6ae3258d) | ![naverlogin](https://github.com/user-attachments/assets/0d29a44d-6ae5-4d22-82ea-f3efd389f3dd) | ![googlelogin](https://github.com/user-attachments/assets/e15446e7-60f0-4033-9821-7f5ffd538adc) |
|---|---|---|
| 카카오 로그인 | 네이버 로그인 | 구글 로그인 |

- 회원가입/아이디 또는 비밀번호 찾기 페이지
  - 데이터 베이스에 저장된 이름과 이메일이 일치할 경우 아이디를 알려주고, 아이디와 이메일이 일치할 경우 이메일로 임시 비밀번호 발송시킴. 발송 된 후 데이터 베이스는 임시 비밀번호로 자동 업데이트 되어 임시 비밀번호로 로그인 가능, 이후 개인정보수정 페이지에서 비밀번호 변경

| ![register](https://github.com/user-attachments/assets/23905db7-dcf9-48f7-89b7-e2c361b5db2a) | ![findAccount](https://github.com/user-attachments/assets/99a0a2b0-d5fd-4ada-a43c-2309fd563fc3) | ![find](https://github.com/user-attachments/assets/1f94067b-d436-4011-a0b0-6b7590b7c4f4) |
|---|---|---|
| 계정 찾기 | 회원가입 | 비밀번호 찾기 |
## 📚참고자료
- **[예술의 전당](https://www.sacticket.co.kr)**:
  - 메인 페이지 이미지 슬라이드 디자인 참고
- **[모두예술극장](https://www.moduartcenter.co.kr)**:
  - 전반적인 디자인 참고
- **[인터파크 티켓](https://tickets.interpark.com)**:
  - 공연 정보, 예매 페이지 참고

## 👨‍💻 Developer
- 오승안 [OurOh](https://github.com/OurOh)
- 오수석 [zoilee](https://github.com/zoilee)
- 이민형 [Minh137](https://github.com/Minh137)
- 제규진 [Ginie-J](https://github.com/Ginie-J)
- 박민정 [vMinJungPark](https://github.com/vMinJungPark)
