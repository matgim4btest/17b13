package rs.edu.matgim.zadatak;

import java.sql.SQLException;

public class Program {

    public static void main(String[] args) throws SQLException {

        DB _db = new DB();
        _db.printRacun();
        float f = _db.zadatak(2,3);
        _db.printRacun();
        System.out.println(f);
        _db.close();
    }
}
