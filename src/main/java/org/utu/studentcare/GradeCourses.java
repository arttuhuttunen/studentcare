package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ButtonRenderer;
import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.db.orm.CourseInstance;
import org.utu.studentcare.db.orm.Student;

import java.sql.SQLException;
import java.util.List;

public class GradeCourses extends VerticalLayout implements View {
    Grid<CourseInstance> courseGrid = new Grid<>();
    List<CourseInstance> courses;
    SessionAuthentication authentication;
    public GradeCourses(SessionAuthentication authentication) {
        addComponent(new Label("Arvostele kursseja näkymä"));
        this.authentication = authentication;
        courseGrid.setWidth("1000");
        addComponent(courseGrid);
    }
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        try {
            loadColumns();
        } catch (AppLogicException | SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadColumns() throws AppLogicException, SQLException {
        courseGrid.removeAllColumns();
        Student opt = authentication.getStudent().get();
        courses = opt.asTeacher().teaching(authentication.getConnection());
        courseGrid.setItems(courses);
        courseGrid.addColumn(courseInstance -> courseInstance.wholeNameId(40)).setCaption("Kurssin nimi").setId("courseName");
        courseGrid.sort("courseName", SortDirection.ASCENDING);
        courseGrid.addColumn(grade -> "Siirry arvostelemaan kurssisuorituksia",
                new ButtonRenderer<>(clickevent -> {
                    getUI().getNavigator().addView("GradeCourse", new GradeCourse(authentication, clickevent.getItem()));
                    getUI().getNavigator().navigateTo("GradeCourse");
                }));
    }
}
