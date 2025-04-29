##🚆 TRAIN RESERVATION SYSTEM

   A basic train ticket reservation system built using Java, MySQL, and tools like Eclipse IDE and MySQL Workbench. It allows users to register, log in, view available trains, make 
   reservations, and manage schedules.

##🛠️ TECH STACK
   - Java (Console Application
   - MySQL (Database)
   - JDBC (Database Connectivity)
   - Eclipse IDE
   - MySQL Workbench

   
##📁 PROJECT STRUCTURE
             src/
             
             ├── main/
             
             │    ├── java/
             
             │    │    └── com.trainreservation/
             
             │    │         ├── controller/
             
             │    │         ├── model/
             
             │    │         ├── util/
             
             │    │         └── view/
             
             │    └── resources/
             
             │         └── config.properties
             └── test/
             
##🛠  DATABASE NAME
   train_reservation_system

##🔌 HOW TO CONNECT
  Host: localhost

 Port: 3306 (default MySQL port)

 Database: train_reservation_system

 Username: root (or your MySQL username)

 Password: your MySQL password

    
##🚀 HOW TO RUN

  1. **Set up MySQL database**
   - Run the SQL file from `sql/train_reservation_schema.sql` in MySQL Workbench

  2. **Import the project into Eclipse**
   - Use "Import Existing Java Project" option

  3. **Add MySQL JDBC Driver**
   - Add `mysql-connector-java.jar` to your project’s build path

  4. **Run the application**
   - Start with `Main.java` in the `ui` package
     

##✅ FEATURES
  - Admin and User login
  - View available trains and schedules
  - Book and cancel reservations
  - View reservation history

##📝 CONCLUSION
    This console-based Java application provides a complete backend simulation of a train reservation system. It uses proper object-oriented practices, real-time MySQL database 
    integration, and allows for easy extension into a GUI or web application in the future.







