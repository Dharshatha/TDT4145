package imdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class IMDBProject_v2 {
	private String username = "root";
	private String password = "";
	private String url = "jdbc:mysql://localhost:____/fakeimdb?autoReconnect=true&useSSL=false&serverTimezone=UTC";
    protected Connection conn;
    
    public IMDBProject_v2() {
    }
    
    private void checkDriver() {
    	try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public Connection connect() {    	
        try {
            Properties p = new Properties();
            p.put("user", username);
            p.put("password", password);
            conn = DriverManager.getConnection(url, p);            
        } catch (Exception e)
        {
            throw new RuntimeException("Unable to connect", e);
        }
        return conn;
    }
    
 
    
    // __USE_CASES__
    // Use case 1: Finne navnet på alle rollene en gitt skuespiller har.
    private void findRoles(Scanner scanner, Connection conn) {
    	System.out.println("Finne navnet på alle rollene til en skuespiller.\n----------------");
    	try {
    		System.out.println("Oppgi PID på personen du ønsker å finne rollene til: ");
    		int PID = scanner.nextInt();
    		Statement stmt = conn.createStatement();
    		String query = "SELECT Person.PID, PNavn, SkuespillerSpillerIProduksjon.Rolle\r\n" + 
    				"FROM Person Natural join skuespillerspilleriproduksjon \r\n" + 
    				"WHERE SkuespillerSpillerIProduksjon.PID = " + PID ;
    		ResultSet rs = stmt.executeQuery(query);
    		List<String> roller = new ArrayList<>();
    		String navn = "";
    	
    		while (rs.next()) {
    			roller.add(rs.getString("Rolle"));
    			navn = rs.getString("PNavn");
    		}
    		
    		if (roller.size() == 0) {
    			System.out.println("PID: " + PID + " har ingen roller.");
    		}
    		
    		else {
				System.out.println("PID: " + PID + ", navn: " + navn + " har roller: ");
				for (int i = 0; i < roller.size(); i++) {
					System.out.println(roller.get(i) + " ");
				} 
			}
    		
    	}
    	catch (Exception e) {
    		System.out.println("Error med database:\n"+e);
    	}
    }
    
    // Use case 2: Finne hvilke filmer som en gitt skuespiller opptrer i.
    private void findMovies(Scanner scanner, Connection conn) {
    	System.out.println("Finn hvilke filmer skuespilleren opptrer i.\n----------------");
    	try {
    		System.out.println("Oppgi SkuespillerID for å finne filmene h*n spiller i: ");
    		int SkuespillerID = scanner.nextInt();
    		Statement stmt = conn.createStatement();
    		String query = "SELECT ProdTittel, PNavn\r\n" + 
    				"From Person Natural Join SkuespillerSpillerIProduksjon "
    				+ "Join Produksjon on Produksjon.ProdID = SkuespillerSpillerIProduksjon.ProdID\r\n" + 
    				"WHERE Person.PID = " + SkuespillerID + " AND Produksjon.ProdID = SkuespillerSpillerIProduksjon.ProdID";
    		ResultSet rs = stmt.executeQuery(query);
    		List<String> filmer = new ArrayList<>();
    		String navn = "";
    		
    		while (rs.next()) {
        		filmer.add(rs.getString("ProdTittel"));
        		navn = rs.getString("PNavn");
    		}
    		
    		if (filmer.size() == 0) {
    			System.out.println("SkuespillerID: " + SkuespillerID + " har ikke spilt i noen filmer.");
    		}
    		
    		else {
				System.out.println(navn + " spiller i: ");
				for (int i = 0; i < filmer.size(); i++) {
					System.out.println(filmer.get(i) + " ");
				} 
			}
    		
    	}
    	catch (Exception e) {
    		System.out.println("Error med database:\n"+e);
    	}
    }
    
    // Use case 3: Finne hvilke filmselskap som lager flest filmer inne hver sjanger (grøssere, familie, o.l.).
    private void corpWithMostInGenre(Scanner scanner, Connection conn) {
    	System.out.println("Finne hvilke filmselskap som lager flest filmer innen sjanger.\n----------------");
    	try {
    		System.out.println("Velg sjanger: ");
    		scanner.nextLine();
    		String kategori = scanner.nextLine();
    		Statement stmt = conn.createStatement();
    		String query = "Select kategori.KatType, selskap.URL, Count(*) as Antall\r\n" + 
    				"From selskap natural join (eierskap natural join (produksjon natural join (kategorisert natural join kategori)))\r\n" + 
    				"Where kategori.KatType = '"+kategori+"' Group by KategoriID, SelskapID Order by Antall DESC";
    		ResultSet rs = stmt.executeQuery(query);
    		
    		if (!rs.next()) {
    			System.out.println("Fant ingen filmer innen '" + kategori + "'");
    		}
    		
    		else {
    			rs.previous();
				while (rs.next()) {
					System.out.println("Selskap: " + rs.getString("URL") + " antall: " + rs.getString("Antall"));
				} 
			}
    		
    	}
    	catch (Exception e) {
    		System.out.println("Error med database:\n"+e);
    	}
    }
    
    // Legger til film i databasen
    private void insertMovie(Scanner scanner, Connection conn, int ProdID) {
    	System.out.println("Lengde (HH:MM:SS): ");
    	scanner.nextLine();
    	String Lengde = scanner.nextLine();
    	System.out.println("Produksjonstittel: ");
    	String ProdTittel = scanner.nextLine();
    	System.out.println("Produksjonstype (TV, Streaming eller Kino): ");
    	String ProdType = scanner.nextLine();
    	System.out.println("Utgivelsesår(yyyy): ");
    	int UtAar = scanner.nextInt();
    	System.out.println("Lanseringsdato (yyyy-mm-dd):");
    	scanner.nextLine();
    	String LanDato = scanner.nextLine();
    	System.out.println("Produksjonsbeskrivelse: ");
    	String ProdBeskrivelse = scanner.nextLine();
    	System.out.println("Video (True/False): ");
    	String Video = scanner.nextLine();
    	String FilmSerie = "Film";
    	String Prodquery = "INSERT INTO produksjon\r\n" + 
    			"VALUES ("+ProdID+", '"+ FilmSerie + "', "+ "Null" + ", "+ "Null" + ", '"+ Lengde+"', '"+ProdTittel+"', '"+ProdType+"', "+UtAar+", '"
    					+ LanDato+"', '"+ProdBeskrivelse+"', '"+Video+"');"; 	
    	
    	try {
    		PreparedStatement stmt = conn.prepareStatement(Prodquery);
    		stmt.execute();
    		System.out.println("Filmen " + ProdTittel + " ble lagt til i databasen. Trykk enter for å legge inn forfatter.");
    		scanner.nextLine();
    		this.insertforfatter(scanner, conn, ProdID);
    	}
    	catch (Exception e){
    		System.out.println("Error med database:\n"+e);
    	}
    	
    }
    

 // Sjekker om filmen eksisterer i SQL databasen fra før.
    private boolean checkIfMovieDuplicate(int ProdID) throws SQLException {
    	String query = "SELECT produksjon.prodID\r\n" + 
    			"FROM Produksjon\r\n" + 
    			"Where produksjon.prodID = " + ProdID + ";";
    	PreparedStatement stmt = conn.prepareStatement(query);
    	stmt.execute();
    	ResultSet rs = stmt.executeQuery(query);
    	if (!rs.next()) {
			return true;
		}
    	else {
    		return false; 	
    		}
    } 
    
    
    //Sjekker om oppgitt PersonID allerede eksisterer i databasen.
    private boolean PersonDoesntExist(Scanner scanner, Connection conn, int PersonID) throws SQLException {
    	String query = "SELECT Person.PID\r\n" + 
    			"FROM Person\r\n" + 
    			"Where Person.PID =" + PersonID + ";";
    	PreparedStatement stmt = conn.prepareStatement(query);
    	stmt.execute();
    	ResultSet rs = stmt.executeQuery(query);
    	if (!rs.next()) {
			return true;
		}
    	else {
    		return false; 	
    		}    	
    }
    
    // Legger til forfatter i databasen
    private void insertforfatter(Scanner scanner, Connection conn, int ProdID) {
    	System.out.println("Antall forfattere i Produksjonen: ");
    	int forfatterantall = scanner.nextInt();
    	try {   	for(int i = 1; i<= forfatterantall; i++) {
    		System.out.println("PersonID for forfatter: ");
    		int PersonID = scanner.nextInt();
    		try {
    			// Sjekker om personen allerede eksisterer i databasen
				if ((this.PersonDoesntExist(scanner, conn, PersonID))){
					this.insertforfatterhelpfunction(scanner, conn, ProdID, PersonID, i);
				}
				// Dersom personen allerede eksisterer i databasen, kan man velge å knytte personen i databasen som regissør
				//Eller så kan man bruke ny PersonID
				else {
					System.out.println("Denne personen finnes allerede i databasen");
					scanner.nextLine();
					System.out.println("Vil du at databasen skal bruke eksisternde person som forfatter for angitt PersonID? (Ja/Nei)");
					String answer = scanner.nextLine();
					if (answer.equalsIgnoreCase("nei")) {
						System.out.println("Skriv inn ny PersonID for forfatter: ");
			    		int PersonIDny = scanner.nextInt();	
						while(!(this.PersonDoesntExist(scanner, conn, PersonIDny))){
							System.out.println("Du må skirve ny PersonID, siden det du oppgitte allerede eksisterer i databasen.");
							System.out.println("PersonID for forfatter: ");
				    		int PersonIDny1 = scanner.nextInt();
				    		PersonIDny = PersonIDny1;
				 
						}			
						this.insertforfatterhelpfunction(scanner, conn, ProdID, PersonIDny, i);
					}
					else if(answer.equalsIgnoreCase("ja")){
						String Regquery1 = "insert into HarSkrevetProduksjon values (" + PersonID +","+  ProdID +");";
						try {
							PreparedStatement stmt2 = conn.prepareStatement(Regquery1);
							stmt2.execute();
							System.out.println("Personen med personID: "+ PersonID + " har blitt satt som forfatter til denne filmen.");
						}
						catch (Exception e){
							System.out.println("Error med database:\n"+e);
						}	
					}
					else {
						throw new IllegalArgumentException("Du har skrevet noe ugyldig inn i feltet.");
					}
				}
			} catch (Exception e){
	    		System.out.println("Error med database:\n"+e);
	    		}
    	}
    	this.insertSkuespiller(scanner, conn, ProdID);
    		
    	}
    	catch(Exception e) {
    		System.out.println("Error med database:\n"+e);
    		}
    }
    
    // Ekstrafunksjon som hjelper til med å legge til forfatter i databasen. 
    private void insertforfatterhelpfunction(Scanner scanner, Connection conn, int ProdID, int PersonID, int i) {
				System.out.println("Navn på forfatter: ");
				scanner.nextLine();
				String forfatternavn = scanner.nextLine();
				System.out.println("Fødselsår: ");
				int Fodselsaar = scanner.nextInt();
				System.out.println("Nasjonalitet: ");
				scanner.nextLine();
				String nasjonalitet = scanner.nextLine(); 
				String Regquery = "insert into HarSkrevetProduksjon values (" + PersonID +","+  ProdID +");";
				String perquery = "insert into Person values (' "  +PersonID + "', '" +forfatternavn+"' ," + Fodselsaar+", '" + nasjonalitet +"');";
				try {
					PreparedStatement stmt1 = conn.prepareStatement(perquery);
					stmt1.execute();
					PreparedStatement stmt = conn.prepareStatement(Regquery);
					stmt.execute();
					System.out.println("Regissør "+ i + ":  " + forfatternavn + " ble lagt til i databasen.");
				}
				catch (Exception e){
					System.out.println("Error med database:\n"+e);
				}	
    }
  
   
 // Setter inn skuespiller i SQL databasen
    private void insertSkuespiller(Scanner scanner, Connection conn, int ProdID) {
    	System.out.println("Antall skuespillere i filmen du ønsker å legge inn: ");
    	int skuespillerantall = scanner.nextInt();
    	for(int i = 1; i<= skuespillerantall; i++) {
    		System.out.println("PersonID for Skuespiller: ");
    		int PersonID = scanner.nextInt();
    		try {
    			if((this.PersonDoesntExist(scanner, conn, PersonID))){
    				this.insertSkuespillerhelpfunciton(scanner, conn, ProdID, PersonID, i);
    	 		}
    			else {
					System.out.println("Denne personen finnes allerede i databasen");
					scanner.nextLine();
					System.out.println("Vil du at databasen skal bruke eksisternde person som skuespiller for angitt PersonID? (Ja/Nei)");
					String answer = scanner.nextLine();
					if (answer.equalsIgnoreCase("nei")) {
						System.out.println("PersonID for skuespiller: ");
			    		int PersonIDny = scanner.nextInt();	
						while(!(this.PersonDoesntExist(scanner, conn, PersonIDny))){
							System.out.println("PersonID for skuespiller: ");
				    		int PersonIDny1 = scanner.nextInt();
				    		PersonIDny = PersonIDny1;
				    		System.out.println("Du må skirve ny PersonID, siden det du oppgitte allerede eksisterer i databasen.");
						}	
						this.insertSkuespillerhelpfunciton(scanner, conn, ProdID, PersonIDny, i);
					}
					else if(answer.equalsIgnoreCase("ja")){
						System.out.println("Hva slags rolle har skuespilleren i filmen: ");
						String Rolle = scanner.nextLine();
						String Regquery1 = "insert into skuespillerspilleriproduksjon values (" + PersonID +","+  ProdID + "," + "'"+Rolle +"');";
						try {
							PreparedStatement stmt2 = conn.prepareStatement(Regquery1);
							stmt2.execute();
							System.out.println("Personen med personID: "+ PersonID + "har blitt satt som skuespiller til denne filmen.");
						}
						catch (Exception e){
							System.out.println("Error med database:\n"+e);
						}	
						
					}
					
					else {
						throw new IllegalArgumentException("Du har skrevet noe ugyldig inn i feltet.");
					}	
				}    			
    		}
    		catch(Exception e) {
    			System.out.println("Error med database:\n"+e);
    		}
    	}  	this.insertRegissor(scanner, conn, ProdID);
    }
    
    // Hjelpefunksjon som legger til Skuespiller    
    private void insertSkuespillerhelpfunciton(Scanner scanner, Connection conn, int ProdID, int PersonID, int i) {
   		System.out.println("Hva slags rolle spiller skuespilleren i filmen: ");
		scanner.nextLine();
   		String Rolle = scanner.nextLine();
		System.out.println("Navn på skuespiller: ");
		String Skuespillernavn = scanner.nextLine();
		System.out.println("Fødselsår: ");
		int Fodselsaar = scanner.nextInt();
		System.out.println("Nasjonalitet: ");
		scanner.nextLine();
		String nasjonalitet = scanner.nextLine();
		String Regquery = "insert into skuespillerspilleriproduksjon values (" + PersonID +","+  ProdID + "," + "'"+Rolle +"');";
		String perquery = "insert into Person values (' "  +PersonID + "', '" +Skuespillernavn+"' ," + Fodselsaar+", '" + nasjonalitet +"');";
		try {
			PreparedStatement stmt1 = conn.prepareStatement(perquery);
    		stmt1.execute();
    		PreparedStatement stmt = conn.prepareStatement(Regquery);
    		stmt.execute();
    		scanner.nextLine();
    		System.out.println("Skuespiller "+ i + ": " + Skuespillernavn + " ble lagt til i databasen.");
    	}
    	catch (Exception e){
    		System.out.println("Error med database:\n"+e);
    	}
    	
    }
    
    
    // Legger til regissør i databasen
    private void insertRegissor(Scanner scanner, Connection conn, int ProdID) {
    	System.out.println("Antall regissører i Produksjonen: ");
    	int regissørantall = scanner.nextInt();
    	for(int i = 1; i<= regissørantall; i++) {
    		System.out.println("PersonID for regissør: ");
    		int PersonID = scanner.nextInt();
    		try {
    			// Sjekker om personen allerede eksisterer i databasen
				if ((this.PersonDoesntExist(scanner, conn, PersonID))){
					this.insertRegissorhelpfunction(scanner, conn, ProdID, PersonID, i);
				}
				// Dersom personen allerede eksisterer i databasen, kan man velge å knytte personen i databasen som regissør
				//Eller så kan man bruke ny PersonID
				else {
					System.out.println("Denne personen finnes allerede i databasen");
					scanner.nextLine();
					System.out.println("Vil du at databasen skal bruke eksisternde person som regissør for angitt PersonID? (Ja/Nei)");
					String answer = scanner.nextLine();
					if (answer.equalsIgnoreCase("nei")) {
						System.out.println("PersonID for regissør: ");
			    		int PersonIDny = scanner.nextInt();	
						while(!(this.PersonDoesntExist(scanner, conn, PersonIDny))){
							System.out.println("PersonID for regissør: ");
				    		int PersonIDny1 = scanner.nextInt();
				    		PersonIDny = PersonIDny1;
				    		System.out.println("Du må skirve ny PersonID, siden det du oppgitte allerede eksisterer i databasen.");
						}			
						this.insertRegissorhelpfunction(scanner, conn, ProdID, PersonIDny, i);
					}
					else if(answer.equalsIgnoreCase("ja")){
						String Regquery1 = "insert into regissørlagetproduksjon values (" + PersonID +","+  ProdID +");";
						try {
							PreparedStatement stmt2 = conn.prepareStatement(Regquery1);
							stmt2.execute();
							System.out.println("Personen med personID: "+ PersonID + "har blitt satt som regissør til denne filmen.");
						}
						catch (Exception e){
							System.out.println("Error med database:\n"+e);
						}	
					}
					else {
						throw new IllegalArgumentException("Du har skrevet noe ugyldig inn i feltet.");
					}
				}
			} catch (Exception e){
	    		System.out.println("Error med database:\n"+e);
	    		}
    	}
    	this.insertSelskap(scanner, conn, ProdID);
    }
    
    
    // Legger til Regissør i databasen - hjelpefunksjon
    private void insertRegissorhelpfunction(Scanner scanner, Connection conn, int ProdID, int PersonID, int i) {
				System.out.println("Navn på regissør: ");
				scanner.nextLine();
				String Regissørnavn = scanner.nextLine();
				System.out.println("Fødselsår: ");
				int Fodselsaar = scanner.nextInt();
				System.out.println("Nasjonalitet: ");
				scanner.nextLine();
				String nasjonalitet = scanner.nextLine(); 
				String Regquery = "insert into regissørlagetproduksjon values (" + PersonID +","+  ProdID +");";
				String perquery = "insert into Person values (' "  +PersonID + "', '" +Regissørnavn+"' ," + Fodselsaar+", '" + nasjonalitet +"');";
				try {
					PreparedStatement stmt1 = conn.prepareStatement(perquery);
					stmt1.execute();
					PreparedStatement stmt = conn.prepareStatement(Regquery);
					stmt.execute();
					System.out.println("Regissør "+ i + ":  " + Regissørnavn + " ble lagt til i databasen.");
				}
				catch (Exception e){
					System.out.println("Error med database:\n"+e);
				}	
    }
    	
    //Sjekker om selskapet eksisterer fra før
    private boolean SelskapDoesntExist(Scanner scanner, Connection conn, int SelskapID) throws SQLException{
    	String query = "SELECT Selskap.SelskapID\r\n" + 
    			"FROM Selskap\r\n" + 
    			"Where Selskap.SelskapID =" + SelskapID + ";";
    	PreparedStatement stmt = conn.prepareStatement(query);
    	stmt.execute();
    	ResultSet rs = stmt.executeQuery(query);
    	if (!rs.next()) {
			return true;
		}
    	else {
    		return false; 	
    		} 
    }
    
    // Legger til selskap i databasen - hjelpefunksjon
    private void insertSelskaphelpfunction(Scanner scanner, Connection conn, int ProdID, int SelskapID, int i) {
    	System.out.println("Angi URL til selskapet: ");
		scanner.nextLine();
		String URL = scanner.nextLine();
		System.out.println("Adresse: ");
		String Adresse = scanner.nextLine();
		System.out.println("Land: ");
		String Land = scanner.nextLine(); 
		String Regquery = "insert into eierskap values (" + SelskapID +","+  ProdID +");";
		String perquery = "insert into Selskap values (' "  +SelskapID + "', '" +URL+"' , '" + Adresse+"', '" + Land +"');";
		try {
			PreparedStatement stmt1 = conn.prepareStatement(perquery);
			stmt1.execute();
			PreparedStatement stmt = conn.prepareStatement(Regquery);
			stmt.execute();
			System.out.println("Selskap "+ i + ":  " + URL + " ble lagt til i databasen.");
		}
		catch (Exception e){
			System.out.println("Error med database:\n"+e);
		}	
    	
    }
    
    // Legger til selskap i databasen
    private void insertSelskap(Scanner scanner, Connection conn, int ProdID) {
    	System.out.println("Antall Selskap i Produksjonen: ");
    	int selskapantall = scanner.nextInt();
    	for(int i = 1; i<= selskapantall; i++) {
    		System.out.println("SelskapID for Selskap: ");
    		int SelskapID = scanner.nextInt();
    		try {
    			// Sjekker om personen allerede eksisterer i databasen
				if ((this.SelskapDoesntExist(scanner, conn, SelskapID))){
					this.insertSelskaphelpfunction(scanner, conn, ProdID, SelskapID, i);
				}
				// Dersom Selskap allerede eksisterer i databasen, kan man velge å knytte Selskapet i databasen med filmen
				//Eller så kan man bruke ny SelskapID
				else {
					System.out.println("Dette selskapet finnes allerede i databasen");
					scanner.nextLine();
					System.out.println("Vil du at databasen skal bruke eksisternde Selskap som selkap for denne filmen? (Ja/Nei)");
					String answer = scanner.nextLine();
					if (answer.equalsIgnoreCase("nei")) {
						System.out.println("Ny selskapsID: ");
			    		int SelskapIDny = scanner.nextInt();	
						while(!(this.SelskapDoesntExist(scanner, conn, SelskapIDny))){
							System.out.println("Skriv ny selskapID for å opprette nytt selskap: ");
				    		int SelskapIDny1 = scanner.nextInt();
				    		SelskapIDny = SelskapIDny1;
				    		System.out.println("Du må skirve ny SelskapID, siden det du oppgitte allerede eksisterer i databasen.");
						}
						this.insertSelskaphelpfunction(scanner, conn, ProdID, SelskapIDny, i);
					}
					else if(answer.equalsIgnoreCase("ja")){
						String Regquery1 = "insert into eierskap values (" + SelskapID +","+  ProdID +");";
						try {
							PreparedStatement stmt2 = conn.prepareStatement(Regquery1);
							stmt2.execute();
							System.out.println("Selskap med SelskapsID: "+ SelskapID + "har fått eierskap til denne filmen.");
						}
						catch (Exception e){
							System.out.println("Error med database:\n"+e);
						}	
					}
					else {
						throw new IllegalArgumentException("Du har skrevet noe ugyldig inn i feltet.");
					}
				}
			} catch (Exception e){
	    		System.out.println("Error med database:\n"+e);
	    		}
    	}
    	this.insertKategori(scanner, conn, ProdID);
    }
    
    //Sjekker om sjangeren allerede eksisterer i databasen
    private boolean KategoriDoesntExist(Scanner scanner, Connection conn, int KategoriID) throws SQLException{
    	String query = "SELECT Kategori.KategoriID\r\n" + 
    			"FROM Kategori\r\n" + 
    			"Where Kategori.KategoriID =" + KategoriID + ";";
    	PreparedStatement stmt = conn.prepareStatement(query);
    	stmt.execute();
    	ResultSet rs = stmt.executeQuery(query);
    	if (!rs.next()) {
			return true;
		}
    	else {
    		return false; 	
    		} 
    }
    
    // Legger til sjanger i databasen - hjelpefunksjon
    private void insertKategorihelpfunction(Scanner scanner, Connection conn, int ProdID, int KategoriID, int i) {
    	System.out.println("Angi navnet på sjangertypen: ");
		scanner.nextLine();
		String Sjangertype = scanner.nextLine();
		String Regquery = "insert into kategorisert values (" + KategoriID +","+  ProdID +");";
		String perquery = "insert into kategori values (' "  +KategoriID + "', '" +Sjangertype+"');";
		try {
			PreparedStatement stmt1 = conn.prepareStatement(perquery);
			stmt1.execute();
			PreparedStatement stmt = conn.prepareStatement(Regquery);
			stmt.execute();
			System.out.println("Sjanger "+ i + ":  " + Sjangertype + " ble lagt til i databasen.");
		}
		catch (Exception e){
			System.out.println("Error med database:\n"+e);
		}	
    	
    }
    
    //Legger til sjanger i databasen
    private void insertKategori(Scanner scanner, Connection conn, int ProdID) {
    	System.out.println("Antall sjangere du ønsker å sette til filmen: ");
    	int sjangertall = scanner.nextInt();
    	for(int i = 1; i<= sjangertall; i++) {
    		System.out.println("SjangerID for Sjanger: ");
    		int KategoriID = scanner.nextInt();
    		try {
    			// Sjekker om kategori allerede eksisterer i databasen
				if ((this.KategoriDoesntExist(scanner, conn, KategoriID))){
					this.insertKategorihelpfunction(scanner, conn, ProdID, KategoriID, i);
				}
				// Dersom Selskap allerede eksisterer i databasen, kan man velge å knytte Selskapet i databasen med filmen
				//Eller så kan man bruke ny SelskapID
				else {
					System.out.println("Denne sjangeren finnes allerede i databasen");
					scanner.nextLine();
					System.out.println("Vil du at databasen skal bruke eksisternde sjanger som sjanger for denne filmen? (Ja/Nei)");
					String answer = scanner.nextLine();
					if (answer.equalsIgnoreCase("nei")) {
						System.out.println("Ny sjangerID: ");
			    		int sjangerIDny = scanner.nextInt();	
						while(!(this.KategoriDoesntExist(scanner, conn, sjangerIDny))){
							System.out.println("Du må skirve ny sjangerID, siden det du oppgitte allerede eksisterer i databasen.");
							System.out.println("Skriv ny sjangerID for å opprette ny sjanger: ");
				    		int sjangerIDny1 = scanner.nextInt();
				    		sjangerIDny = sjangerIDny1;
				    		
						}
						this.insertKategorihelpfunction(scanner, conn, ProdID, sjangerIDny, i);
					}
					else if(answer.equalsIgnoreCase("ja")){
						String Regquery1 = "insert into kategorisert values (" + KategoriID +","+  ProdID +");";
						try {
							PreparedStatement stmt2 = conn.prepareStatement(Regquery1);
							stmt2.execute();
							System.out.println("Sjanger med sjangerID: "+ KategoriID + " har blitt satt som sjanger til denne filmen.");
						}
						catch (Exception e){
							System.out.println("Error med database:\n"+e);
						}	
					}
					else {
						throw new IllegalArgumentException("Du har skrevet noe ugyldig inn i feltet.");
					}
				}
			} catch (Exception e){
	    		System.out.println("Error med database:\n"+e);
	    		}
    	}
    	this.insertMusikk(scanner, conn, ProdID);
    }
    	
    //Sjekker om sangen allerede eksisterer i databasen
    private boolean MusicDoesntExist(Scanner scanner, Connection conn, int MusikkID) throws SQLException {
    	String query = "SELECT musikk.musikkID\r\n" + 
    			"FROM musikk\r\n" + 
    			"Where musikk.musikkID=" + MusikkID + ";";
    	PreparedStatement stmt = conn.prepareStatement(query);
    	stmt.execute();
    	ResultSet rs = stmt.executeQuery(query);
    	if (!rs.next()) {
			return true;
		}
    	else {
    		return false; 	
    		} 
    }
    
    //Funksjon for å legge til sang i databasen - hjelpefunksjon
    private void insertMusikkhelpfunction(Scanner scanner, Connection conn, int ProdID, int MusikkID, int i) {
    	System.out.println("Angi komponistnavn: ");
		scanner.nextLine();
		String KompNavn = scanner.nextLine();
		System.out.println("Angi Fremførernavn: ");
		String FremfoererNavn = scanner.nextLine();
		String Regquery = "insert into harmusikktilproduksjon values (" + MusikkID +","+  ProdID +");";
		String perquery = "insert into musikk values (' "  +MusikkID + "', '" +KompNavn+ "', '" + FremfoererNavn + "');";
		try {
			PreparedStatement stmt1 = conn.prepareStatement(perquery);
			stmt1.execute();
			PreparedStatement stmt = conn.prepareStatement(Regquery);
			stmt.execute();
			System.out.println("Sang "+ i + " med komponist:  " + KompNavn + " ble lagt til i databasen.");
		}
		catch (Exception e){
			System.out.println("Error med database:\n"+e);
		}	
    	
    }
    	
    // funksjon for å legge til sang i databasen 
    private void insertMusikk(Scanner scanner, Connection conn, int ProdID) {
       	System.out.println("Antall sanger du ønsker å sette inn: ");
    	int sangertall = scanner.nextInt();
    	for(int i = 1; i<= sangertall; i++) {
    		System.out.println("MusikkID for sang: ");
    		int MusikkID = scanner.nextInt();
    		try {
    			// Sjekker om kategori allerede eksisterer i databasen
				if ((this.MusicDoesntExist(scanner, conn, MusikkID))){
					this.insertMusikkhelpfunction(scanner, conn, ProdID, MusikkID, i);
				}
				// Dersom Selskap allerede eksisterer i databasen, kan man velge å knytte Selskapet i databasen med filmen
				//Eller så kan man bruke ny SelskapID
				else {
					System.out.println("Denne sangen finnes allerede i databasen");
					scanner.nextLine();
					System.out.println("Vil du at databasen skal bruke eksisternde sang som sang for denne filmen? (Ja/Nei)");
					String answer = scanner.nextLine();
					if (answer.equalsIgnoreCase("nei")) {
						System.out.println("Ny MusikkID: ");
			    		int MusikkIDny = scanner.nextInt();	
						while(!(this.KategoriDoesntExist(scanner, conn, MusikkIDny))){
							System.out.println("Du må skirve ny MusikkID, siden det du oppgitte allerede eksisterer i databasen.");
							System.out.println("Skriv ny MusikkID for å opprette ny sang: ");
				    		int MusikkIDny1 = scanner.nextInt();
				    		MusikkIDny = MusikkIDny1;
				    		
						}
						this.insertMusikkhelpfunction(scanner, conn, ProdID, MusikkIDny, i);
					}
					else if(answer.equalsIgnoreCase("ja")){
						String Regquery1 = "insert into harmusikktilproduksjon values (" + MusikkID +","+  ProdID +");";
						try {
							PreparedStatement stmt2 = conn.prepareStatement(Regquery1);
							stmt2.execute();
							System.out.println("Sang med MusikkID: "+ MusikkID + " har fått en kobling til denne filmen.");
						}
						catch (Exception e){
							System.out.println("Error med database:\n"+e);
						}	
					}
					else {
						throw new IllegalArgumentException("Du har skrevet noe ugyldig inn i feltet.");
					}
				}
			} catch (Exception e){
	    		System.out.println("Error med database:\n"+e);
	    		}
    	}
    }
    	
 
    private void insertSerie(Scanner scanner, Connection conn, int ProdID) {
    	System.out.println("Episodenr: ");
    	int Episodenr = scanner.nextInt();
    	System.out.println("Sesongnr: ");
    	int Sesongnr = scanner.nextInt();
    	System.out.println("Lengde (HH:MM:SS): ");
    	scanner.nextLine();
    	String Lengde = scanner.nextLine();
    	System.out.println("Produksjonstittel: ");
    	String ProdTittel = scanner.nextLine();
    	System.out.println("Produksjonstype (TV, Streaming eller Kino): ");
    	String ProdType = scanner.nextLine();
    	System.out.println("Utgivelsesår(yyyy): ");
    	int UtAar = scanner.nextInt();
    	System.out.println("Lanseringsdato (yyyy-mm-dd):");
    	scanner.nextLine();
    	String LanDato = scanner.nextLine();
    	System.out.println("Produksjonsbeskrivelse: ");
    	String ProdBeskrivelse = scanner.nextLine();
    	System.out.println("Video (True/False): ");
    	String Video = scanner.nextLine();
    	String FilmSerie = "Serie";
    	String Prodquery = "INSERT INTO produksjon\r\n" + 
    			"VALUES ("+ProdID+", '"+ FilmSerie + "', '"+ Episodenr + "', '"+ Sesongnr + "', '"+ Lengde+"', '"+ProdTittel+"', '"+ProdType+"', "+UtAar+", '"
    					+ LanDato+"', '"+ProdBeskrivelse+"', '"+Video+"');"; 	
    	
    	try {
    		PreparedStatement stmt = conn.prepareStatement(Prodquery);
    		stmt.execute();
    		System.out.println("Serie: Produksjonen " + ProdTittel + " ble lagt til i databasen. Trykk enter for å legge inn forfatter.");
    		scanner.nextLine();
    		this.insertforfatter(scanner, conn, ProdID);
    	}
    	catch (Exception e){
    		System.out.println("Error med database:\n"+e);
    	}
    	
    }
    
    // Use case 4: Sette inn en ny film med regissør, manusforfattere, skuespillere og det som hører med.
    private void insertNewMovie(Scanner scanner, Connection conn) {
       	System.out.println("Sett inn en ny film eller serie.\n----------------");
    	//Sette inn ny film
        scanner.nextLine();
       	System.out.println("Skal du sette inn en film eller serie? ");
       	String FilmSerie = scanner.nextLine();
        scanner.nextLine();
       	if(FilmSerie.equalsIgnoreCase("Film")) {
       		System.out.println("Sett inn FilmID: ");
        	int ProdID = scanner.nextInt();
        	try {
    			if (this.checkIfMovieDuplicate(ProdID)) {
    				this.insertMovie(scanner, conn, ProdID); // Må lage variabler som sier om det er serie, sesong, episode;
    			}
    			else {
    				System.out.println("FilmID'en eksisterer allerede i databasen");
    			}
    		} catch (Exception e) {
    			System.out.println("Error med database:\n"+e);
    		}
       	}
       	else if(FilmSerie.equalsIgnoreCase("Serie")){
       		System.out.println("Sett inn SerieID: ");
        	int ProdID = scanner.nextInt();
        	try {
    			if (this.checkIfMovieDuplicate(ProdID)) {
    				this.insertSerie(scanner, conn, ProdID); // Må lage variabler som sier om det er serie, sesong, episode;
    			}
    			else {
    				System.out.println("ProduksjonID'en eksisterer allerede i databasen");
    			}
    		} catch (Exception e) {
    			System.out.println("Error med database:\n"+e);
    		}
       	}	
    }
    
    //KODE TIL CASE 5:
    
    private void insertReviewHelpFunction(Scanner scanner, Connection conn, int BrukerID, int ProdID) {
    	System.out.println("Ranger (1-10):");
    	int Rating = scanner.nextInt();
    	System.out.println("Skriv en annmeldelse (skriv på samme linje): ");
    	scanner.nextLine();
    	String Kommentar = scanner.nextLine();
    	String Regquery = "insert into AnmeldelseAvProduksjon values (" + ProdID +","+  BrukerID +","+ Rating +",'"+ Kommentar +"');";
		try {
			PreparedStatement stmt = conn.prepareStatement(Regquery);
			stmt.execute();
			System.out.println("Anmeldelse av produksjon med ID "+ ProdID + " av bruker:  " + BrukerID + " ble lagt til i databasen.");
		}
		catch (Exception e){
			System.out.println("Error med database:\n"+e);
		}	
    	
    	
    	
    }
    
    private boolean checkIfUserDoesntExist(Scanner scanner, Connection conn, int BrukerID) throws SQLException {
    	String query = "SELECT bruker.brukerID\r\n" + 
    			"FROM Bruker\r\n" + 
    			"Where bruker.brukerID = " + BrukerID + ";";
    	PreparedStatement stmt = conn.prepareStatement(query);
    	stmt.execute();
    	ResultSet rs = stmt.executeQuery(query);
    	if (!rs.next()) {
			return true;
		}
    	else {
    		return false; 	
    		}
    	
    }
    
    private void insertUser(Scanner scanner, Connection conn, int BrukerID) {
    	String Regquery = "insert into Bruker values (" + BrukerID + ");";
		try {
			PreparedStatement stmt = conn.prepareStatement(Regquery);
			stmt.execute();
			System.out.println("Ny bruker med brukerID: "+ BrukerID +  " ble lagt til i databasen.");
		}
		catch (Exception e){
			System.out.println("Error med database:\n"+e);
		}	
    	
    	
    	
    }

    // Use case 5: Sette inn ny anmeldelse av en episode av en serie.
    private void insertReview(Scanner scanner, Connection conn) {
    	System.out.println("Sette inn ny anmeldelse.\n----------------");
    	System.out.println("Hva er BrukerID'en din (tall)? ");
    	int BrukerID = scanner.nextInt();
    	try {
			if (this.checkIfUserDoesntExist(scanner, conn, BrukerID)) {
				this.insertUser(scanner, conn, BrukerID);
				scanner.nextLine();
		    	System.out.println("Angi ProduksjonsID'en til filmen/episoden du vil anmelde: ");
		    	int ProdID = scanner.nextInt();
		    	try {
					if(this.checkIfMovieDuplicate(ProdID)) {
						System.out.println("Denne filmen/episoden eksisterer ikke i databasen.");
					}
					else {
						this.insertReviewHelpFunction(scanner, conn, BrukerID, ProdID);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				scanner.nextLine();
		    	System.out.println("Angi ProduksjonsID'en til filmen/episoden du vil anmelde: ");
		    	int ProdID = scanner.nextInt();
		    	try {
					if(this.checkIfMovieDuplicate(ProdID)) {
						System.out.println("Denne filmen/episoden eksisterer ikke i databasen.");
					}
					else {
						this.insertReviewHelpFunction(scanner, conn, BrukerID, ProdID);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}    	
    	
    }
    
    

    public static void main(String[] args) {
		IMDBProject_v2 dbproject = new IMDBProject_v2();
		dbproject.checkDriver();
		
		// Spawn connection and scanner object
		Connection con = dbproject.connect();
		Scanner scanner = new Scanner(System.in);

		// main
		boolean cont = true;
		System.out.println("Velkommen til programmet vårt");
		System.out.println("Velg funksjon:\n"
				+ " 1:\tFinne navnet på alle roller til en skuespiller\n"
				+ " 2:\tFinne hvilke filmer en skuespiller opptrer i\n"
				+ " 3:\tFinne hvilke filmselskap som har flest filmer innen en sjanger\n"
				+ " 4:\tSette inn en ny film\n"
				+ " 5:\tSette inn annmeldelse.\n"
				+ "Eller 0 for å avslutte");
		while (cont) {
			System.out.println("Meny");
			System.out.print(">>");
			int valg = -1; // force default in switch if input not correct.
			try {
				valg = scanner.nextInt();
			}catch(Exception e) {
				scanner.nextLine(); // Flush incorrect input
				System.out.println("OBS! Må være heltall 0-7");
			}

			switch (valg) {
				case 0:
					cont = false;
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					break;
				case 1:
					dbproject.findRoles(scanner, con);
					break;
				case 2:
					dbproject.findMovies(scanner, con);
					break;
				case 3:
					dbproject.corpWithMostInGenre(scanner, con);
					break;
				case 4:
					dbproject.insertNewMovie(scanner, con);
					break;
				case 5:
					dbproject.insertReview(scanner, con);
					break;
				default:
					System.out.println("Ikke gyldig valg");
					break;
			}
		}
		System.out.println("Program avsluttet.");

	}

}
