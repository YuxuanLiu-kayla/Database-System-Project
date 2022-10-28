-- Include your create table DDL statements in this file.
-- Make sure to terminate each statement with a semicolon (;)

-- LEAVE this statement on. It is required to connect to your database.
CONNECT TO cs421;

-- Remember to put the create table ddls for the tables with foreign key references
--    ONLY AFTER the parent tables has already been created.

-- This is only an example of how you add create table ddls to this file.
--   You may remove it.
CREATE TABLE Mother
        (hcardid INTEGER NOT NULL,
        name VARCHAR(50) NOT NULL,
        email VARCHAR(100) NOT NULL,
        phone VARCHAR(20) NOT NULL,
        birthday DATETIME NOT NULL,
        address VARCHAR(50) NOT NULL,
        profession VARCHAR(50) NOT NULL,
        birthframe VARCHAR(20) NOT NULL,
        mbloodtype VARCHAR(10),
        PRIMARY KEY (hcardid)
);

CREATE TABLE Father
        (fatherid VARCHAR(10) NOT NULL,
        fname VARCHAR(50) NOT NULL,
        femail VARCHAR(100),
        fphone VARCHAR(20) NOT NULL,
        fhcardid INTEGER,
        fbirthday DATETIME NOT NULL,
        faddress VARCHAR(50),
        fprofession VARCHAR(50) NOT NULL,
        fbloodtype VARCHAR(10),
        PRIMARY KEY (fatherid)
);

CREATE TABLE Couple
        (coupleid VARCHAR(10) NOT NULL,
        isInterested VARCHAR(10) NOT NULL,
        hcardid INTEGER NOT NULL,
        fatherid VARCHAR(20),
        PRIMARY KEY (coupleid),
        FOREIGN KEY (hcardid) REFERENCES Mother,
        FOREIGN KEY (fatherid) REFERENCES Father
);

CREATE TABLE Institute
        (iemail VARCHAR(100) NOT NULL,
        iname VARCHAR(50) NOT NULL,
        iphone BIGINT NOT NULL,
        iaddress VARCHAR(50) NOT NULL,
        website VARCHAR(50),
        PRIMARY KEY (iemail)
);

CREATE TABLE CommClinic
        (iemail VARCHAR(100) NOT NULL,
        PRIMARY KEY (iemail),
        FOREIGN KEY (iemail) REFERENCES Institute
);

CREATE TABLE BirthCenter
        (iemail VARCHAR(100) NOT NULL,
        PRIMARY KEY (iemail),
        FOREIGN KEY (iemail) REFERENCES Institute
);

CREATE TABLE Midwife
        (pracid VARCHAR(10) NOT NULL,
        mname VARCHAR(50) NOT NULL,
        mphone VARCHAR(20)NOT NULL,
        workplace VARCHAR(50) NOT NULL,
        memail VARCHAR(100) NOT NULL,
        iemail VARCHAR(100) NOT NULL,
        PRIMARY KEY (pracid),
        FOREIGN KEY (iemail) REFERENCES Institute
);

CREATE TABLE InfoSession
        (sessionid VARCHAR(10) NOT NULL,
        sdate DATETIME NOT NULL,
        stime VARCHAR(20) NOT NULL,
        language VARCHAR(20) NOT NULL,
        pracid VARCHAR(10) NOT NULL,
        PRIMARY KEY (sessionid),
        FOREIGN KEY (pracid) REFERENCES Midwife
);

CREATE TABLE invited
        (coupleid VARCHAR(10) NOT NULL,
        sessionid VARCHAR(10) NOT NULL,
        attend VARCHAR(10) NOT NULL,
        PRIMARY KEY (coupleid),
        FOREIGN KEY (sessionid) REFERENCES InfoSession
);

CREATE TABLE Pregnancy
        (coupleid VARCHAR(10) NOT NULL,
	numpreg INTEGER NOT NULL,
        numbabies INTEGER,
        expdued DATETIME,
        estdued DATETIME,
        ishomebirth VARCHAR(10),
        ppracid VARCHAR(10) NOT NULL,
        bpracid VARCHAR(10),
        iemail VARCHAR(100),
        PRIMARY KEY (coupleid, numpreg),
        FOREIGN KEY (ppracid) REFERENCES Midwife,
        FOREIGN KEY (bpracid) REFERENCES Midwife,
        FOREIGN KEY (coupleid) REFERENCES Couple,
        FOREIGN KEY (iemail) REFERENCES BirthCenter
);

CREATE TABLE Baby
        (babyid VARCHAR(10) NOT NULL,
        bname VARCHAR(50),
        gender VARCHAR(10) NOT NULL,
        bbloodtype VARCHAR(10),
        birthday DATETIME,
        birthtime VARCHAR(10),
        coupleid VARCHAR(10) NOT NULL,
	numpreg INTEGER NOT NULL,
        PRIMARY KEY (babyid),
        FOREIGN KEY (coupleid, numpreg) REFERENCES Pregnancy
);

CREATE TABLE Technician
        (techid VARCHAR(10) NOT NULL,
        tname VARCHAR(50) NOT NULL,
        tphone VARCHAR(20) NOT NULL,
        PRIMARY KEY (techid)
);

CREATE TABLE Test
        (testid VARCHAR(10) NOT NULL,
        ttype VARCHAR(50) NOT NULL,
        presdate DATETIME NOT NULL,
        sampledate DATETIME NOT NULL,
	labdate DATETIME NOT NULL,
        result VARCHAR(100) NOT NULL,
        pracid VARCHAR(10) NOT NULL,
        coupleid VARCHAR(10),
	numpreg INTEGER,
        babyid VARCHAR(10),
        techid VARCHAR(10) NOT NULL,
        PRIMARY KEY (testid),
        FOREIGN KEY (pracid) REFERENCES Midwife,
        FOREIGN KEY (coupleid, numpreg) REFERENCES Pregnancy,
        FOREIGN KEY (babyid) REFERENCES Baby,
        FOREIGN KEY (techid) REFERENCES Technician
);

CREATE TABLE Appointment
        (aptmtid VARCHAR(10) NOT NULL,
        adate DATETIME NOT NULL,
        atime VARCHAR(10) NOT NULL,
        coupleid VARCHAR(10) NOT NULL,
	numpreg INTEGER NOT NULL,
        pracid VARCHAR(10) NOT NULL,
        PRIMARY KEY (aptmtid),
        FOREIGN KEY (coupleid, numpreg) REFERENCES Pregnancy,
        FOREIGN KEY (pracid) REFERENCES Midwife
);

CREATE TABLE Observation
        (noteid VARCHAR(10) NOT NULL,
        odate DATETIME NOT NULL,
        otime VARCHAR(10) NOT NULL,
        content VARCHAR(100) NOT NULL,
        aptmtid VARCHAR(10) NOT NULL,
        PRIMARY KEY (noteid),
        FOREIGN KEY (aptmtid) REFERENCES Appointment
);

CREATE TABLE DueDate
        (duedateid VARCHAR(10) NOT NULL,
        usounddate DATETIME NOT NULL,
        lmpdate DATETIME NOT NULL,
	finaldate DATETIME,
        aptmtid VARCHAR(10),
        PRIMARY KEY (duedateid),
        FOREIGN KEY (aptmtid) REFERENCES Appointment
);


