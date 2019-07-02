package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.applogic.ExerciseSpec;
import org.utu.studentcare.applogic.Session;
import org.utu.studentcare.db.orm.CourseInstance;
import org.utu.studentcare.db.orm.Exercise;
import org.utu.studentcare.db.orm.Exercises;
import org.utu.studentcare.db.orm.Student;

import java.sql.SQLException;
import java.util.Optional;

public class GradeCourseStudent extends VerticalLayout implements View {
    SessionAuthentication authentication;
    Student student;
    CourseInstance courseInstance;
    VerticalLayout exerciseGrid;

    public GradeCourseStudent(SessionAuthentication authentication, Student student, CourseInstance courseInstance) {
        this.authentication = authentication;
        this.student = student;
        this.courseInstance = courseInstance;
        exerciseGrid = new VerticalLayout();
        addComponent(new Label("Kurssin " + courseInstance.wholeNameId(40) + " arvostelu"));
        addComponent(new Label("Opiskelija: " + student.wholeName()));
        addComponent(exerciseGrid);
    }

    public void enter(ViewChangeListener.ViewChangeEvent event) {
        try {
            loadExercises();
        } catch (AppLogicException | SQLException e) {
            e.printStackTrace();
            Notification.show("VIRHE: Opiskelijan vastausten näyttäminen epäonnistui, yritä myöhemmin uudestaan", Notification.Type.ERROR_MESSAGE).setDelayMsec(5000);
        }
    }

    private void loadExercises() throws AppLogicException, SQLException {
        exerciseGrid.removeAllComponents();
        for (ExerciseSpec exerciseSpec : courseInstance.exerciseSpecs().getExerciseDecls()){
            exerciseGrid.addComponent(new Label(exerciseSpec.getDescription()));

            if (Exercise.find(authentication.getConnection(), student.id, courseInstance.instanceId, exerciseSpec.getId()).isPresent()) {
                exerciseGrid.addComponent(new TextArea("Vastaus tehtävään", Exercise.find(authentication.getConnection(), student.id, courseInstance.instanceId, exerciseSpec.getId()).get().uploadResource));
                exerciseGrid.addComponent(new TextArea("Opiskelijan vapaavalintainen kommentti", Exercise.find(authentication.getConnection(), student.id, courseInstance.instanceId, exerciseSpec.getId()).get().comment));
            } else {
                exerciseGrid.addComponent(new Label("Opiskelija ei ole vielä vastannut tehtävään"));
            }
            exerciseGrid.addComponent(new Label(""));

        }
    }
}
