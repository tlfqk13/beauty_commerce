# 서버 구동을 위한 스크립트
###########################################################################
ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

REPOSITORY=/home/ubuntu/app
PROJECT_NAME=step2

cd $REPOSITORY/$PROJECT_NAME/

echo "> 새 어플리케이션 배포"
JAR_NAME=$(ls -tr $REPOSITORY/$PROJECT_NAME/*.jar | tail -n 1)
JAR_NAME=$(basename "$JAR_NAME")

echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

IDLE_PROFILE=$(find_idle_profile)

echo "> $JAR_NAME 를 profile=$IDLE_PROFILE 로 실행합니다."
nohup nohup java -jar $JAR_NAME \
          --spring.config.location=classpath:/application-$IDLE_PROFILE.yml \
          --spring.profiles.active=$IDLE_PROFILE > $REPOSITORY/$PROJECT_NAME/nohup.out 2>&1 &
