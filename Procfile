web: java $JAVA_OPTS -Dserver.port=$PORT -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar $(ls target/*.jar | grep -v .original | head -n 1)
