# StudentCare

User Interface -project made for User Interfaces course at University Of Turku. Made with Vaadin Framework 8. Assignment was to make an interface for study management system.
All Vaadin classes are made by me (located in src\main\java\org\utu\studentcare), and all other classes (located in subdirectories, backend logic) are part of assignment.

## Installing and running

Dependency control is controlled by Maven, so installing it is necessary [](https://maven.apache.org/). 
Project can be downloaded with `git clone https://gitlab.utu.fi/arhutt/studentcare/`, and installation of it is done with `mvn install`.

To run a project, use command `mvn jetty:run`, which will start a local Jetty-server, and after startup program can be accessed in [localhost:8080](localhost:8080).

Pre-generated accounts are found in value4life.db, and here are few accounts for testing:

Admin: tunnus: kaeese , salasana: kp1234

Teacher: tunnus: daanad , salasana: dl1234

Student: tunnus: ilvijo , salasana: il1234
