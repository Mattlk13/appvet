-- MySQL dump 10.13  Distrib 5.6.26, for Linux (x86_64)
--
-- Host: localhost    Database: appvet
-- ------------------------------------------------------
-- Server version	5.6.26

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
-- Table structure for table `alerts`
--

DROP TABLE IF EXISTS `alerts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alerts` (
  `username` varchar(254) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `alerttype` varchar(64) NOT NULL DEFAULT 'NORMAL',
  `message` varchar(1000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alerts`
--

LOCK TABLES `alerts` WRITE;
/*!40000 ALTER TABLE `alerts` DISABLE KEYS */;
/*!40000 ALTER TABLE `alerts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `androidtoolstatus`
--

DROP TABLE IF EXISTS `androidtoolstatus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `androidtoolstatus` (
  `appid` varchar(32) NOT NULL DEFAULT '',
  `registration` varchar(120) DEFAULT NULL,
  `appinfo` varchar(120) DEFAULT NULL,
  `audit` varchar(120) DEFAULT NULL,
  `androidsummary` varchar(120) DEFAULT 'NA',
  `androwarn` varchar(120) DEFAULT 'NA',
  `sigverifier` varchar(120) DEFAULT 'NA',
  `mkef` varchar(120) DEFAULT 'NA',
  `appollo` varchar(120) DEFAULT 'NA',
  `approval` varchar(120) DEFAULT 'NA',
  `golive` varchar(120) DEFAULT 'NA',
  `fireeye` varchar(120) DEFAULT 'NA',
  PRIMARY KEY (`appid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `androidtoolstatus`
--

LOCK TABLES `androidtoolstatus` WRITE;
/*!40000 ALTER TABLE `androidtoolstatus` DISABLE KEYS */;
/*!40000 ALTER TABLE `androidtoolstatus` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `apps`
--

DROP TABLE IF EXISTS `apps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `apps` (
  `appid` varchar(32) NOT NULL DEFAULT '',
  `lastupdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `appname` varchar(120) DEFAULT NULL,
  `packagename` varchar(120) DEFAULT NULL,
  `versioncode` varchar(120) DEFAULT NULL,
  `versionname` varchar(120) DEFAULT NULL,
  `filename` varchar(120) DEFAULT NULL,
   `submittime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `appstatus` varchar(120) DEFAULT NULL,
  `username` varchar(120) DEFAULT NULL,
  `clienthost` varchar(120) DEFAULT NULL,
  `os` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`appid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `apps`
--

LOCK TABLES `apps` WRITE;
/*!40000 ALTER TABLE `apps` DISABLE KEYS */;
/*!40000 ALTER TABLE `apps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iostoolstatus`
--

DROP TABLE IF EXISTS `iostoolstatus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iostoolstatus` (
  `appid` varchar(32) NOT NULL DEFAULT '',
  `registration` varchar(120) DEFAULT NULL,
  `appinfo` varchar(120) DEFAULT NULL,
  `audit` varchar(120) DEFAULT NULL,
  `androidsummary` varchar(120) DEFAULT NULL,
  `approval` varchar(120) DEFAULT NULL,
  `golive` varchar(120) DEFAULT NULL,
  `iossummary` varchar(120) DEFAULT NULL,
  PRIMARY KEY (`appid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iostoolstatus`
--

LOCK TABLES `iostoolstatus` WRITE;
/*!40000 ALTER TABLE `iostoolstatus` DISABLE KEYS */;
/*!40000 ALTER TABLE `iostoolstatus` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sessions`
--

DROP TABLE IF EXISTS `sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sessions` (
  `sessionid` varchar(32) NOT NULL DEFAULT '',
  `username` varchar(120) DEFAULT NULL,
  `clientaddress` varchar(120) DEFAULT NULL,
  `expiretime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`sessionid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sessions`
--

LOCK TABLES `sessions` WRITE;
/*!40000 ALTER TABLE `sessions` DISABLE KEYS */;
/*!40000 ALTER TABLE `sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `username` varchar(254) NOT NULL DEFAULT '',
  `password` varchar(128) DEFAULT NULL,
  `lastName` varchar(32) DEFAULT NULL,
  `firstName` varchar(32) DEFAULT NULL,
  `email` varchar(120) DEFAULT NULL,
  `role` varchar(500) DEFAULT 'NEW',
  `lastlogon` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fromhost` varchar(120) DEFAULT NULL,
  `toolsAuth` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
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

-- Dump completed on 2016-03-10  8:47:53
