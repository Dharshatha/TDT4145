CREATE DATABASE FakeIMDB; 
USE FakeIMDB; #Endre


CREATE TABLE Produksjon(
	ProdID int not null,
	FilmSerie VARCHAR(15),
    Episodenr Integer,
    Sesongnr Integer,
    Lengde TIME, 
    ProdTittel VARCHAR(200),
    ProdType VARCHAR(15),
    UtAar YEAR, 
    LanDato DATE, 
    ProdBeskrivelse VARCHAR(100), 
    Video VARCHAR(5), 
    CONSTRAINT Prod_PK PRIMARY KEY (ProdID),
    CONSTRAINT PRODUKSJON_CHECK_VIDEO CHECK (Video = "True" OR Video = "False"),
    CONSTRAINT PRODUKSJON_CHECK_PRODTYPE CHECK (ProdType = "TV" OR ProdType ="Streaming" OR ProdType ="Kino"),
    CONSTRAINT PRODUKSJON_CHECK_FILMSERIE CHECK (FilmSerie = "Serie" OR FilmSerie = "Film")
);



CREATE TABLE Kategori(
	KategoriID INTEGER NOT NULL, 
	KatType VARCHAR(15),
    CONSTRAINT KATEGORI_PK PRIMARY KEY (KategoriID) 
);
CREATE TABLE Person(
	PID INTEGER NOT NULL, 
	PNavn VARCHAR(20), 
	FAar YEAR, 
	Nasjonlitet VARCHAR(15),
    CONSTRAINT PERSON_PK PRIMARY KEY (PID)
);

CREATE TABLE Musikk(
	MusikkID INTEGER NOT NULL, 
	KompNavn VARCHAR(25), 
	FremfoererNavn VARCHAR(25),
    CONSTRAINT MUSIKK_PK PRIMARY KEY(MusikkID)
);

CREATE TABLE Selskap(
	SelskapID INTEGER NOT NULL, 
	URL VARCHAR(100), 
	Adresse VARCHAR(200),
	Land VARCHAR(46),
    CONSTRAINT SELSKAP_PK PRIMARY KEY(SelskapID)
);

CREATE TABLE Bruker(
	BrukerID INTEGER NOT NULL,
    CONSTRAINT BRUKER_PK PRIMARY KEY(BrukerID)
);

#END OF ENTITESKLASSER
#RELASJONSKLASSER STARTER UNDER 

CREATE TABLE Kategorisert(
	KategoriID INTEGER NOT NULL, 
	ProdID INTEGER NOT NULL,
    CONSTRAINT K_PK PRIMARY KEY(KategoriID,ProdID),
    CONSTRAINT K_FK1 FOREIGN KEY (KategoriID) REFERENCES Kategori(KategoriID)
	ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT K_FK2 FOREIGN KEY (ProdID) REFERENCES Produksjon(ProdID)
	ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Eierskap(
	SelskapID INTEGER NOT NULL, 
    ProdID INTEGER NOT NULL,
    
    CONSTRAINT EIERSKAP_PK PRIMARY KEY (SelskapID,ProdID),
    CONSTRAINT E_FK1 FOREIGN KEY (SelskapID) REFERENCES Selskap(SelskapID)
	ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT E_FK2 FOREIGN KEY (ProdID) REFERENCES Produksjon(ProdID)
	ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE SkuespillerSpillerIProduksjon(
	PID INTEGER NOT NULL, 
    ProdID INTEGER NOT NULL, 
    Rolle VARCHAR(20),
    
    CONSTRAINT SSIP_PK PRIMARY KEY(PID, PRODID),
    CONSTRAINT SSIP_FK1 FOREIGN KEY (PID) REFERENCES Person(PID)
	ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT SSPI_FK2 FOREIGN KEY (ProdID) REFERENCES Produksjon(ProdID)
	ON DELETE CASCADE ON UPDATE CASCADE
);



CREATE TABLE HarSkrevetProduksjon(
	PID INTEGER NOT NULL,
	ProdID INTEGER NOT NULL,
    
    CONSTRAINT HSP_PK PRIMARY KEY (PID, ProdID),
    CONSTRAINT HSP_FK1 FOREIGN KEY (PID) REFERENCES Person(PID)
	ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT HSP_FK2 FOREIGN KEY (ProdID) REFERENCES Produksjon(ProdID)
	ON DELETE CASCADE ON UPDATE CASCADE
);



CREATE TABLE RegissørLagetProduksjon(
	PID INTEGER NOT NULL, 
	ProdID INTEGER NOT NULL, 
    
    CONSTRAINT RLP_PK PRIMARY KEY(PID, ProdID),
    CONSTRAINT RLP_FK1 FOREIGN KEY (PID) REFERENCES Person(PID)
	ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT RLP_FK2 FOREIGN KEY (ProdID) REFERENCES Produksjon(ProdID)
	ON DELETE CASCADE ON UPDATE CASCADE
);



CREATE TABLE AnmeldelseAvProduksjon(
	ProdID INTEGER NOT NULL, 
	BrukerID INTEGER NOT NULL, 
    Rating INTEGER,
    Kommentar VARCHAR(300),
	
    CONSTRAINT AAP_CHECK_RATING CHECK (Rating BETWEEN 1 and 10),
    CONSTRAINT AAP_PK PRIMARY KEY (ProdID, BrukerID),
    CONSTRAINT AAP_FK1 FOREIGN KEY (ProdID) REFERENCES Produksjon(ProdID)
	ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT AAP_FK2 FOREIGN KEY (BrukerID) REFERENCES Bruker(BrukerID)
	ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE HarMusikkTilProduksjon(
	MusikkID INTEGER NOT NULL, 
	ProdID INTEGER NOT NULL, 
    
    CONSTRAINT HMTP_PK PRIMARY KEY (MusikkID, ProdID),
    CONSTRAINT HMTP_FK1 FOREIGN KEY (MusikkID) REFERENCES Musikk(MusikkID)
	ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT HMTP_FK2 FOREIGN KEY (ProdID) REFERENCES Produksjon(ProdID)
	ON DELETE CASCADE ON UPDATE CASCADE
);


#END OF Relasjonsklasser