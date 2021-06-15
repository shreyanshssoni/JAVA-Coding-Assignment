# JAVA-Coding-Assignment
# How to run project
##### Navigate to the root of the project via command line and execute the command
mvn spring-boot:run
# Task 1 Curl
curl -X GET \
  http://localhost:8080/parent/detail/{pageNo} \
  -H 'cache-control: no-cache' \
  -H 'postman-token: d3784ad4-15ba-7a76-3ac2-12c19317cc00'
# Task 2 Curl
curl -X GET \
  http://localhost:8080/child/detail/{parentId} \
  -H 'cache-control: no-cache' \
  -H 'postman-token: 4523bf5e-d22c-3644-49a9-3c9df8bf0a38'
  
