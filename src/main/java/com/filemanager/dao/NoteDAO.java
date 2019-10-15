package com.filemanager.dao;

import java.sql.*;

public class NoteDAO {
    private Connection connection;
    private Statement statement;
    private static volatile NoteDAO instance;

    private NoteDAO(){}

    public static NoteDAO getInstance(){
        if(instance == null){
            synchronized(NoteDAO.class){
                if (instance == null){
                    instance = new NoteDAO();
                }
            }
        }
        return instance;
    }

    public void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(
                    "jdbc:sqlite:C:\\Users\\PC\\Desktop\\web3\\notes.db");
            statement = connection.createStatement();
            System.out.println("connect");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void disconnect(){
        try {
            connection.close();
            System.out.println("disconnect");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String getNoteByName(String name){
        String sql = String.format("SELECT note FROM notes\n" +
                "WHERE name='%s';", name);
        try {
            ResultSet rs = statement.executeQuery(sql);
            if(rs.next()){
                return rs.getString(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());;
        }
        return null;
    }
    public void addNoteToDB(String name, String note){
        String sql;
        if(getNoteByName(name) == null){
            sql = String.format("INSERT INTO notes(name, note)\n" +
                    "VALUES ('%s','%s');", name, note);
        }else{
            sql = String.format("UPDATE notes SET note='%s'\n" +
                    "WHERE name='%s';", note, name);
        }
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteNote(String name){
        String sql = String.format("DELETE FROM notes\n" +
                "WHERE name='%s';", name);
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

