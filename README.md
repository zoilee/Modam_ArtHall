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
- 사용자는 다양한 공연 정보를 한눈에 확인하고, 실시간으로 좌석을 예약하고 조회할 수 있는 시스템 구축을 통해 편리한 예약 제공.
- 사용자를 위한 **쉽고 직관적인 인터페이스** 제공.
- 유저/관리자용 로그인 및 계정관리 시스템 구현.
- 적립금과 기간별 환불 수수료 시스템을 포함한 결제 기능 구현.
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
- 로그인
![login](https://github.com/user-attachments/assets/f1be0aab-70b3-4ce3-b9b7-812ce087653a)
- 예약시스템
    - datepicker를 이용한 날짜선택
    - database에서 해당 회차의 가용 좌석을 검색해 출력
![reservform](https://github.com/user-attachments/assets/020c7305-f794-4fdb-b7b8-a065f79cadd2)
    - javascript로 좌석 생성 및 좌석번호 부여
    - 선택 인원만큼 좌석을 선택하지 않으면 경고창 출력
![seatselect](https://github.com/user-attachments/assets/f7a3a4ba-1ec5-4c14-8149-2c2f2c46d833)


## 📚참고자료
- **[예술의 전당](https://www.sacticket.co.kr)**:
  - 메인 페이지 이미지 슬라이드 디자인 참고
- **[모두예술극장](https://www.moduartcenter.co.kr)**:
  - 전반적인 디자인 참고
- **[인터파크 티켓](https://tickets.interpark.com)**:
  - 공연 정보, 예매 페이지 참고

## 👨‍💻 Developer
- 오승안
- 오수석
- 이민형
- 제규진
- 박민정
