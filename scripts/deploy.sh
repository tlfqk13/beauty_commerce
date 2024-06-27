#!/usr/bin/env bash
# 현재 port 찾기
function find_current_port() {
  CURRENT_PROFILE=$(curl 15.164.111.12/profile)

  if [ "${CURRENT_PROFILE}" == real1 ]; then
    echo "8081"
  else
    echo "8082"
  fi
}

# 쉬고 있는 profile 찾기: dev1이 사용중이면 dev2가 쉬고 있고, 반대면 dev1이 쉬고 있음
function find_idle_profile() {
  CURRENT_PROFILE=$(curl 15.164.111.12/profile)

  if [ "${CURRENT_PROFILE}" == real1 ]; then
    IDLE_PROFILE=real2
  else
    IDLE_PROFILE=real1
  fi

  echo "${IDLE_PROFILE}"
}

# 쉬고 있는 profile의 port 찾기
function find_idle_port() {
  IDLE_PROFILE=$(find_idle_profile)
  if [ "${IDLE_PROFILE}" == real1 ]; then
    echo "8081"
  else
    echo "8082"
  fi
}

# 서버 구동을 위한 스크립트
###########################################################################
REPOSITORY=/home/ubuntu/app
PROJECT_NAME=step2

cd "$REPOSITORY/$PROJECT_NAME/" || exit 1

echo "> 새 어플리케이션 배포"
JAR_NAME=$(ls -tr $REPOSITORY/$PROJECT_NAME/*.jar | tail -n 1)
JAR_NAME=$(basename "$JAR_NAME")

echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

pwd

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

CURRENT_PORT=$(find_current_port)
echo ">CURRENT_PORT : $CURRENT_PORT"
IDLE_PROFILE=$(find_idle_profile)
echo ">IDLE_PROFILE : $IDLE_PROFILE"
IDLE_PORT=$(find_idle_port)
echo ">IDLE_PORT : $IDLE_PORT"

echo "> $JAR_NAME 를 profile=$IDLE_PROFILE 로 실행합니다."
nohup java -jar -Djasypt.encryptor.password=sampleroad1! \
     $JAR_NAME \
    --spring.config.location=classpath:/application-$IDLE_PROFILE.yml \
    --spring.profiles.active=$IDLE_PROFILE >$REPOSITORY/$PROJECT_NAME/nohup.out 2>&1 &


# health check
echo "> Health Check Start!"
echo "> IDLE_PORT: $IDLE_PORT"
echo "> curl -s http://127.0.0.1:$IDLE_PORT/profile "
sleep 10

for RETRY_COUNT in {1..10}; do
  echo ">CURRENT_PORT : $CURRENT_PORT"
  RESPONSE=$(curl -s http://localhost:"${CURRENT_PORT}"/profile)
  UP_COUNT=$(echo ${RESPONSE} | grep "real" | wc -l)

  if [ ${UP_COUNT} -ge 1 ]; then # $up_count >= 1
    echo "> Health check 성공"

    # 기존 포트에서 idle port로 switch

    echo "> 전환할 Port: $IDLE_PORT"
    echo "> Port 전환"
    echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" | sudo tee /etc/nginx/conf.d/service-url.inc

    echo "> 엔진엑스 Reload"
    sudo systemctl reload nginx
    break

  else
    echo "> Health check의 응답을 알 수 없거나 혹은 실행 상태가 아닙니다."
    echo "> Health check: ${RESPONSE}"
  fi

  if [ ${RETRY_COUNT} -eq 10 ]; then
    echo "> Health check 실패. "
    echo "> 엔진엑스에 연결하지 않고 배포를 종료합니다."
    exit 1
  fi

  echo "> Health check 연결 실패. 재시도..."
  sleep 10
done

# 기존 포트 stop

CURRENT_PID=$(lsof -ti tcp:${CURRENT_PORT})
echo "> CURRENT_PID : ${CURRENT_PID}"

if [ -z ${CURRENT_PID} ]; then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 ${CURRENT_PID}
  sleep 5
fi
