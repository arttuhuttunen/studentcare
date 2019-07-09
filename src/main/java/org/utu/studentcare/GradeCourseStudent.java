package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.applogic.ExerciseSpec;
import org.utu.studentcare.applogic.Session;
import org.utu.studentcare.applogic.ValRange;
import org.utu.studentcare.db.orm.*;
import scala.App;

import java.sql.SQLException;
import java.util.Optional;

//This view shows specific students ALL exercises of specific course in one page
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
        Button cancelBtn = new Button("Peruuta");
        cancelBtn.addClickListener(clickEvent ->
                getUI().getNavigator().navigateTo("GradeCourse"));
        addComponent(cancelBtn);

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
        Optional<CourseGrade> cgOpt = CourseGrade.find(authentication.getConnection(), student.id, courseInstance.instanceId); //Shortens method parameters

        Optional<ValRange> gradeByExercises = student.exercises(authentication.getConnection(), courseInstance.instanceId).grade();

        //This method restricts the ability to grade course only when course isn't already graded or all exercises are graded
        if (!cgOpt.isPresent() && gradeByExercises.get().min == gradeByExercises.get().max) {
            exerciseGrid.addComponent(new Label("Tehtävien pisteiden mukaan laskettu kurssiarvosana on: " + gradeByExercises.get().min));
            exerciseGrid.addComponent(new Button("Hyväksy kurssiarvosana", click -> {
                try {
                    if (courseInstance.grade(authentication.getConnection(), student, (int) gradeByExercises.get().min, authentication.getStudent().get().asTeacher())) {
                        Notification.show("Kurssi arvioitu onnistuneesti!").setDelayMsec(5000);
                        loadExercises();
                    } else {Notification.show("VIRHE: Kurssin arviointi epäonnistui, yritä myöhemmin uudestaan", Notification.Type.ERROR_MESSAGE).setDelayMsec(5000);}
                } catch (SQLException | AppLogicException e) {
                    e.printStackTrace();
                    Notification.show("Tapahtui odottamaton virhe, yritä myöhemmin uudestaan", Notification.Type.ERROR_MESSAGE).setDelayMsec(5000);
                }
            }));
        } else {
            if (!cgOpt.isPresent()) {
                exerciseGrid.addComponent(new Label("Kurssiarvosanaa ei voi määrittää vielä, koska kaikkia tehtäviä ei ole vielä arvosteltu ja/tai palautettu"));
            }
        }

        //If course is graded -> show info about grade and grading acceptation status
        if (cgOpt.isPresent()) {
            exerciseGrid.addComponent(new Label("Kurssi on arvioitu " + cgOpt.get().gradeDate +  " arvosanalla " + cgOpt.get().grade));
            if (cgOpt.get().adminDate.isEmpty()) {
                exerciseGrid.addComponent(new Label("Arviointia ei ole vielä lisätty opintorekisteriin"));
            } else {
                exerciseGrid.addComponent(new Label("Arviointi on lisätty opintorekisteriin " + cgOpt.get().gradeDate));
            }
        }

        //Loads selected student's all exercises
        for (ExerciseSpec exerciseSpec : courseInstance.exerciseSpecs().getExerciseDecls()){
            exerciseGrid.addComponent(new Label(exerciseSpec.getDescription()));

            if (Exercise.find(authentication.getConnection(), student.id, courseInstance.instanceId, exerciseSpec.getId()).isPresent()) {
                Exercise exercise = Exercise.find(authentication.getConnection(), student.id, courseInstance.instanceId, exerciseSpec.getId()).get();

                //Some values needs to be searched directly from the db, so searchParams shortens method parameters for better code readability
                Optional<Exercise> searchParams = Exercise.find(authentication.getConnection(), student.id, courseInstance.instanceId, exerciseSpec.getId());

                exerciseGrid.addComponent(new Label("Tehtävä tallennettu " + searchParams.get().uploadDate));
                TextArea studentAnswer = new TextArea("Vastaus tehtävään", searchParams.get().uploadResource);
                TextArea studentComment = new TextArea("Opiskelijan vapaavalintainen kommentti", searchParams.get().comment);
                studentAnswer.setReadOnly(true);
                studentComment.setReadOnly(true);
                TextArea teacherComment = new TextArea("Opettajan kommentti");
                TextField gradeField = new TextField("Arvioi suoritus (pistejakauma välillä " + exerciseSpec.getRange() + ")");
                Button gradeBtn = new Button("Arvostele");
                Button cancelBtn = new Button("Peruuta");

                //If exercise is already graded -> load grading data from db, and disable gradingfields and upload buttons
                if (searchParams.get().graded()) {
                    exerciseGrid.addComponent(new Label("Tehtävä arvioitu " + searchParams.get().gradeDate));
                    teacherComment.setValue(searchParams.get().teacherComment);
                    gradeField.setValue(Double.toString(searchParams.get().grade));
                    gradeField.setEnabled(false);
                    teacherComment.setEnabled(false);
                    gradeBtn.setEnabled(false);
                }
                gradeBtn.addClickListener(click -> {
                    try {
                        if (exerciseSpec.possibleValues().contains(Double.parseDouble(gradeField.getValue()))) {
                            exercise.grade(authentication.getConnection(), authentication.getStudent().get().id, Double.parseDouble(gradeField.getValue()), teacherComment.getValue());

                            loadExercises();
                            Notification.show("Tehtävä " + exerciseSpec.getDescription() + " arvioitu onnistuneesti!").setDelayMsec(5000);
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
                exerciseGrid.addComponents(studentAnswer, studentComment, gradeField, teacherComment, gradeBtn, cancelBtn);
            } else {
                exerciseGrid.addComponent(new Label("Opiskelija ei ole vielä vastannut tehtävään"));
            }
            exerciseGrid.addComponent(new Label("")); //Workaround for adding space between exercise components without using CSS

        }
    }
}
