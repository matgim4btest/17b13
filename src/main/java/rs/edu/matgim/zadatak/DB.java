package rs.edu.matgim.zadatak;

import java.sql.*;

public class DB {

    String connectionString = "jdbc:sqlite:src\\main\\java\\Banka.db";

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

}
