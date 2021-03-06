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
import java.util.List;

//This view shows courses, which user isn't teaching yet
public class TeachCourses extends VerticalLayout implements View {
    Grid<CourseInstance> courseGrid = new Grid<>();
    List<CourseInstance> courses;
    SessionAuthentication authentication;
    public TeachCourses(SessionAuthentication authentication){
        addComponent(new Label("Opetusnäkymä"));
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
        courses = opt.asTeacher().notTeaching(authentication.getConnection());
        courseGrid.setItems(courses);
        courseGrid.addColumn(courseInstance -> courseInstance.wholeNameId(40)).setCaption("Kurssin nimi").setId("courseName");
        courseGrid.sort("courseName", SortDirection.ASCENDING);
        courseGrid.addColumn(teach -> "Ala opettaa kurssia",
                new ButtonRenderer<>(clickevent -> {
                    try {
                        if (opt.asTeacher().teachCourse(authentication.getConnection(), CourseInstance.findI(authentication.getConnection(), clickevent.getItem().instanceId).get())) { //Tries to update db about new course teacher, otherwise show error
                            loadColumns();
                            Notification.show("Opetat nyt kurssia " + clickevent.getItem().wholeNameId(40)).setDelayMsec(3000);
                        } else {
                            Notification.show("VIRHE: Opettajaksi liittyminen epäonnistui, yritä myöhemmin uudestaan", Notification.Type.WARNING_MESSAGE);
                        }
                    } catch (SQLException | AppLogicException e) {
                        e.printStackTrace();
                    }
                }));
    }
}
