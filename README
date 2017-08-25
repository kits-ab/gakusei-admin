#Gakusei-Admin

In order to build this project one must first build Gakusei: https://github.com/kits-ab/gakusei
However one must change the spring-boot plugin in pom.xml from:
	    <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <addResources>true</addResources>
                </configuration>
            </plugin>

to:
	    <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <layout>MODULE</layout>
                    <addResources>true</addResources>
                </configuration>
            </plugin>

In order to provide a proper jar, which should automatically be present in ~/.m2 
