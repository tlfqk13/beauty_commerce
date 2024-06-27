#!/usr/bin/env bash

# health.sh

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh
source ${ABSDIR}/switch.sh

IDLE_PORT=$(find_idle_port)

echo "> Health Check Start"
echo "> IDLE_PORT: $IDLE_PORT"
echo "> curl -s http://localhost:$IDLE_PORT/profile "
sleep 10

for RETRY_COUNT in {1..10}; do
  RESPONSE=$(curl -s http://localhost:"${IDLE_PORT}"/profile)
  UP_COUNT=$(echo ${RESPONSE} | grep "real" | wc -l)

  echo "> current response ${RESPONSE}"

  if [ ${UP_COUNT} -ge 1 ]; then # $up_count >= 1
    echo "> Health check 성공"

    # 기존 포트에서 idle port로 switch

    echo "> 전환할 Port: $IDLE_PORT"
    echo "> Port 전환"
    echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" | sudo tee /etc/nginx/conf.d/service-prod-url.inc
    echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" | sudo tee /etc/nginx/conf.d/service-prod-url.inc

    echo "> 엔진엑스 Reload"
    ssh sudo service nginx reload
    ssh sudo service nginx reload
    break
  else
    echo "> Health check의 응답을 알 수 없거나 혹은 실행 상태가 아닙니다."
    echo "> current response_error ${RESPONSE}"
  fi

  if [ "${RETRY_COUNT}" -eq 10 ]; then
    echo "> Health check 실패. "
    echo "> 엔진엑스에 연결하지 않고 배포를 종료합니다."
    exit 1
  fi

  echo "> Health check 연결 실패. 재시도..."
  sleep 10
done
