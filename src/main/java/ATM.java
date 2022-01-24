import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ATM {
    private static BufferedReader reader;
    private static BufferedReader in;
    private static BufferedWriter out;
    private static Statement statement;


    public static void main(String[] args) {
        int error = 0;
        while (error<5) {
            try {
                reader = new BufferedReader(new InputStreamReader(System.in));
                Socket clientSocket = new Socket("localhost", 3350);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ATM", "ATM", "123");
                statement = connection.createStatement();
            } catch (IOException | SQLException | NullPointerException e) {
                error++;
                System.out.println("��������� ������������ � ���� ������ ��� �������");
            }
            while (error< 5) {
                if (checkNumberCard()) {
                    while (true) {
                        System.out.println("--------������� ����--------\n" +
                                "1.������� ������ �� �����\n" +
                                "2.����� ������\n" +
                                "3.��������� �� ������ ����\n" +
                                "4.������ ������\n" +
                                "6.���������� ������\n" +
                                "������� ��� ������ ��������: ");
                        try {
                            String input = reader.readLine();
                            if (checkPatternOneNumber(input)) {
                                if (Integer.parseInt(input) == 1) {
                                    out.write(1 + "\n");
                                    out.flush();
                                    String read = in.readLine();
                                    System.out.println("��� ������: " + read);
                                }
                                if (Integer.parseInt(input) == 2) {
                                    while (true) {
                                        try {
                                            System.out.println("������� �����, ������� 100: " +
                                                    "\n��� �������� � ���������� ���� ������� 0");
                                            int temp = Integer.parseInt(reader.readLine());
                                            if (temp == 0) break;
                                            if (temp % 100 == 0 && temp > 0) {
                                                if (!checkMoneyATM(temp)) {
                                                    System.out.println("� ������ ������ " +
                                                            "������ ����������, ����������� ������");
                                                    continue;
                                                }
                                                out.write(2 + "\n");
                                                out.flush();
                                                out.write(temp + "\n");
                                                out.flush();
                                                int tempAnswer = Integer.parseInt(in.readLine());
                                                if (tempAnswer == 1) {
                                                    System.out.println("�������� ������ �������, ��� ������: " +
                                                            "" + Integer.parseInt(in.readLine()));
                                                    break;
                                                }
                                                if (tempAnswer == 0) {
                                                    System.out.println("������������ �������, ��������� �������");
                                                }
                                            } else {
                                                System.out.println("������������ �����, ������� ����� ������� 100");
                                            }
                                        } catch (NumberFormatException e) {
                                            System.out.println("�������� ������, ��������� ����");
                                        }

                                    }
                                }
                                if (Integer.parseInt(input) == 3) {
                                    while (true) {
                                        try {
                                            System.out.println("������� ����� ����� �� ������� ������ "
                                                    + "��������� ������ � ����� : \n��� ������ ������� 0");
                                            String number = reader.readLine();
                                            if (Long.parseLong(number) == 0) break;
                                            String sum = reader.readLine();
                                            if (checkPatternNumber(number) && Long.parseLong(number) > 0 && Long.parseLong(sum) > 0) {
                                                out.write(3 + "\n");
                                                out.flush();
                                                out.write(number + "\n");
                                                out.flush();
                                                out.write(sum + "\n");
                                                out.flush();
                                                int serverAnswer = Integer.parseInt(in.readLine());
                                                if (serverAnswer == 1) {
                                                    System.out.println("�������� ���������, ��� ������ " + Integer.parseInt(in.readLine()));
                                                    break;
                                                }
                                                if (serverAnswer == 2) {
                                                    System.out.println("������ ������ ���, ��������� �������");
                                                }
                                                if (serverAnswer == 3) {
                                                    System.out.println("������������ �������");
                                                }
                                            } else if (Long.parseLong(number) == 0) break;
                                            else {
                                                System.out.println("������������ ������ ������");
                                            }
                                        } catch (NumberFormatException e) {
                                            System.out.println("�������� ������, ��������� ����");
                                        }

                                    }
                                }
                                if (Integer.parseInt(input) == 4) {
                                    while (true) {
                                        try {
                                            System.out.println("������� �����, ������� 100: \n" + "��� ������ ������� 0");
                                            int total = Integer.parseInt(reader.readLine());
                                            if (total == 0 || total < 0) break;
                                            if (total % 100 == 0) {
                                                out.write(4 + "\n");
                                                out.flush();
                                                out.write(total + "\n");
                                                out.flush();
                                                System.out.println("�������� ��������� �������," + "��� ������ ����������: "
                                                        + Integer.parseInt(in.readLine()));
                                                break;
                                            } else {
                                                System.out.println("������������ �����, ������� ����� ������� 100");
                                            }
                                        } catch (NumberFormatException e) {
                                            System.out.println("�������� ������, ��������� ����");
                                        }

                                    }
                                }
                                if (Integer.parseInt(input) == 6) {
                                    out.write(6 + "\n");
                                    out.flush();
                                    break;
                                }
                            } else {
                                System.out.println("��������� �������");
                            }
                        } catch (IOException e) {
                            error++;
                            System.out.println("�������� ������ �����");
                        }
                    }
                }
            }
        }

    }


    public static boolean checkNumberCard() {
        System.out.println("����� ����������!\n������� ����� �����: ");
        int temp = 0;
        try {
            while (temp < 3) {
                Pattern pattern = Pattern.compile("\\d{16}");
                String clientNumber = reader.readLine();
                Matcher matcher = pattern.matcher(clientNumber);
                if (matcher.find()) {
                    out.write(10 + "\n");
                    out.flush();
                    out.write(clientNumber + "\n");
                    out.flush();
                    System.out.println("������� ���-���: ");
                    String pinCode = reader.readLine();
                    out.write(pinCode + "\n");
                    out.flush();
                    int answer = Integer.parseInt(in.readLine());
                    if (answer == 1) {
                        return true;
                    }
                }
                if (temp == 2) {
                    System.out.println("�� ������������ 3 �������");
                    break;
                }
                temp++;
                System.out.println("����������� ������ ����� ����� ��� ������, ��������� �������: ");
            }
        } catch (IOException e) {
            System.out.println("������ �����");
        }
        return false;
    }

    public static boolean checkPatternOneNumber(String string) {
        Pattern pattern = Pattern.compile("\\d");
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }

    public static boolean checkPatternNumber(String string) {
        Pattern pattern = Pattern.compile("\\d{10}");
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }

    public static boolean checkMoneyATM(int total) {
        int sum = total;
        try {
            String sql = "select count5000 from moneyinatm";
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            int particular4 = total / 5000;
            int count = resultSet.getInt("count5000");
            if (particular4 <= count && particular4 != 0) {
                sum = sum - 5000 * particular4;
                sql = "update moneyinatm set count5000=" + (count - particular4) + " where id=1";
                statement.executeUpdate(sql);
            }
            sql = "select count1000 from moneyinatm";
            resultSet = statement.executeQuery(sql);
            resultSet.next();
            int tics4a = sum / 1000;
            count = resultSet.getInt("count1000");
            if (tics4a <= count && tics4a != 0) {
                sum = sum - 1000 * tics4a;
                sql = "update moneyinatm set count1000=" + (count - tics4a) + " where id=1";
                statement.executeUpdate(sql);
            }
            sql = "select count500 from moneyinatm";
            resultSet = statement.executeQuery(sql);
            resultSet.next();
            count = resultSet.getInt("count500");
            int plats = sum / 500;
            if (plats <= count && plats != 0) {
                sum = sum - 500 * plats;
                sql = "update moneyinatm set count500=" + (count - plats) + " where id=1";
                statement.executeUpdate(sql);
            }
            sql = "select count100 from moneyinatm";
            resultSet = statement.executeQuery(sql);
            resultSet.next();
            count = resultSet.getInt("count100");
            int sto = sum / 100;
            if (sto <= count && sto != 0) {
                sum = sum - 100 * sto;
                sql = "update moneyinatm set count100=" + (count - sto) + " where id=1";
                statement.executeUpdate(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sum == 0;


    }

}