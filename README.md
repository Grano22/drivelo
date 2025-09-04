Car Fleet App "Drivelo"
===

🚀 How to run the app in the dev mode?

* Create application-dev.properties in the ./src/main/resources/ directory.
* Run `docker compose -f ./compose.yml ./compose.dev.yml up` to start a database.
* Visit frontend directory, then run `npm run start`
* Run `mvn spring-boot:run -Dspring-boot.run.profiles=dev` to start backend