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
            Notification.show("VIRHE: Opiskelijan vastausten näyttäminen epäonnistui, yritä myöhemmin uudestaan", Notification.Type.WARNING_MESSAGE).setDelayMsec(5000);
        }
    }

    private void loadExercises() throws AppLogicException, SQLException {
        exerciseGrid.removeAllComponents();
        for (ExerciseSpec exerciseSpec : courseInstance.exerciseSpecs().getExerciseDecls()){
            exerciseGrid.addComponent(new Label(exerciseSpec.getDescription()));

            if (Exercise.find(authentication.getConnection(), student.id, courseInstance.instanceId, exerciseSpec.getId()).isPresent()) {
                Exercise exercise = Exercise.find(authentication.getConnection(), student.id, courseInstance.instanceId, exerciseSpec.getId()).get();

                //Some values needs to be searched directly from the db, so searchParams shortens parameters for better code readability
                Optional<Exercise> searchParams = Exercise.find(authentication.getConnection(), student.id, courseInstance.instanceId, exerciseSpec.getId());

                exerciseGrid.addComponent(new Label("Tehtävä tallennettu " + searchParams.get().uploadDate));
                exerciseGrid.addComponent(new TextArea("Vastaus tehtävään", searchParams.get().uploadResource));
                exerciseGrid.addComponent(new TextArea("Opiskelijan vapaavalintainen kommentti", searchParams.get().comment));
                TextArea teacherComment = new TextArea("Opettajan kommentti");
                TextField gradeField = new TextField("Arvioi suoritus (pistejakauma välillä " + exerciseSpec.getRange() + ")");
                Button gradeBtn = new Button("Arvostele");
                Button cancelBtn = new Button("Peruuta");
                if (searchParams.get().graded()) {
                    exerciseGrid.addComponent(new Label("Tehtävä arvioitu " + searchParams.get().gradeDate));
                    teacherComment.setValue(searchParams.get().teacherComment);
                    gradeField.setValue(Double.toString(searchParams.get().grade));
                    gradeBtn.setEnabled(false);
                }
                gradeBtn.addClickListener(click -> {
                    try {
                        if (exerciseSpec.possibleValues().contains(Double.parseDouble(gradeField.getValue()))) {
                            exercise.grade(authentication.getConnection(), authentication.getStudent().get().id, Double.parseDouble(gradeField.getValue()), teacherComment.getValue());

                            loadExercises();
                            Notification.show("Tehtävä " + exerciseSpec.getDescription() + " arvioitu onnistuneesti!");
                        } else {
                            Notification.show("Antamasi pistemäärä (" + gradeField.getValue() + ") ei ole tehtävän pistealueella (" + exerciseSpec.possibleValues() + "). Yritä uudestaan", Notification.Type.ERROR_MESSAGE).setDelayMsec(5000);
                        }
                    } catch (NumberFormatException n) {
                        Notification.show("Syötetty pistemäärä ei ole kokonais- tai desimaaliluku, yritä uudelleen", Notification.Type.WARNING_MESSAGE).setDelayMsec(5000);
                    } catch (AppLogicException | SQLException e)  {
                        e.printStackTrace();
                        Notification.show("Tapahtui odottamaton virhe, yritä myöhemmin uudestaan", Notification.Type.ERROR_MESSAGE).setDelayMsec(5000);
                    }
                });
                cancelBtn.addClickListener(click -> {
                    gradeField.clear();
                    teacherComment.clear();
                });
                exerciseGrid.addComponents(gradeField, teacherComment, gradeBtn, cancelBtn);
            } else {
                exerciseGrid.addComponent(new Label("Opiskelija ei ole vielä vastannut tehtävään"));
            }
            exerciseGrid.addComponent(new Label("")); //Workaround for adding space between exercise components without using CSS

        }
    }
}
