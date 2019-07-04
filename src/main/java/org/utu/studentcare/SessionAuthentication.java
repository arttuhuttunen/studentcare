package org.utu.studentcare;

import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.db.SQLConnection;
import org.utu.studentcare.db.orm.Student;

import java.sql.SQLException;
import java.util.Optional;

public class SessionAuthentication {

    private Optional<Student> student;
    private SQLConnection connection;

    public boolean loginControl(SQLConnection sqlConnection , String username, String password) throws SQLException, AppLogicException {
        if (Student.authenticate(sqlConnection, username, password).equals(Optional.empty())) {
            return false;
        } else {
            student = Student.authenticate(sqlConnection, username, password);
            this.connection = sqlConnection;
            return true;}
    }

    public boolean isUserSignedIn() {
        return student != null;
    }

    public SQLConnection getConnection() {
        return connection;
    }

    public Optional<Student> getStudent() {
        return student;
    }
}
