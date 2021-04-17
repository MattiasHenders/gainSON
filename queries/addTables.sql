DROP TABLE Exercises;
DROP TABLE Locations;
DROP TABLE Tracking;
DROP TABLE Media;
DROP TABLE FrenchData;
DROP TABLE SpanishData;
DROP TABLE ForeignData;
DROP TABLE MuscleGroups;
/*=============================================*/
CREATE TABLE Locations(
	locationsID int NOT NULL,
	boolAtGym bit NOT NULL,
	boolAtHome bit NOT NULL,
	boolOutside bit NOT NULL,
PRIMARY PK_Locations KEY locationsID;

CREATE TABLE Tracking(
	trackingID int NOT NULL,
	boolRepsSets bit NULL,
	boolBodyWeight bit NULL,
	boolWeights bit NULL,
	boolTimer bit NULL,
	boolStopwatch bit NULL,
	boolDistance bit NULL,
 CONSTRAINT PK_Tracking PRIMARY KEY trackingID;

CREATE TABLE Media(
	mediaID int NOT NULL,
	youtube varchar(max) NULL,
 CONSTRAINT PK_Media PRIMARY KEY mediaID;

CREATE TABLE ForeignData(
	foreignDataID int NOT NULL,
	frenchID varchar(50) NULL,
	spanishID varchar(50) NULL,
 CONSTRAINT PK_ForeignData PRIMARY KEY foreignDataID;

CREATE TABLE SpanishData(
	exerciseID int NOT NULL,
	name varchar(50) NOT NULL,
	description varchar(max) NOT NULL,
	language varchar(50) NULL,
 CONSTRAINT PK_SpanishData PRIMARY KEY exerciseID;

CREATE TABLE FrenchData(
	exerciseID int NOT NULL,
	name varchar(50) NULL,
	description varchar(max) NULL,
	language varchar(50) NULL,
 CONSTRAINT PK_FrenchData PRIMARY KEY exerciseID;

CREATE TABLE MuscleGroups(
	muscleGroupsID int NOT NULL,
	triceps bit NOT NULL,
	pectorals bit NOT NULL,
	deltoids bit NOT NULL,
	quadriceps bit NULL,
	hamstrings bit NOT NULL,
	lats bit NOT NULL,
	traps bit NOT NULL,
	biceps bit NOT NULL,
 CONSTRAINT PK_MuscleGroups PRIMARY KEY muscleGroupsID;

CREATE TABLE Exercises(
	exerciseID int NOT NULL,
	name varchar(50) NULL,
	description varchar(max) NULL,
	difficulty float NULL,
 CONSTRAINT PK_Exercises PRIMARY KEY exerciseID;