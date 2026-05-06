/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.lspb.db_mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author santiago
 */
public class DB_MySQL {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/testdb";
        String user = "root"; // usuario por defecto en phpMyAdmin
        String password = ""; // normalmente vacío en local

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Conexión establecida con la base de datos local.");
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (" +
                            "id INT(3) AUTO_INCREMENT PRIMARY KEY , nombre TEXT(64))");
                    
                    // Insertar datos
                    stmt.execute("INSERT INTO usuarios (nombre) VALUES ('Ana')");
                    
                    //Modificar datos
                    stmt.execute("UPDATE usuarios SET nombre = 'Luis' WHERE nombre = 'Ana'");
        } catch (SQLException e) {
            System.out.println("❌ Error de conexión: " + e.getMessage());
        }
    }
}
