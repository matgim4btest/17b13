package rs.edu.matgim.zadatak;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DB {

    String connectionString = "jdbc:sqlite:src/main/java/Banka.db";

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

            ResultSet rs = s.executeQuery("SELECT * FROM Racun WHERE Status = 'A'");
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
    
    Connection con;
    
    public float zadatak(int idFil, int  idKom){
        float f=0;
        String datum = new String();
        datum="";
        String osnov = "Uplata na zahtev gradjanina.";
        
        try ( Connection con = DriverManager.getConnection(connectionString); 
                PreparedStatement ps = con.prepareStatement("Select Stanje, DozvMinus, IdRac From Racun WHERE IdFil=? AND IdKom=?");
                PreparedStatement psupdate = con.prepareStatement("UPDATE Racun SET Stanje=? WHERE IdRac=?");
                PreparedStatement psinsert = con.prepareStatement("INSERT INTO Stavka VALUES (NULL, NULL, NULL, NULL, ?, ?, ?)");
                PreparedStatement psinsert2 = con.prepareStatement("INSERT INTO Uplata VALUES (?, ?)")
                ) {
            con.setAutoCommit(false);
            ps.setInt(1,idFil);
            ps.setInt(2,idKom);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int stanje = rs.getInt("Stanje");
                int dminus = rs.getInt("DozvMinus");
                int idrac = rs.getInt("IdRac");
                if(-stanje>dminus){
                    
                    int iznos = -dminus - stanje;
                    f += iznos;
                    
                    psupdate.setInt(1,-dminus);
                    psupdate.executeUpdate();
                    
                    psinsert.setInt(1, iznos);
                    psinsert.setInt(2,idFil);
                    psinsert.setInt(3,idrac);
                    psinsert.executeUpdate();
                    
                    psinsert2.setInt(1, iznos);
                    psinsert2.setString(2,osnov);
                    psinsert2.executeUpdate();
                }

            }
            con.commit();
            System.out.println("Uspesna relizacija.");

        } catch (SQLException ex) {
            System.out.println("Dogodila se greska.");
            System.out.println(ex);
        } catch (Exception e){
            System.out.println("Dogodila se greska.");
            System.out.println(e);
        } /*finally{
            try {
                con.rollback();
                con.close();
            } catch (SQLException ex1) {
                Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }*/
        return f;
    }

}
