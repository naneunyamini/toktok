package Server.Function;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class DatabaseManager {
    private Connection connection; // 데이터베이스 연결 객체

    public DatabaseManager() throws SQLException {
        // localhost의 MySQL 데이터베이스에 연결한다. (사용자명: root, 패스워드: 1234)
        this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sampledb", "root", "1234");
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public boolean registerUser(String name, String id, String pw) {
        try {
            // student 테이블에 사용자 정보를 추가하는 SQL 쿼리
            String query = "INSERT INTO student (name, id, pw) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, id);
                preparedStatement.setString(3, pw);

                // 쿼리를 실행하고 영향을 받은 행의 수를 확인하여 등록 성공 여부를 반환한다.
                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isIdDuplicate(String id) {
        try {
            // student 테이블에서 지정된 ID가 이미 존재하는지 확인하는 SQL 쿼리
            String query = "SELECT id FROM student WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, id);

                // 쿼리를 실행하고 결과를 확인하여 중복 여부를 반환한다.
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkLogin(String id, String pw) {
        try {
            // student 테이블에서 지정된 ID와 비밀번호로 로그인을 확인하는 SQL 쿼리
            String query = "SELECT * FROM student WHERE id = ? AND pw = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, pw);

                // 쿼리를 실행하고 결과를 확인하여 로그인 성공 여부를 반환한다.
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean signOutUser(String id) {
        try {
            // student 테이블에서 지정된 ID를 사용하여 사용자를 삭제하는 SQL 쿼리
            String query = "DELETE FROM student WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, id);

                // 쿼리를 실행하고 영향을 받은 행의 수를 확인하여 삭제 성공 여부를 반환한다.
                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean doesUserExist(String id) {
        try {
            // student 테이블에서 지정된 ID가 존재하는지 확인하는 SQL 쿼리
            String query = "SELECT id FROM student WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, id);

                // 쿼리를 실행하고 결과를 확인하여 존재 여부를 반환한다.
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUserName(String id) {
        try {
            // student 테이블에서 지정된 ID를 사용하여 사용자의 이름을 가져오는 SQL 쿼리
            String query = "SELECT name FROM student WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, id);

                // 쿼리를 실행하고 결과를 확인하여 사용자 이름을 반환한다.
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("name");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCurrentPassword(String id) {
        try {
            // student 테이블에서 지정된 ID를 사용하여 사용자의 현재 비밀번호를 가져오는 SQL 쿼리
            String query = "SELECT pw FROM student WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, id);

                // 쿼리를 실행하고 결과를 확인하여 사용자의 현재 비밀번호를 반환한다.
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("pw");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean changePassword(String id, String newPassword) {
        try {
            // student 테이블에서 지정된 ID를 사용하여 사용자의 비밀번호를 변경하는 SQL 쿼리
            String query = "UPDATE student SET pw = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, newPassword);
                preparedStatement.setString(2, id);

                // 쿼리를 실행하고 영향을 받은 행의 수를 확인하여 변경 성공 여부를 반환한다.
                int rowsAffected = preparedStatement.executeUpdate();

                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}