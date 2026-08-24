# ec2 배포 스크립트
#!/bin/bash

# 배포 경로설정
BASE_PATH="/home/ec2-user/docker"
cd $BASE_PATH
echo "> 배포 경로: $BASE_PATH"

# 현재 구동중인 컨테이너(PORT 8081(Blue)) 확인
echo "> 현재 구동중인 bookmarker-app-blue 컨테이너 찾기"
EXIST_BLUE=$(docker ps -q -f name=bookmarker-app-blue)
echo "> 컨테이너 ID: $EXIST_BLUE"

echo "> 컨테이너 ID 길이 체크 0=8081, 0!=8082"
if [ -z "$EXIST_BLUE" ]; then
  echo "> [BLUE] 배포 시작 (포트: 8081)"
  TARGET_PORT=8081
  TARGET_UP="blue"
  DELETE_UP="green"
else
  echo "> [GREEN] 배포 시작 (포트: 8082)"
  TARGET_PORT=8082
  TARGET_UP="green"
  DELETE_UP="blue"
fi
echo "> 컨테이너 ID: $EXIST_BLUE 실행중인 타겟: $DELETE_UP , 빌드 대상 타겟: $TARGET_UP"

# 컨테이너 빌드 및 실행
echo "> docker-compose.${TARGET_UP}.yml 실행"
docker compose -f docker-compose.${TARGET_UP}.yml up -d --build

# Health Check (최대 10회, 5초 간격)
echo "> Health Check 시작 10회 반복 (http://127.0.0.1:${TARGET_PORT}/actuator/health)"
for RETRY_COUNT in {1..10}
do
  RESPONSE=$(curl -s http://127.0.0.1:${TARGET_PORT}/actuator/health)
  UP_COUNT=$(echo $RESPONSE | grep 'UP' | wc -l)

  # up이 1개 이상이면 성공
  if [ $UP_COUNT -ge 1 ]; then
    echo "> Health Check 성공!"
    break
  else
    echo "> Health Check 대기 중... ($RETRY_COUNT/10)"
    sleep 5
  fi

  # 실패 횟수가 10회면 컴포즈 종료
  if [ $RETRY_COUNT -eq 10 ]; then
    echo ">>> Health Check 실패! 배포를 중단하고 신규 컨테이너를 종료합니다."
    docker compose -f docker-compose.${TARGET_UP}.yml down
    exit 1
  fi
done

# Nginx 타겟 포트 스위칭 및 Reload
# service-url에 작성된 타겟 주소(포트) 수정하여 Reload
echo "set \$service_url http://127.0.0.1:${TARGET_PORT};" | sudo tee /etc/nginx/conf.d/service-url.inc
sudo systemctl reload nginx
echo ">>> Nginx 포트 스위칭 완료 -> $TARGET_PORT , $TARGET_UP "

# 기존 구버전 컨테이너 종료
echo ">>> 구버전(${DELETE_UP}) 컨테이너 종료"
docker compose -f docker-compose.${DELETE_UP}.yml down