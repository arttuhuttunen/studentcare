package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
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
        exercisesGrid.addColumn(ExerciseSpec::getDescription).setCaption("Harjoitukset");
        addComponent(exercisesGrid);
    }
}