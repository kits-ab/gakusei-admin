# Gakusei-Admin

In order to build this project one must first build Gakusei: https://github.com/kits-ab/gakusei using `mvn clean install -P build`.
This should result in a Jar being present in ~/.m2 and Gakusei-Admin being able to find its dependencies.

### Package it up for deployment

Will create a single .jar-file with all resources embedded. As such, front-end resources must be compiled to the back-end. Make sure you have a front-end (designed to work with this [one](https://github.com/kits-ab/gakusei-admin-fe)). There is a pre-made maven profile for this purpose.

 
- Do `mvn clean package "-Dfrontend=/where/your/frontend/is" -Pproduction` to install npm packages, compile front-end and back-end and then package the .jar-file to `target/`.
 
- Server can then be started using the packaged .jar-file, sample commands can be seen below:

    - `nohup java -jar gakusei-admin.jar &> server.log &` for in-memory db, that clears on restart.

    - `nohup java -jar gakusei-admin.jar --spring.profiles.active="postgres, enable-resource-caching" &> server.log &` for postgres


