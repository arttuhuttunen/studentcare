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
        //Grid<HashMap<String, String>> courseGrid = new Grid<>();
        Grid<CourseInstance> courseGrid = new Grid<>();
        Student opt = authentication.getStudent().get();
        //for (int i = 0; i < opt.attending(authentication.getConnection()).size(); i++) {
        //  courseGrid.addColumn(opt.attending(authentication.getConnection()).get(i).wholeNameId(20)).setCaption("Kurssin nimi").setId("courseName");
        //}
        List<CourseInstance> courses = opt.attending(authentication.getConnection()).subList(0, opt.attending(authentication.getConnection()).size());
        Map<String, String> courseNames = new HashMap<>();
        String WHOLENAME = "wholeNameId";
        for (CourseInstance course : courses) {
            courseNames.put(WHOLENAME, course.wholeNameId(20));
        }
        courseGrid.setItems(courses) ;
        courseGrid.addColumn(course -> course.wholeNameId(40)).setCaption("Kurssin nimi");
        System.out.println(courseNames);
        addComponent(courseGrid);
    }
}
