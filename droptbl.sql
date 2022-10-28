-- Include your drop table DDL statements in this file.
-- Make sure to terminate each statement with a semicolon (;)

-- LEAVE this statement on. It is required to connect to your database.
CONNECT TO cs421;

-- Remember to put the drop table ddls for the tables with foreign key references
--    ONLY AFTER the parent tables has already been dropped (reverse of the creation order).

DROP TABLE Mother;
DROP TABLE Father;
DROP TABLE Couple;
DROP TABLE Institute;
DROP TABLE CommClinic;
DROP TABLE BirthCenter;
DROP TABLE Midwife;
DROP TABLE InfoSession;
DROP TABLE invited;
DROP TABLE Pregnancy;
DROP TABLE Baby;
DROP TABLE Technician;
DROP TABLE Test;
DROP TABLE Appointment;
DROP TABLE Observation;
DROP TABLE DueDate;
