package dev.sammy_ulfh.ai;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
    public static void main(String[] args) {
        try {
            String url = "jdbc:oracle:thin:@gestiondetareasbd_high?TNS_ADMIN=/Users/diana/.../wallet";
            String user = "CHATBOT_USER";
            String pass = "Chatbot2026AB";

            Connection conn = DriverManager.getConnection(url, user, pass);
            System.out.println("✅ CONECTADO");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}