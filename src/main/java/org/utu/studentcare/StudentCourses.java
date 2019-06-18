package org.utu.studentcare;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.components.grid.SingleSelectionModel;
import com.vaadin.ui.renderers.ButtonRenderer;
import javafx.beans.binding.When;
import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.db.orm.Course;
import org.utu.studentcare.db.orm.CourseInstance;
import org.utu.studentcare.db.orm.Student;

import javax.swing.*;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.*;

public class StudentCourses extends HorizontalLayout implements View {
    public StudentCourses(SessionAuthentication authentication) throws SQLException, AppLogicException {
        CourseInstance tempObject;

        //Navigator navigator = getUI().getNavigator();
        addComponent(new Label("Omat kurssit näkymä"));
        Grid<CourseInstance> courseGrid = new Grid<>();
        Student opt = authentication.getStudent().get();
        List<CourseInstance> courses = opt.attending(authentication.getConnection());
        courseGrid.setItems(courses);
        /*courseGrid.addColumn( course -> (course.wholeNameId(40); tempObject = course), new ButtonRenderer(clickevent -> {
            navigator.navigateTo("CourseView", navigator.addView(new CourseView(authentication.getConnection(), ));
        })).setCaption("Kurssin nimi");*/
        courseGrid.addColumn(courseInstance -> {
            //courseInstance.wholeNameId(40);
            return courseInstance.wholeNameId(40);
        }).setCaption("Kurssin nimi");
        courseGrid.addColumn(course -> "Näytä kurssin tiedot",
                new ButtonRenderer(clickevent -> {
                    try {
                        getUI().getNavigator().addView("CourseView" ,new CourseView(authentication, (CourseInstance) clickevent.getItem()));
                        getUI().getNavigator().navigateTo("CourseView");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (AppLogicException e) {
                        e.printStackTrace();
                    }
                }));
        //courseGrid.setWidth("100%");
        //courseGrid.setHeight("100%");
        addComponent(courseGrid);
    }
}