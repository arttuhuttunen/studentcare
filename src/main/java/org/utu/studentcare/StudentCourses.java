package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import javafx.beans.binding.When;
import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.db.orm.Course;
import org.utu.studentcare.db.orm.CourseInstance;
import org.utu.studentcare.db.orm.Student;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.*;

public class StudentCourses extends HorizontalLayout implements View {
    public StudentCourses(SessionAuthentication authentication) throws SQLException, AppLogicException {
        addComponent(new Label("Omat kurssit näkymä"));
        Grid<CourseInstance> courseGrid = new Grid<>();
        Student opt = authentication.getStudent().get();
        List<CourseInstance> courses = opt.attending(authentication.getConnection()).subList(0, opt.attending(authentication.getConnection()).size());
        courseGrid.setItems(courses) ;
        courseGrid.addColumn(course -> course.wholeNameId(40)).setCaption("Kurssin nimi");
        addComponent(courseGrid);
    }
}
