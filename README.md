## **프로젝트 소개**
  기상 정보와 관광 산업을 통합하여 사용자에게 맞춤형 여행 경험을 제공하는 시스템을 개발하는 것을 목표로 한다. 기상산업의 성장과 관광 산업의 변화를 고려하여, 실시간 기상 데이터를 활용해 날씨에 적합한 관광지를 추천함으로써 사용자 만족도를 향상시키고자 한다. 이 시스템은 지리적 좌표를 기반으로 거리를 계산하고, 사용자가 사전에 제공한 선호도 정보를 비교 분석하여 개인화된 관광지 추천 리스트를 생성한다. 이를 통해 기존의 위치 기반 서비스의 한계를 넘어, 기상 조건을 고려한 맞춤형 여행 경험을 제공한다.
<br><br>

## 구현 기능

**1. 회원 관련 기능**
  - 회원가입
  - 로그인/로그아웃
  - 회원 선호도 조사

**2. 추천시스템**
   - 추천 알고리즘
   - 사용자 현재 위치 좌표 추출 및 근처 관광지 조회
   - 실시간 기상 정보 추출

**3. SNS**
   - 게시글 관리
   - 주소 자동 입력
<br>


## 실행 화면
### 회원 선호도 조사 
![선호도조사](https://github.com/seoy316/weatherable/blob/main/img/선호도조사폼.png) <br>

### 추천리스트
![추천리스트](https://github.com/seoy316/weatherable/blob/main/img/추천리스트.png) <br>

### 게시글 작성 
![게시글작성](https://github.com/seoy316/weatherable/blob/main/img/게시글작성.png) <br>


<br>

## 기술 스택
|분류|기술|
| :-: |:- |
|Language| <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white"> <img src="https://img.shields.io/badge/python-3776AB?style=for-the-badge&logo=python&logoColor=white"> |
|IDE| <img src="https://img.shields.io/badge/androidstudio-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=white"> |
|Framework|<img src="https://img.shields.io/badge/flask-000000?style=for-the-badge&logo=flask&logoColor=white"> 
|Build Tool| <img src="https://img.shields.io/badge/apachemaven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white"> |
|DB| <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> |
|Server| goorm |
|Collaboration| <img src="https://img.shields.io/badge/trello-0052CC?style=for-the-badge&logo=confluence&logoColor=white"> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/-googledocs-4285F4?style=for-the-badge&logo=googledocs&logoColor=white"> |

<br>

## **시스템구조**
![시스템구조](https://github.com/seoy316/weatherable/blob/main/img/시스템구조.png)


<br>

## **DB 설계 (ERD)**

![WEATHERABLE](https://github.com/seoy316/weatherable/blob/main/img/weatherable_erd.png)
<br><br>

## 역할
### 장서윤
- 추천 알고리즘 개발
- 추천 시스템 프론트 및 백엔드
- 게시글 관리 프론트 및 백엔드

### 장현영
- 장소 정보 크롤링
- 위치 기반 서비스 프론트 및 백엔드
- Google Places API를 활용한 자동 주소 입력

### 차채원
- Firebase를 이용한 사용자 인증 시스템 구현
- 회원가입 프론트엔드 및 백엔드
- OpenWeatherMap API를 활용한 날씨 정보 통합
