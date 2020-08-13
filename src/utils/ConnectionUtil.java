/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
/**
 *
 * @author Rian03
 */
public class ConnectionUtil {
    private static Connection conn = null;
    
    public static Connection conDB() {
        String database = "jdbc:sqlite:persil.db";
        try {
            conn = DriverManager.getConnection(database);
            System.err.println("Database Connected");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        return conn;
        
    }
    
    public static Connection connect() {
        String database = "jdbc:sqlite:persil.db";
        try {
            conn = DriverManager.getConnection(database);
            System.err.println("Database Connected");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        return conn;
        
    }
    
    public static void close() {
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) { 
                System.out.println(ex.getMessage());
            }
        }
    }
    
//    public void createTable() {
//        String sql = "CREATE TABLE IF NOT EXISTS 'kelas_desa' (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,nama text NOT NULL);"
//                + "CREATE TABLE IF NOT EXISTS 'passing' (set_id varchar(50) NOT NULL PRIMARY KEY AUTOINCREMENT,id int(11) NOT NULL,nama text NOT NULL,nomor int(11) NOT NULL);"
//                + "CREATE TABLE IF NOT EXISTS 'persil' (id int(11) NOT NULL PRIMARY KEY AUTOINCREMENT,nama text NOT NULL);"
//                + "CREATE TABLE IF NOT EXISTS 'subpersil' (id int(11) NOT NULL PRIMARY KEY AUTOINCREMENT,,id_persil int(11) NOT NULL,nopersil varchar(100) NOT NULL,id_kelas int(11) NOT NULL,sub_ha bigint(20) NOT NULL,sub_da bigint(20) NOT NULL,sub_rp bigint(20) NOT NULL,sub_s int(100) NOT NULL,sebab text NOT NULL,tanggal_perubahan text NOT NULL);";
//
//        Connection connection = ConnectionUtil.conDB();
//        Statement stmt;
//        try {
//            stmt = connection.createStatement();
//            stmt.execute(sql);
//        } catch (SQLException ex) {
//            System.err.println(ex.getMessage());
//        }
//
//    }
}
