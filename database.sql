-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: blood_bank
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bloodstock`
--

DROP TABLE IF EXISTS `bloodstock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bloodstock` (
  `stock_id` int NOT NULL AUTO_INCREMENT,
  `blood_group` varchar(5) NOT NULL,
  `donation_date` date NOT NULL,
  `expiration_date` date NOT NULL,
  `donor_id` int NOT NULL,
  `status` enum('Available','Issued','Discarded','Expired') DEFAULT 'Available',
  PRIMARY KEY (`stock_id`),
  KEY `donor_id` (`donor_id`),
  CONSTRAINT `bloodstock_ibfk_1` FOREIGN KEY (`donor_id`) REFERENCES `donors` (`donor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bloodstock`
--

LOCK TABLES `bloodstock` WRITE;
/*!40000 ALTER TABLE `bloodstock` DISABLE KEYS */;
INSERT INTO `bloodstock` VALUES (1,'A+','2026-03-21','2026-05-02',3,'Issued'),(2,'A+','2026-03-21','2026-05-02',4,'Issued'),(3,'AB+','2026-03-21','2026-05-02',5,'Available'),(4,'A+','2026-03-21','2026-05-02',3,'Discarded'),(5,'AB+','2026-03-23','2026-05-04',6,'Available'),(6,'O+','2026-03-23','2026-05-04',7,'Issued'),(7,'O+','2026-03-23','2026-05-04',7,'Available'),(8,'A+','2026-03-26','2026-04-11',3,'Discarded'),(9,'A+','2026-03-27','2026-05-08',3,'Discarded'),(10,'A+','2026-03-27','2026-04-11',8,'Expired'),(11,'AB+','2026-04-08','2026-05-20',9,'Issued'),(12,'A+','2026-04-13','2026-05-25',10,'Available'),(13,'B+','2026-04-13','2026-05-25',11,'Available'),(14,'B-','2026-04-13','2026-05-25',12,'Available'),(15,'A-','2026-04-13','2026-05-25',13,'Available'),(16,'A+','2026-04-13','2026-05-25',10,'Available'),(17,'B+','2026-04-13','2026-05-25',11,'Available');
/*!40000 ALTER TABLE `bloodstock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `donors`
--

DROP TABLE IF EXISTS `donors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `donors` (
  `donor_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `age` int NOT NULL,
  `blood_group` varchar(5) NOT NULL,
  `phone` varchar(15) NOT NULL,
  `last_donation_date` date DEFAULT NULL,
  `medical_issue` varchar(100) DEFAULT 'None',
  PRIMARY KEY (`donor_id`),
  UNIQUE KEY `phone` (`phone`),
  CONSTRAINT `donors_chk_1` CHECK (((`age` >= 18) and (`age` <= 65)))
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `donors`
--

LOCK TABLES `donors` WRITE;
/*!40000 ALTER TABLE `donors` DISABLE KEYS */;
INSERT INTO `donors` VALUES (3,'ABC',20,'A+','1234567890','2026-03-27','CRITICAL FLAG: Hepatitis B'),(4,'GHI',35,'A+','2345678901','2026-03-21','None'),(5,'BCD',45,'AB+','9801892444','2026-03-21','None'),(6,'BCDFG',56,'AB+','5678901234','2026-03-23','None'),(7,'aditya',20,'O+','7983903952','2026-03-23','pain'),(8,'adi',23,'A+','9876543287','2026-03-27','None'),(9,'Prototype',25,'AB+','9999999999','2026-04-08','CRITICAL FLAG: Hepatitis B'),(10,'EFG',25,'A+','1111111111','2026-04-13','None'),(11,'HIJ',22,'B+','2222222222','2026-04-13','None'),(12,'KLM',35,'B-','3333333333','2026-04-13','Cough'),(13,'NOP',34,'A-','4444444444','2026-04-13','None');
/*!40000 ALTER TABLE `donors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `request`
--

DROP TABLE IF EXISTS `request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `request` (
  `request_id` int NOT NULL AUTO_INCREMENT,
  `blood_group` varchar(5) NOT NULL,
  `patient_name` varchar(50) NOT NULL,
  `request_date` date NOT NULL,
  `issued_stock_id` int NOT NULL,
  PRIMARY KEY (`request_id`),
  KEY `issued_stock_id` (`issued_stock_id`),
  CONSTRAINT `request_ibfk_1` FOREIGN KEY (`issued_stock_id`) REFERENCES `bloodstock` (`stock_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `request`
--

LOCK TABLES `request` WRITE;
/*!40000 ALTER TABLE `request` DISABLE KEYS */;
INSERT INTO `request` VALUES (1,'A+','DEF','2026-03-21',1),(2,'AB+','MKZHS','2026-03-23',2),(3,'O+','Rawat','2026-03-27',6),(4,'AB+','John','2026-04-08',11);
/*!40000 ALTER TABLE `request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `role` varchar(20) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('admin','admin123','ADMIN'),('staff1','pass123','STAFF');
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

-- Dump completed on 2026-04-14 13:57:01
