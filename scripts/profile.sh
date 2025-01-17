#!/usr/bin/env bash

function find_idle_profile()
{
    # curl 결과로 연결할 서비스 결정
#    RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://15.164.111.12:8080/profile)
    CURRENT_PROFILE=$(curl 15.164.111.12/profile)

    # CURRENT_PROFILE 값 출력
    echo "CURRENT_PROFILE: ${CURRENT_PROFILE}"

    # IDLE_PROFILE : nginx와 연결되지 않은 profile
    if [ "${CURRENT_PROFILE}" == "real1" ]
    then
      IDLE_PROFILE=real2
    else
      IDLE_PROFILE=real1
    fi

    # bash script는 값의 반환이 안된다.
    # echo로 결과 출력 후, 그 값을 잡아서 사용한다.
    echo "${IDLE_PROFILE}"
}

# 쉬고 있는 profile의 port 찾기
function find_idle_port()
{
    IDLE_PROFILE=$(find_idle_profile)

    if [ "${IDLE_PROFILE}" == "real1" ]
    then
      echo "8081"
    else
      echo "8082"
    fi
}
