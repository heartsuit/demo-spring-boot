-- MySQL dump 10.13  Distrib 5.7.27, for Linux (x86_64)
--
-- Host: localhost    Database: mybatis
-- ------------------------------------------------------
-- Server version	5.7.27-0ubuntu0.16.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `tb_class`
--

DROP TABLE IF EXISTS `tb_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_class` (
  `c_id` int(11) NOT NULL AUTO_INCREMENT,
  `c_name` varchar(20) DEFAULT NULL,
  `c_ht_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`c_id`),
  UNIQUE KEY `c_ht_id` (`c_ht_id`),
  CONSTRAINT `tb_class_ibfk_1` FOREIGN KEY (`c_ht_id`) REFERENCES `tb_head_teacher` (`ht_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_class`
--

LOCK TABLES `tb_class` WRITE;
/*!40000 ALTER TABLE `tb_class` DISABLE KEYS */;
INSERT INTO `tb_class` VALUES (1,'Class One',1),(2,'Class One',NULL);
/*!40000 ALTER TABLE `tb_class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_course`
--

DROP TABLE IF EXISTS `tb_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_course` (
  `c_id` int(11) NOT NULL AUTO_INCREMENT,
  `c_name` varchar(20) DEFAULT NULL,
  `c_credit` int(11) DEFAULT NULL,
  PRIMARY KEY (`c_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_course`
--

LOCK TABLES `tb_course` WRITE;
/*!40000 ALTER TABLE `tb_course` DISABLE KEYS */;
INSERT INTO `tb_course` VALUES (1,'Math',5),(2,'Computer',4);
/*!40000 ALTER TABLE `tb_course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_head_teacher`
--

DROP TABLE IF EXISTS `tb_head_teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_head_teacher` (
  `ht_id` int(11) NOT NULL AUTO_INCREMENT,
  `ht_name` varchar(20) DEFAULT NULL,
  `ht_age` int(11) DEFAULT NULL,
  PRIMARY KEY (`ht_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_head_teacher`
--

LOCK TABLES `tb_head_teacher` WRITE;
/*!40000 ALTER TABLE `tb_head_teacher` DISABLE KEYS */;
INSERT INTO `tb_head_teacher` VALUES (1,'ZhangSan',40);
/*!40000 ALTER TABLE `tb_head_teacher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_select_course`
--

DROP TABLE IF EXISTS `tb_select_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_select_course` (
  `sc_s_id` int(11) NOT NULL,
  `sc_c_id` int(11) NOT NULL,
  `sc_date` date DEFAULT NULL,
  PRIMARY KEY (`sc_s_id`,`sc_c_id`),
  KEY `sc_c_id` (`sc_c_id`),
  CONSTRAINT `tb_select_course_ibfk_1` FOREIGN KEY (`sc_s_id`) REFERENCES `tb_student` (`s_id`),
  CONSTRAINT `tb_select_course_ibfk_2` FOREIGN KEY (`sc_c_id`) REFERENCES `tb_course` (`c_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_select_course`
--

LOCK TABLES `tb_select_course` WRITE;
/*!40000 ALTER TABLE `tb_select_course` DISABLE KEYS */;
INSERT INTO `tb_select_course` VALUES (1,1,'2017-03-01'),(1,2,'2017-03-01'),(2,1,'2017-03-02'),(2,2,'2017-03-02');
/*!40000 ALTER TABLE `tb_select_course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_student`
--

DROP TABLE IF EXISTS `tb_student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_student` (
  `s_id` int(11) NOT NULL AUTO_INCREMENT,
  `s_name` varchar(20) DEFAULT NULL,
  `s_sex` varchar(10) DEFAULT NULL,
  `s_age` int(11) DEFAULT NULL,
  `s_c_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`s_id`),
  KEY `s_c_id` (`s_c_id`),
  CONSTRAINT `tb_student_ibfk_1` FOREIGN KEY (`s_c_id`) REFERENCES `tb_class` (`c_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_student`
--

LOCK TABLES `tb_student` WRITE;
/*!40000 ALTER TABLE `tb_student` DISABLE KEYS */;
INSERT INTO `tb_student` VALUES (1,'Tom','male',18,1),(2,'Jack','male',19,1),(3,'Rose','female',18,1);
/*!40000 ALTER TABLE `tb_student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL,
  `sex` varchar(10) DEFAULT NULL,
  `address` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Tom','123456','male','chongqing'),(2,'Tom','123456','male','chengdu'),(4,'Jack','13579','male','beijing');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-10-02 14:16:27

create table user_annotation(
id int primary key auto_increment,
name varchar(20),
sex varchar(10),
age int);

insert into user_annotation(name,sex,age) values('Tom','male',20),('Jack','male',19),('Rose','female',18),('Lucy','female',19);


  --
-- Table structure for table `tb_course`
--

DROP TABLE IF EXISTS `tb_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_course` (
  `c_id` int(11) NOT NULL AUTO_INCREMENT,
  `c_name` varchar(20) DEFAULT NULL,
  `c_credit` int(11) DEFAULT NULL,
  PRIMARY KEY (`c_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_course`
--

LOCK TABLES `tb_course` WRITE;
/*!40000 ALTER TABLE `tb_course` DISABLE KEYS */;
INSERT INTO `tb_course` VALUES (1,'Math',5),(2,'Computer',4);
/*!40000 ALTER TABLE `tb_course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_select_course`
--

DROP TABLE IF EXISTS `tb_select_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_select_course` (
  `sc_s_id` int(11) NOT NULL,
  `sc_c_id` int(11) NOT NULL,
  `sc_date` date DEFAULT NULL,
  PRIMARY KEY (`sc_s_id`,`sc_c_id`),
  KEY `sc_c_id` (`sc_c_id`),
  CONSTRAINT `tb_select_course_ibfk_1` FOREIGN KEY (`sc_s_id`) REFERENCES `tb_student` (`s_id`),
  CONSTRAINT `tb_select_course_ibfk_2` FOREIGN KEY (`sc_c_id`) REFERENCES `tb_course` (`c_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_select_course`
--

LOCK TABLES `tb_select_course` WRITE;
/*!40000 ALTER TABLE `tb_select_course` DISABLE KEYS */;
INSERT INTO `tb_select_course` VALUES (1,1,'2017-03-01'),(1,2,'2017-03-01'),(2,1,'2017-03-02'),(2,2,'2017-03-02');
/*!40000 ALTER TABLE `tb_select_course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_student`
--

DROP TABLE IF EXISTS `tb_student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_student` (
  `s_id` int(11) NOT NULL AUTO_INCREMENT,
  `s_name` varchar(20) DEFAULT NULL,
  `s_sex` varchar(10) DEFAULT NULL,
  `s_age` int(11) DEFAULT NULL,
  PRIMARY KEY (`s_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_student`
--

LOCK TABLES `tb_student` WRITE;
/*!40000 ALTER TABLE `tb_student` DISABLE KEYS */;
INSERT INTO `tb_student` VALUES (1,'Tom','male',18),(2,'Tom','male',18);
/*!40000 ALTER TABLE `tb_student` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-10-02 21:14:51
