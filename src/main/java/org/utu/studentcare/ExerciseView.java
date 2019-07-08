package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.applogic.ExerciseSpec;
import org.utu.studentcare.applogic.Session;
import org.utu.studentcare.db.orm.CourseInstance;
import org.utu.studentcare.db.orm.Exercise;

import java.sql.SQLException;
import java.util.Optional;

public class ExerciseView extends VerticalLayout implements View {
    SessionAuthentication authentication;
    ExerciseSpec exerciseSpec;
    CourseInstance courseInstance;
    VerticalLayout exerciseLayout;

    public ExerciseView(SessionAuthentication authentication, ExerciseSpec exerciseSpec, CourseInstance courseInstance){
        this.authentication = authentication;
        this.exerciseSpec = exerciseSpec;
        this.courseInstance = courseInstance;
        exerciseLayout = new VerticalLayout();
        addComponent(exerciseLayout);
    }
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        try {
            loadExercise();
        } catch (AppLogicException | SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadExercise() throws AppLogicException, SQLException {
            exerciseLayout.removeAllComponents();
            int studentID = authentication.getStudent().get().id;
            Exercise exercise = new Exercise(studentID, courseInstance.instanceId, exerciseSpec.getId(), exerciseSpec);
            Optional<Exercise> searchParams = Exercise.find(authentication.getConnection(), studentID, courseInstance.instanceId, exerciseSpec.getId());

            exerciseLayout.addComponent(new Label("Kurssi: " + courseInstance.wholeNameId(40)));
            exerciseLayout.addComponent(new Label("Harjoituksen " + exerciseSpec.getDescription() + " palautuslomake"));
            TextArea exerciseAnswer = new TextArea("Vastaus tehtävään");
            TextArea exerciseComment = new TextArea("Vapaavalintainen kommentti");
            Button submitButton = new Button("Palauta tehtävä");
            Button cancelButton = new Button("Peruuta");


            /*This if-statement locks exercise after graded by making TextArea uneditable and disabling submit-button
             * That results in grey textfield and submit-button, which (hopefully) gives user a feeling of submit immutability*/
            if (searchParams.isPresent()) {
                exerciseLayout.addComponent(new Label("Tehtävä palautettu " + searchParams.get().uploadDate));
                if (!searchParams.get().gradeDate.isEmpty()) {
                    exerciseLayout.addComponent(new Label("Arvosteltu " + searchParams.get().gradeDate + ", Arvosana: " + searchParams.get().grade));
                    exerciseLayout.addComponent(new Label("Opettajan kommentti: " + searchParams.get().teacherComment));
                    submitButton.setEnabled(false);
                    exerciseAnswer.setEnabled(false);
                    exerciseComment.setEnabled(false);
                }

                exerciseAnswer.setValue(searchParams.get().uploadResource);
                exerciseComment.setValue(searchParams.get().comment);
            }

            submitButton.addClickListener(clickEvent ->
                    {
                        try {
                            exercise.upload(authentication.getConnection(), exerciseAnswer.getValue(), exerciseComment.getValue());
                            Notification successNotification = new Notification("Tehtävä palautettu onnistuneesti");
                            successNotification.setDelayMsec(5000);
                            successNotification.show(Page.getCurrent());
                            loadExercise();
                        } catch (SQLException | AppLogicException e) {
                            e.printStackTrace();
                        }
                    }
            );
            //Exercise exercise = Exercise.find(authentication.getConnection(), studentID, courseInstance.instanceId, exerciseSpec.getId()).orElseThrow(() -> new AppLogicException("Harjoitusta ei löytynyt"));

            cancelButton.addClickListener(clickEvent ->
                    getUI().getNavigator().navigateTo("CourseView"));
            exerciseLayout.addComponents(exerciseAnswer,exerciseComment, submitButton, cancelButton);
    }
}
