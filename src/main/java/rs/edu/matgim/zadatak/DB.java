package rs.edu.matgim.zadatak;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DB {

    String connectionString = "jdbc:sqlite:src/main/java/Banka.db";
    
    Connection con;

    public DB() throws SQLException {
        this.con = DriverManager.getConnection(connectionString);
        this.con.setAutoCommit(false);
    }
    
    public void printKomitent() {
        try ( Connection conn = DriverManager.getConnection(connectionString);  Statement s = conn.createStatement()) {

            ResultSet rs = s.executeQuery("SELECT * FROM Komitent");
            while (rs.next()) {
                int IdKom = rs.getInt("IdKom");
                String Naziv = rs.getString("Naziv");
                String Adresa = rs.getString("Adresa");

                System.out.println(String.format("%d\t%s\t%s", IdKom, Naziv, Adresa));
            }

        } catch (SQLException ex) {
            System.out.println("Greska prilikom povezivanja na bazu");
            System.out.println(ex);
        }
    }
    
    public void printRacun() {
        try ( Connection conn = DriverManager.getConnection(connectionString);  Statement s = conn.createStatement()) {

            ResultSet rs = s.executeQuery("SELECT * FROM Racun  WHERE Status = 'A'");
            while (rs.next()) {
                int IdRac = rs.getInt("IdRac");
                String Status = rs.getString("Status");
                int brstavki = rs.getInt("BrojStavki");
                int dminus = rs.getInt("DozvMinus");
                int stanje = rs.getInt("Stanje");
                int IdFil = rs.getInt("IdFil");
                int IdKom = rs.getInt("IdKom");
                
                System.out.println(String.format("%d\t%s\t%d\t%d\t%d\t%d\t%d", IdRac, Status, brstavki, dminus, stanje, IdFil, IdKom));
            }
            rs.close();

        } catch (SQLException ex) {
            System.out.println("Greska prilikom povezivanja na bazu");
            System.out.println(ex);
        }
    }
    
    public void update(int id, int stanje, int brStavki) throws SQLException{
        Statement stmt = con.createStatement();
        String query = "UPDATE Racun SET Stanje = " + stanje + ", Status = 'A', BrojStavki = " + brStavki + " WHERE IdRac=" + id;
        stmt.execute(query);
        stmt.close();
    }
    
    public void insert(String datum, String vreme, int iznos, int idFil, int idRac) throws SQLException{
        
        String osnov = new String("Uplata na zahtev gradjanina");
        
        PreparedStatement stmt, stmt2;
        String query = "INSERT INTO Stavka VALUES (NULL, NULL, ?, ?, ?, ?, ?)";
        String query2 = "INSERT INTO Uplata VALUES (?, ?)";
        
        stmt = con.prepareStatement(query);
        stmt.setString(1, datum);
        stmt.setString(2, vreme);
        stmt.setInt(3, iznos);
        stmt.setInt(4, idFil);
        stmt.setInt(5, idRac);
        
        ResultSet rs = stmt.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        
        stmt2 = con.prepareStatement(query);
        stmt2.setInt(1, id);
        stmt2.setString(2, osnov);
        
        stmt.execute();
        stmt.close();
    }
    
    public float zadatak(int idFil, int idKom) throws SQLException{
        float suma = 0;
        String datum = new String();
        String vreme = new String();
        
        LocalTime time = LocalTime.now();
        vreme = time.toString();
        LocalDate date = LocalDate.now();
        datum = date.toString();
        
        try {
            Statement stmt = con.createStatement();
            String query = "Select IdRac, Stanje, DozvMinus, BrojStavki From Racun WHERE IdKom=" + idKom + " AND Status='B'";
            ResultSet rs = stmt.executeQuery(query);
            
            while(rs.next()){
            int idRac = rs.getInt("IdRac");
            int stanje = rs.getInt("Stanje");
            int dozvMinus  = rs.getInt("DozvMinus");
            int brStavki  = rs.getInt("BrojStavki");
            
            int iznos = -stanje - dozvMinus;
            suma += iznos;
            stanje = -dozvMinus;
            //System.out.println(iznos);
            update(idRac, stanje, brStavki+1);
            insert(datum, vreme, iznos, idFil, idRac);
            
            }
            rs.close();
            con.commit();
            return suma;
            
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
            con.rollback();
            return 0;
        }
        
    }
    
    public void close() throws SQLException{
        con.close();
    }
    
    
    
    
    
    
    
//    public float zadatak(int idFil, int  idKom){
//        float f=0;
//        String datum = new String();
//        datum="";
//        String osnov = "Uplata na zahtev gradjanina.";
//        
//        try ( Connection con = DriverManager.getConnection(connectionString); 
//                PreparedStatement ps = con.prepareStatement("Select Stanje, DozvMinus, IdRac From Racun WHERE IdFil=? AND IdKom=?");
//                PreparedStatement psupdate = con.prepareStatement("UPDATE Racun SET Stanje=? WHERE IdRac=?");
//                PreparedStatement psinsert = con.prepareStatement("INSERT INTO Stavka VALUES (NULL, NULL, NULL, NULL, ?, ?, ?)");
//                PreparedStatement psinsert2 = con.prepareStatement("INSERT INTO Uplata VALUES (?, ?)")
//                ) {
//            con.setAutoCommit(false);
//            ps.setInt(1,idFil);
//            ps.setInt(2,idKom);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                int stanje = rs.getInt("Stanje");
//                int dminus = rs.getInt("DozvMinus");
//                int idrac = rs.getInt("IdRac");
//                if(-stanje>dminus){
//                    
//                    int iznos = -dminus - stanje;
//                    f += iznos;
//                    
//                    psupdate.setInt(1,-dminus);
//                    psupdate.executeUpdate();
//                    
//                    psinsert.setInt(1, iznos);
//                    psinsert.setInt(2,idFil);
//                    psinsert.setInt(3,idrac);
//                    psinsert.executeUpdate();
//                    
//                    psinsert2.setInt(1, iznos);
//                    psinsert2.setString(2,osnov);
//                    psinsert2.executeUpdate();
//                }
//
//            }
//            con.commit();
//            System.out.println("Uspesna relizacija.");
//
//        } catch (SQLException ex) {
//            System.out.println("Dogodila se greska.");
//            System.out.println(ex);
//        } catch (Exception e){
//            System.out.println("Dogodila se greska.");
//            System.out.println(e);
//        } /*finally{
//            try {
//                con.rollback();
//                con.close();
//            } catch (SQLException ex1) {
//                Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex1);
//            }
//        }*/
//        return f;
//    }

}
