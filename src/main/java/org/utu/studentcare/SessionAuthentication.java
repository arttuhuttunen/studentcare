package org.utu.studentcare;

import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.db.SQLConnection;
import org.utu.studentcare.db.orm.Student;

import java.sql.SQLException;
import java.util.Optional;

public class SessionAuthentication {

    private Optional<Student> student;
    private Optional<Student> isTeacher;

    public boolean loginControl(SQLConnection sqlConnection , String username, String password) throws SQLException, AppLogicException {
        if (Student.authenticate(sqlConnection, username, password).equals(Optional.empty())) {
            return false;
        } else {
            student = Student.authenticate(sqlConnection, username, password);

            return true;}
    }

    public boolean isUserSignedIn() {
        return student != null;
    }

    public Optional<Student> getStudent() {
        return student;
    }
}
