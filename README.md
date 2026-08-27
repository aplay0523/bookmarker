## 프로젝트 소개
- 도서관 정보나루 대용량 공공데이터 처리 최적화와 CI/CD 및 무중단 배포를 위한 백엔드 기술 중심 프로젝트입니다.

## 프로젝트 접속 링크
- http://ec2-52-64-0-111.ap-southeast-2.compute.amazonaws.com

## 시스템 아키텍처
<img width="1096" height="731" alt="시스템_아키텍처" src="https://github.com/user-attachments/assets/d3853b68-96b9-49cc-8699-f6bb2e00f177" />

## 기술 스택

### 백엔드
- Java 17
- Spring Boot 3.5.13
- Spring Security

### 디비 & 캐시
- PostgreSql
- Redis

### 환경
- Docker
- Nginx
- AWS EC2

## 주요기능
- 도서관 검색 네이버 지도 매핑
- 도서 검색 및 상세 정보 조회
- 소셜 로그인
- Nginx를 활용한 무중단 배포
- Git Actions활용한 CI/CD

## 주요 기술적 개선

### 1. 외부 API 병렬 처리
- 다수의 외부 API 순차 호출하면서 발생하는 응답 지연 문제 발생
- CompletableFuture와 전용 ThreadPool을 활용한 병렬 API 호출

### 2. Redis 캐싱 적용
- 로그인 사용자 session 캐시 적용
- 반복적으로 조회되는 도서 상세 데이터 Redis 캐시 적용

### 3. 무중단 배포 환경 구축
- Docker 기반 Blue-Green 배포 환경 구성 및 배포 compose 분리
- Nginx를 활용한 트래픽 전환
- 신규 컨테이너 실행 후 Spring Boot Health Check를 통한 정상 상태 확인
- Health Check 통과한 경우만 트래픽 전환 되도록 배포 스크립트 작성
