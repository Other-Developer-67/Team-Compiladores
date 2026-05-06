/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.lspb.db_sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author santiago
 */

public class DB_SQLite {

    public static void main(String[] args) {
        try {
            // La BD se crea automáticamente en el archivo "mi_base.db"
            String url = "jdbc:sqlite:mi_base.db";
            Class.forName("org.sqlite.JDBC");
            try (Connection conn = DriverManager.getConnection(url)) {
                if (conn != null) {
                    System.out.println("¡Conectado sin internet!");
                    
                    // Crear tabla
                    Statement stmt = conn.createStatement();
                    stmt.execute("PRAGMA foreign_keys = ON;");
                    stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (" +
                            "id INTEGER PRIMARY KEY, nombre TEXT)");
                    
                    // Insertar datos
                    stmt.execute("INSERT INTO usuarios (nombre) VALUES ('Ana')");
                    
                    // Consultar
                    ResultSet rs = stmt.executeQuery("SELECT * FROM usuarios");
                    while (rs.next()) {
                        System.out.println(rs.getInt("id") + ": " + rs.getString("nombre"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException ex) {
            System.getLogger(DB_SQLite.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}
