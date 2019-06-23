package org.utu.studentcare;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
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
    SessionAuthentication authentication;
    Grid<CourseInstance> courseGrid;

    public StudentCourses(SessionAuthentication authentication) throws SQLException, AppLogicException {
        this.authentication = authentication;
        courseGrid = new Grid<>();
        addComponent(new Label("Omat kurssit näkymä"));

        Student opt = authentication.getStudent().get(); //Student opt is for shortening Optional parameters for better readability
        courseGrid.setWidthUndefined();
        courseGrid.setHeightUndefined();
        addComponent(courseGrid);
    }

    public void enter(ViewChangeListener.ViewChangeEvent event) {
        courseGrid.removeAllColumns(); //Removing all columns before loading them again
        List<CourseInstance> courses = null;
        Student opt = authentication.getStudent().get(); //Student opt is for shortening Optional parameters for better readability

        try {
            courses = opt.attending(authentication.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (AppLogicException e) {
            e.printStackTrace();
        }
        courseGrid.setItems(courses);
        /*courseGrid.addColumn( course -> (course.wholeNameId(40); tempObject = course), new ButtonRenderer(clickevent -> {
            navigator.navigateTo("CourseView", navigator.addView(new CourseView(authentication.getConnection(), ));
        })).setCaption("Kurssin nimi");*/
        courseGrid.addColumn(courseInstance -> courseInstance.wholeNameId(40)).setCaption("Kurssin nimi");
        courseGrid.addColumn(status -> {
            try {
                return opt.exercises(authentication.getConnection(), status.instanceId).status();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (AppLogicException e) {
                e.printStackTrace();
            }
            return new AppLogicException("Virhe taulukon luomisessa");
        }).setCaption("Suorituksen tilanne");
        courseGrid.addColumn(course -> "Näytä kurssin tiedot",
                new ButtonRenderer(clickevent -> {
                    try {
                        getUI().getNavigator().addView("CourseView" ,new CourseView(authentication, (CourseInstance) clickevent.getItem()));
                    } catch (AppLogicException e) {
                        e.printStackTrace();
                    }
                    getUI().getNavigator().navigateTo("CourseView");
                }));
        //courseGrid.setHeightByRows(courses.size());
        courseGrid.setWidth("1000");
    }
}