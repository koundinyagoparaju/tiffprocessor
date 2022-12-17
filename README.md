Tiff Analyser
--

Stack:
-
- Backend - Java 19, Spring Boot 3.0.0
- Frontend - React.js

Functionality:
- To run the application,
    1. If jdk 19 is installed in the system, run the command `./mvnw spring-boot:run` in *unix based systems and `.\mvnw.cmd spring-boot:run` for windows based systems.
    2. If docker is installed in the system, run the command `./run.sh` in *unix based systems and `.\run.cmd` for windows based systems.

Assumptions:
-
- All frames in the tiff are of same size

Note:
- 
- For the sake of showing in browser, the processed image is converted to PNG since browser support for showing TIFF image is limited (Only Safari, among major browsers, supports TIFF images).
- Currently max file size is set to 3GB, In case bigger files are uploaded, increase the limit which is set in `src/main/resources/application.properties` 




