package ru.gb.server;
import java.sql.*;
import java.util.Arrays;

public class DataBaseService {
    private Connection connection;
    private Statement statement;

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }

    public DataBaseService() {
        try {
            connectDb();
            System.out.println("Connected ok");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void connectDb() throws Exception {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:ServerDB.db");
        statement = connection.createStatement();
    }

    public void creatRegistration(String login, String password, String name, String surname) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement
                ("INSERT INTO ClientsDate (Login, Password, Name, Surname) VALUES (?, ?, ?, ?);")){
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, name);
            preparedStatement.setString(4, surname);
            preparedStatement.executeUpdate();
        }

    }
    public boolean hasRegistration(String login) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement
                ("SELECT Login FROM ClientsDate WHERE Login = ?;")){
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
        }
        return false;
    }
    public boolean hasAuth(String login, String password) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement
                ("SELECT Login,Password FROM ClientsDate WHERE Login = ? AND Password = ?;")){
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                    if(resultSet.getString(1).equals(login)&&resultSet.getString(2).equals(password)){
                        return true;
                    }
                }
        }
        return false;
    }
    public String creatAuth(String login, String password) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement
                ("SELECT  Login, Password, id, Name, Surname FROM ClientsDate WHERE Login = ? AND Password = ?;")){
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                if(resultSet.getString(1).equals(login)&&resultSet.getString(2).equals(password)){
                    return (resultSet.getString(3)+"_"+resultSet.getString(4)+"."+ Arrays.hashCode(resultSet.getString(5).getBytes()));
                }
            }
        }
        return "Not_found";
    }
    public void authRegister(String auth, String login) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement
                ("INSERT INTO AuthTable (auth, login) VALUES (?, ?);") ){
            preparedStatement.setString(1, auth);
            preparedStatement.setString(2, login);
            preparedStatement.executeUpdate();

        }
    }

    public boolean isAuthRegister(String auth, String login) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement
                ("SELECT id FROM AuthTable WHERE auth = ? and login = ?;")){
            preparedStatement.setString(1, auth);
            preparedStatement.setString(2, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
        }
        return false;
    }

    public boolean hasAuthRegister(String auth, String login) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement
                ("SELECT auth, login FROM AuthTable WHERE auth = ? and login = ?;")){
            preparedStatement.setString(1, auth);
            preparedStatement.setString(2,login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getString(1).equals(auth)&&resultSet.getString(2).equals(login);
            }
        }
        return false;
    }
}
