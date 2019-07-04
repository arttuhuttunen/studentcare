package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.db.orm.Course;
import org.utu.studentcare.db.orm.CourseInstance;
import org.utu.studentcare.db.orm.Student;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JoinCourses extends VerticalLayout implements View {
    Grid<CourseInstance> courseGrid;
    List<CourseInstance> courses;
    SessionAuthentication authentication;
    public JoinCourses(SessionAuthentication authentication){
        addComponent(new Label("Kurssi-ilmo näkymä"));
        courseGrid = new Grid<>();
        courseGrid.setWidth("800");
        this.authentication = authentication;
        addComponents(courseGrid);
    }

    public void enter(ViewChangeListener.ViewChangeEvent event) {
        try {
            loadColumns();
        } catch (SQLException | AppLogicException e) {
            e.printStackTrace();
        }
    }

    private void loadColumns() throws SQLException, AppLogicException {
        courseGrid.removeAllColumns();
        Student opt = authentication.getStudent().get();
        courses = opt.notAttending(authentication.getConnection());
        courseGrid.setItems(courses);
        courseGrid.addColumn(courseInstance -> courseInstance.wholeNameId(40)).setCaption("Kurssin nimi").setId("courseName");
        courseGrid.sort("courseName" ,SortDirection.ASCENDING);
        courseGrid.addColumn(join -> "Liity kurssille",
                new ButtonRenderer<>(clickevent -> {
                    try {
                        if (opt.joinCourse(authentication.getConnection(), CourseInstance.findI(authentication.getConnection(), clickevent.getItem().instanceId).get())) {
                            courseGrid.removeAllColumns();
                            loadColumns(); //This itself call is for reloading all rows after successful course joining
                            Notification.show("Liitytty kurssille " + clickevent.getItem().wholeNameId(10) + " onnistuneesti!").setDelayMsec(3000);
                        }
                        else {
                            Notification.show("VIRHE: Liittyminen epäonnistui, yritä myöhemmin uudestaan", Notification.Type.WARNING_MESSAGE);
                        }

                    } catch (SQLException | AppLogicException e) {
                        e.printStackTrace();
                    }
                })

        );
    }
}
