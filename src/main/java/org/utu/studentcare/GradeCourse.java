package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.db.orm.CourseInstance;
import org.utu.studentcare.db.orm.Student;

import java.sql.SQLException;

//This view shows participating students of specific course
public class GradeCourse extends VerticalLayout implements View {
    Grid<Student> studentGrid = new Grid<>();
    SessionAuthentication authentication;
    CourseInstance courseInstance;
    public GradeCourse(SessionAuthentication authentication, CourseInstance courseInstance) {
        this.authentication = authentication;
        this.courseInstance = courseInstance;
        addComponent(new Label("Kurssin " + courseInstance.name + " arvostelu"));
        addComponent(studentGrid);
        studentGrid.setWidth("1000");
        Button cancelBtn = new Button("Peruuta");
        cancelBtn.addClickListener(clickEvent ->
                getUI().getNavigator().navigateTo("GradeCourses"));
        addComponent(cancelBtn);
    }
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        try {
            loadColumns();
        } catch (SQLException | AppLogicException e) {
            e.printStackTrace();
        }
    }

    private void loadColumns() throws SQLException, AppLogicException {
        studentGrid.removeAllColumns();
        studentGrid.setItems(courseInstance.students(authentication.getConnection()));
        studentGrid.addColumn(studentName -> studentName.firstNames).setCaption("Etunimet").setId("firstNames");
        studentGrid.addColumn(studentName -> studentName.familyName).setCaption("Sukunimi");
        studentGrid.addColumn(studentName -> studentName.id).setCaption("Opiskelijanumero");
        studentGrid.addColumn(submittedExercises  -> {
            try {
                return submittedExercises.exercises(authentication.getConnection(), courseInstance.instanceId).status();
            } catch (SQLException | AppLogicException e) {
                e.printStackTrace();
            }
            return Notification.show("VIRHE: Opiskelijataulukon luonti epäonnistui, yritä myöhemmin uudestaan", Notification.Type.WARNING_MESSAGE);
        }).setCaption("Tehtävien tilanne");
        studentGrid.addColumn(student -> "Opiskelijan tehtävät",
            new ButtonRenderer(clickevent -> {
                getUI().getNavigator().addView("GradeCourseStudent", new GradeCourseStudent(authentication, (Student)clickevent.getItem(), courseInstance));
                getUI().getNavigator().navigateTo("GradeCourseStudent");
            })
        );
        studentGrid.sort("firstNames", SortDirection.ASCENDING);
    }
}
