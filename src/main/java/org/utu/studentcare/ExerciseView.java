package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.applogic.ExerciseSpec;
import org.utu.studentcare.applogic.Session;
import org.utu.studentcare.db.orm.CourseInstance;
import org.utu.studentcare.db.orm.Exercise;

import java.sql.SQLException;
import java.util.Optional;

public class ExerciseView extends HorizontalLayout implements View {
    public ExerciseView(SessionAuthentication authentication, ExerciseSpec exerciseSpec, CourseInstance courseInstance) throws SQLException, AppLogicException {
        addComponent(new Label("Harjoituksen " + exerciseSpec.getDescription() + " palautuslomake"));
        TextArea exerciseAnswer = new TextArea("Vastaua tehtävään");
        TextArea exerciseComment = new TextArea("Vapaavalintainen kommentti");
        Button submitButton = new Button("Palauta tehtävä");
        Button cancelButton = new Button("Peruuta");

        int studentID = authentication.getStudent().get().id;
        Exercise exercise = Exercise.find(authentication.getConnection(), studentID, courseInstance.instanceId, exerciseSpec.getId()).orElseThrow(() -> new AppLogicException("Harjoitusta ei löytynyt"));
        submitButton.addClickListener(clickEvent ->
                {
                    try {
                        exercise.upload(authentication.getConnection(), exerciseAnswer.getValue(), exerciseComment.getValue());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (AppLogicException e) {
                        e.printStackTrace();
                    }
                }
        );

        cancelButton.addClickListener(clickEvent ->
                getUI().getNavigator().navigateTo("CourseView"));
    }
}
