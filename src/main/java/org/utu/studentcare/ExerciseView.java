package org.utu.studentcare;

import com.vaadin.navigator.View;
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
    public ExerciseView(SessionAuthentication authentication, ExerciseSpec exerciseSpec, CourseInstance courseInstance) throws SQLException, AppLogicException {
        int studentID = authentication.getStudent().get().id;
        Exercise exercise = new Exercise(studentID, courseInstance.instanceId, exerciseSpec.getId(), exerciseSpec);
        Optional<Exercise> searchParams = Exercise.find(authentication.getConnection(), studentID, courseInstance.instanceId, exerciseSpec.getId());

        addComponent(new Label("Harjoituksen " + exerciseSpec.getDescription() + " palautuslomake"));
        TextArea exerciseAnswer = new TextArea("Vastaus tehtävään");
        TextArea exerciseComment = new TextArea("Vapaavalintainen kommentti");
        Button submitButton = new Button("Palauta tehtävä");
        Button cancelButton = new Button("Peruuta");


        /*This if-statement locks exercise to accept only one submission by making TextArea uneditable and disabling submit-button
        * That results in grey textfield and submit-button, which hopefully gives user a feeling of submit immutability*/
        if (searchParams.isPresent()) {
            addComponent(new Label("Tehtävä palautettu " + searchParams.get().uploadDate));
            if (!searchParams.get().gradeDate.isEmpty()) {
                addComponent(new Label("Arvosteltu " + searchParams.get().gradeDate + ", Arvosana: " + searchParams.get().grade));
            }
            if (!searchParams.get().teacherComment.isEmpty()) {
                addComponent(new Label("Opettajan kommentti: " + searchParams.get().teacherComment));
            }
            exerciseAnswer.setValue(searchParams.get().uploadResource);
            exerciseComment.setValue(searchParams.get().comment);
            submitButton.setEnabled(false);
            exerciseAnswer.setEnabled(false);
            exerciseComment.setEnabled(false);
        }

        submitButton.addClickListener(clickEvent ->
                {
                    try {
                        exercise.upload(authentication.getConnection(), exerciseAnswer.getValue(), exerciseComment.getValue());
                        Notification successNotification = new Notification("Tehtävä palautettu onnistuneesti");
                        successNotification.setDelayMsec(5000);
                        successNotification.show(Page.getCurrent());
                        getUI().getNavigator().navigateTo("CourseView");
                    } catch (SQLException | AppLogicException e) {
                        e.printStackTrace();
                    }
                }
        );
        //Exercise exercise = Exercise.find(authentication.getConnection(), studentID, courseInstance.instanceId, exerciseSpec.getId()).orElseThrow(() -> new AppLogicException("Harjoitusta ei löytynyt"));

        cancelButton.addClickListener(clickEvent ->
                getUI().getNavigator().navigateTo("CourseView"));
        addComponents(exerciseAnswer,exerciseComment, submitButton, cancelButton);
    }
}
