import java.net.*;
import java.io.*;
import java.sql.*;

public class Bank {
    private static Statement statement;
    private static int idClient;
    private static BufferedReader in;
    private static BufferedWriter out;
    private static ResultSet resultSet;


    public static void main(String[] args) {
        int error = 0;
        while (error < 5) {
            try {
                ServerSocket server = new ServerSocket(3350);
                Socket client = server.accept();
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                Connection connection = DriverManager.getConnection(
                        "jdbc:postgresql://localhost:5432/postgres",
                        "postgres",
                        "22121994");
                statement = connection.createStatement();
            } catch (IOException | SQLException | NumberFormatException | NullPointerException e) {
                error++;
                System.out.println("Неправильный ввод");
            }

            while (error < 5) {
                try {
                    int numberFunction = Integer.parseInt(in.readLine());
                    if (numberFunction == 1) {
                        int temp = checkBalance();
                        out.write(temp + "\n");
                        out.flush();
                    }
                    if (numberFunction == 2) {
                        int total = Integer.parseInt(in.readLine());
                        if (checkBalance() - total >= 0) {
                            int temp = checkBalance() - total;
                            String sql = "update client set accountbalance=" + temp + " where id=" + idClient;
                            statement.executeUpdate(sql);
                            out.write(1 + "\n");
                            out.flush();
                            out.write(checkBalance() + "\n");
                            out.flush();
                        } else {
                            out.write(0 + "\n");
                            out.flush();
                        }
                    }
                    if (numberFunction == 3) {
                        long cardNumber = Long.parseLong(in.readLine());
                        int sum = Integer.parseInt(in.readLine());
                        if (checkBalance() - sum < 0) {
                            out.write(3 + "\n");
                            out.flush();
                        }
                        String sql = "select cardnumber from client where cardnumber= " + cardNumber;
                        ResultSet resultSet = statement.executeQuery(sql);
                        if (resultSet.next()) {
                            int thisBalance = checkBalance() - sum;
                            String sqlThisBalance = "update client set accountbalance=" + thisBalance +
                                    " where id=" + idClient;
                            statement.executeUpdate(sqlThisBalance);
                            String sqlOtherClient = "update client set accountbalance=accountbalance" + "+"
                                    + sum + " where cardnumber=" + cardNumber;
                            statement.executeUpdate(sqlOtherClient);
                            out.write(1 + "\n");
                            out.flush();
                            out.write(checkBalance() + "\n");
                            out.flush();
                        } else {
                            out.write(3 + "\n");
                            out.flush();
                        }
                    }
                    if (numberFunction == 4) {
                        int total = Integer.parseInt(in.readLine());
                        int temp = checkBalance() + total;
                        String sql = "update client set accountbalance=" + temp + " where id=" + idClient;
                        statement.executeUpdate(sql);
                        out.write(checkBalance() + "\n");
                        out.flush();
                    }
                    if (numberFunction == 6) {
                        idClient = 0;
                    }
                    if (numberFunction == 10) {
                        String clientNumber = in.readLine();
                        String pinCode = in.readLine();
                        boolean temp = checkClient(clientNumber, pinCode);
                        if (temp) {
                            out.write(1 + "\n");
                            out.flush();
                        } else {
                            out.write(0 + "\n");
                            out.flush();
                        }
                    }
                } catch (NumberFormatException | NullPointerException | IOException | SQLException e) {
                    error++;
                    System.out.println("Ошибка на сервере");
                }

            }
        }
    }


    public static boolean checkClient(String clientNumber, String pinCode) {
        String sql = "select firstname,id from Client where cardnumber=" + clientNumber + " and password =" + "'"
                + Encoder.getSHA(pinCode) + "'";
        try {
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                idClient = resultSet.getInt("id");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Ошибка запроса");
        }

        return false;
    }

    public static int checkBalance() throws SQLException {
        String sql = "select accountbalance from Client where id=" + idClient;
        resultSet = statement.executeQuery(sql);
        resultSet.next();
        return resultSet.getInt("accountbalance");
    }
}
