package org.utu.studentcare;

import com.vaadin.data.Binder;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.applogic.ExerciseSpec;
import org.utu.studentcare.db.orm.CourseInstance;
import org.utu.studentcare.db.orm.Exercise;
import org.utu.studentcare.db.orm.Exercises;
import org.utu.studentcare.db.orm.Student;

import java.sql.SQLException;
import java.util.List;

public class CourseView extends HorizontalLayout implements View {
    public CourseView(SessionAuthentication authentication, CourseInstance courseInstance) throws SQLException, AppLogicException {
        addComponent(new Label("Kurssin " + courseInstance.wholeNameId(40) + " näkymä"));
        Grid<ExerciseSpec> exercisesGrid = new Grid<>();
        Student opt = authentication.getStudent().get();
        List<ExerciseSpec> exercises = courseInstance.exerciseSpecs().getExerciseDecls();
        exercisesGrid.setItems(exercises);
        exercisesGrid.addColumn(ExerciseSpec::getDescription).setCaption("Harjoituksen nimi");
        exercisesGrid.addColumn(ExerciseSpec::getRange).setCaption("Pistemäärä");
        exercisesGrid.addColumn(exercise -> "Siirry harjoitukseen",
            new ButtonRenderer(clickevent -> {
                try {
                    getUI().getNavigator().addView("ExerciseView", new ExerciseView(authentication, (ExerciseSpec)clickevent.getItem(), courseInstance));
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (AppLogicException e) {
                    e.printStackTrace();
                }
                getUI().getNavigator().navigateTo("ExerciseView");
            })
        );
        addComponent(exercisesGrid);
    }
}