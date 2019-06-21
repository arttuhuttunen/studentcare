package org.utu.studentcare;

import com.vaadin.navigator.View;
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

public class JoinCourses extends HorizontalLayout implements View {
    public JoinCourses(SessionAuthentication authentication) throws SQLException, AppLogicException {
        addComponent(new Label("Kurssi-ilmo näkymä"));
        Student opt = authentication.getStudent().get();
        Grid<CourseInstance> courseGrid = new Grid<>();
        List<CourseInstance> courses = opt.notAttending(authentication.getConnection());
        courseGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        courseGrid.setItems(courses);
        courseGrid.addColumn(courseInstance -> courseInstance.wholeNameId(40)).setCaption("Kurssin nimi").setId("courseName");
        courseGrid.sort("courseName" ,SortDirection.ASCENDING);
        courseGrid.addColumn(join -> "Liity kurssille",
                new ButtonRenderer<>(clickevent -> {
                    try {
                        if (!opt.joinCourse(authentication.getConnection(), CourseInstance.findI(authentication.getConnection(), clickevent.getItem().instanceId).get())) {
                            Notification.show("VIRHE: Liittyminen epäonnistui, yritä myöhemmin uudestaan", Notification.Type.WARNING_MESSAGE);
                        }
                        courseGrid.getDataProvider().refreshItem(clickevent.getItem());

                        Notification.show("Liitytty kurssille " + clickevent.getItem().wholeNameId(10) + " onnistuneesti!");
                    } catch (SQLException | AppLogicException e) {
                        e.printStackTrace();
                    }
                })

        );
        addComponents(courseGrid);
        /*Button joinBtn = new Button("Liity valituille kursseille");
        courseGrid.addSelectionListener(event -> {
            Set<CourseInstance> courseSet = event.getAllSelectedItems();
            List<CourseInstance> selectedCourses = new ArrayList<>(courseSet);
            joinBtn.addClickListener(clickEvent -> {
                new Thread(() -> {
                    try {
                        for (CourseInstance selectedCourse : selectedCourses) {
                            while (!opt.joinCourse(authentication.getConnection(), selectedCourse)) {
                                System.out.println("Waiting for db to finish...");
                                Thread.currentThread().wait(1000);
                            }
                            courseGrid.getDataProvider().refreshAll();
                        }
                    } catch (SQLException | InterruptedException e) {
                        e.printStackTrace();
                    } catch (AppLogicException e) {
                        e.printStackTrace();
                    }
                });
            });
        });
        addComponents(courseGrid, joinBtn);*/
    }
}
