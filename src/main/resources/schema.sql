-- MySQL dump 10.13  Distrib 9.2.0, for macos15.2 (arm64)
--
-- Host: localhost    Database: payroll_system
-- ------------------------------------------------------
-- Server version	9.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `employees`
--

DROP TABLE IF EXISTS `employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employees` (
  `employee_id` varchar(10) NOT NULL,
  `user_id` varchar(10) DEFAULT NULL,
  `department` varchar(50) NOT NULL,
  `job_title` varchar(50) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `sur_name` varchar(50) DEFAULT NULL,
  `status` enum('ACTIVE','TERMINATED') NOT NULL,
  `date_of_birth` date NOT NULL,
  `gender` enum('MALE','FEMALE') NOT NULL,
  `pay_type` enum('SALARY','HOURLY') NOT NULL,
  `company_email` varchar(100) NOT NULL,
  `address_line1` varchar(100) NOT NULL,
  `address_line2` varchar(100) DEFAULT NULL,
  `city` varchar(50) NOT NULL,
  `state` varchar(2) NOT NULL,
  `zip` varchar(10) NOT NULL,
  `picture_path` varchar(255) DEFAULT NULL,
  `hire_date` date NOT NULL,
  `base_salary` decimal(10,2) DEFAULT NULL,
  `medical_coverage` enum('SINGLE','FAMILY') DEFAULT NULL,
  `dependents_count` int DEFAULT '0',
  PRIMARY KEY (`employee_id`),
  UNIQUE KEY `company_email` (`company_email`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `employees_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employees`
--

LOCK TABLES `employees` WRITE;
/*!40000 ALTER TABLE `employees` DISABLE KEYS */;
INSERT INTO `employees` VALUES ('EMP001',NULL,'IT','Senior Software Engineer','John','Smith',NULL,'ACTIVE','1985-01-15','MALE','SALARY','EMP001@company.com','','','','','','','2025-03-07',100000.00,'FAMILY',2),('EMP002',NULL,'HR','HR Manager','Sarah','Johnson',NULL,'ACTIVE','1980-02-01','FEMALE','SALARY','sarah.johnson@company.com','456 Oak Ave',NULL,'Carmel','IN','46032',NULL,'2025-03-07',85000.00,'FAMILY',2),('EMP003',NULL,'Finance','Financial Analyst','Michael','Brown',NULL,'ACTIVE','1988-03-10','MALE','SALARY','michael.brown@company.com','789 Pine Rd',NULL,'Fishers','IN','46037',NULL,'2025-03-07',95000.00,'SINGLE',0),('EMP004',NULL,'Marketing','Marketing Specialist','Emily','Davis',NULL,'ACTIVE','1990-04-05','FEMALE','SALARY','emily.davis@company.com','321 Maple Dr',NULL,'Noblesville','IN','46060',NULL,'2025-03-07',70000.00,'FAMILY',1),('EMP005',NULL,'Sales','Sales Manager','David','Wilson',NULL,'ACTIVE','1982-05-20','MALE','SALARY','david.wilson@company.com','654 Elm St',NULL,'Westfield','IN','46074',NULL,'2025-03-07',80000.00,'FAMILY',3),('EMP006',NULL,'Operations','Operations Specialist','Lisa','Anderson',NULL,'ACTIVE','1992-06-01','FEMALE','HOURLY','lisa.anderson@company.com','987 Cedar Ln',NULL,'Indianapolis','IN','46202',NULL,'2025-03-07',25.00,'SINGLE',0),('EMP007',NULL,'Customer Service','Customer Service Representative','Robert','Taylor',NULL,'ACTIVE','1987-07-15','MALE','HOURLY','robert.taylor@company.com','147 Birch Rd',NULL,'Carmel','IN','46032',NULL,'2025-03-07',22.50,'FAMILY',2),('EMP008',NULL,'Administration','Administrative Assistant','Jennifer','Martinez',NULL,'ACTIVE','1991-08-10','FEMALE','HOURLY','jennifer.martinez@company.com','258 Spruce Ave',NULL,'Fishers','IN','46037',NULL,'2025-03-07',20.00,'FAMILY',1),('EMP009',NULL,'IT','IT Support Specialist','Thomas','Garcia',NULL,'ACTIVE','1989-09-05','MALE','HOURLY','thomas.garcia@company.com','369 Willow St',NULL,'Noblesville','IN','46060',NULL,'2025-03-07',23.00,'SINGLE',0),('EMP010',NULL,'HR','HR Coordinator','Michelle','Lee',NULL,'ACTIVE','1993-10-01','FEMALE','HOURLY','michelle.lee@company.com','741 Ash Dr',NULL,'Westfield','IN','46074',NULL,'2025-03-07',21.50,'FAMILY',1),('EMP011',NULL,'Customer Service','Customer Service Representative','James','Rodriguez',NULL,'ACTIVE','1994-11-15','MALE','HOURLY','james.rodriguez@company.com','852 Poplar Rd',NULL,'Indianapolis','IN','46203',NULL,'2025-03-07',18.00,'SINGLE',0),('EMP012',NULL,'Administration','Administrative Assistant','Amanda','White',NULL,'ACTIVE','1995-12-01','FEMALE','HOURLY','amanda.white@company.com','963 Cherry Ln',NULL,'Carmel','IN','46032',NULL,'2025-03-07',17.50,'SINGLE',0);
/*!40000 ALTER TABLE `employees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payroll_records`
--

DROP TABLE IF EXISTS `payroll_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payroll_records` (
  `record_id` bigint NOT NULL AUTO_INCREMENT,
  `employee_id` varchar(10) NOT NULL,
  `pay_period_start` date NOT NULL,
  `pay_period_end` date NOT NULL,
  `gross_pay` decimal(10,2) NOT NULL,
  `net_pay` decimal(10,2) NOT NULL,
  `medical_deduction` decimal(10,2) DEFAULT NULL,
  `dependent_stipend` decimal(10,2) DEFAULT NULL,
  `state_tax` decimal(10,2) DEFAULT NULL,
  `federal_tax` decimal(10,2) DEFAULT NULL,
  `social_security_tax` decimal(10,2) DEFAULT NULL,
  `medicare_tax` decimal(10,2) DEFAULT NULL,
  `employer_social_security` decimal(10,2) DEFAULT NULL,
  `employer_medicare` decimal(10,2) DEFAULT NULL,
  `creation_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`),
  KEY `employee_id` (`employee_id`),
  CONSTRAINT `payroll_records_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payroll_records`
--

LOCK TABLES `payroll_records` WRITE;
/*!40000 ALTER TABLE `payroll_records` DISABLE KEYS */;
/*!40000 ALTER TABLE `payroll_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `time_entries`
--

DROP TABLE IF EXISTS `time_entries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `time_entries` (
  `entry_id` bigint NOT NULL AUTO_INCREMENT,
  `employee_id` varchar(10) NOT NULL,
  `work_date` date NOT NULL,
  `hours_worked` decimal(4,2) NOT NULL,
  `is_pto` tinyint(1) DEFAULT '0',
  `is_locked` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`entry_id`),
  KEY `employee_id` (`employee_id`),
  CONSTRAINT `time_entries_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `time_entries`
--

LOCK TABLES `time_entries` WRITE;
/*!40000 ALTER TABLE `time_entries` DISABLE KEYS */;
/*!40000 ALTER TABLE `time_entries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` varchar(10) NOT NULL,
  `password` varchar(255) NOT NULL,
  `user_type` enum('ADMIN','EMPLOYEE') NOT NULL,
  `email` varchar(100) NOT NULL,
  `employee_id` varchar(10) DEFAULT NULL,
  `must_change_password` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  KEY `employee_id` (`employee_id`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('EMP001','$2a$10$gcCBAsU09sIqQahS4uLkg.6jIu83L6Y3hGmbbHw6jrXOumkbcc36e','EMPLOYEE','john.smith@company.com','EMP001',1),('EMP002','$2a$10$ZAKemJ1geeiC8ZjVZQa02e4NkRTDH6z/22rkBOYJHk1VEWfEBQWBy','EMPLOYEE','sarah.johnson@company.com','EMP002',1),('EMP003','$2a$10$5ak6SmBrdEYRfq.nLlhhz.E1BCSsHtKCv8ncdQRuZkaANlC3fxDvq','EMPLOYEE','michael.brown@company.com','EMP003',1),('EMP004','$2a$10$LHvMM2rVNeyCydzhXYK9vuHndpTSc//kK7NH/mofytYgcyYWIy43.','EMPLOYEE','emily.davis@company.com','EMP004',1),('EMP005','$2a$10$uOmYueYUDWuLY/LhxPVhqOIIhKMLo/XIRxIFF2oQUM5oT1luCW0HS','EMPLOYEE','david.wilson@company.com','EMP005',1),('EMP006','$2a$10$s51sx9LLXuJIUKrPYmOBxu2a8sQN0yxX2zFWa8zkdDai/cqN4jEuq','EMPLOYEE','lisa.anderson@company.com','EMP006',1),('EMP007','$2a$10$JxP.ocutsoWHDiEAJxKpoOHRUE/cOYeV58oVKPoY8XImzmXUZs4dy','EMPLOYEE','robert.taylor@company.com','EMP007',1),('EMP008','$2a$10$IufzsaaE8MfOGNA0B5GkquAqUV7OnuOuQL7JlY/f272THFWkHn52C','EMPLOYEE','jennifer.martinez@company.com','EMP008',1),('EMP009','$2a$10$Bbj.NyG7q1pNXR0Kh7rnR.Su8zGDk/gmFSK1uY/HRzia8DhVbjTki','EMPLOYEE','thomas.garcia@company.com','EMP009',1),('EMP010','$2a$10$RHKCIUjw4dDMmLpAR9H06.LB5PkfTwrZjTeXuzpWkCaGqvRngX0VW','EMPLOYEE','michelle.lee@company.com','EMP010',1),('EMP011','$2a$10$ivrv/5LOhXKJWQQw6QNlpu7OVCUctKbrbwyjeBsb2NkMlR358hSp.','EMPLOYEE','james.rodriguez@company.com','EMP011',1),('EMP012','$2a$10$3xDajd4NNYts78WzTFaicO5RwH9oFE9KIL9Tie191s9VthtDjrBe.','EMPLOYEE','amanda.white@company.com','EMP012',1),('HR0001','$2a$10$xJBUzwizCyJuo87xGibq0Oob0Th5MfZicntYWxsMWgDtN.HhiamhC','ADMIN','admin@abccompany.com',NULL,0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
