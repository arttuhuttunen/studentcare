package org.utu.studentcare;

import com.vaadin.data.Binder;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.applogic.ExerciseSpec;
import org.utu.studentcare.applogic.ExerciseSpecs;
import org.utu.studentcare.db.orm.*;
import scala.App;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CourseView extends VerticalLayout implements View {
    SessionAuthentication authentication;
    CourseInstance courseInstance;
    Grid<ExerciseSpec> exercisesGrid;


    public CourseView(SessionAuthentication authentication, CourseInstance courseInstance) throws AppLogicException {
        this.authentication = authentication;
        this.courseInstance = courseInstance;
        addComponent(new Label("Kurssin " + courseInstance.wholeNameId(40) + " näkymä"));
        exercisesGrid = new Grid<>();

    }
    public void enter(ViewChangeListener.ViewChangeEvent event) {

        List<ExerciseSpec> exercises = null;
        try {
            exercises = courseInstance.exerciseSpecs().getExerciseDecls();
        } catch (AppLogicException e) {
            e.printStackTrace();
        }
        exercisesGrid.removeAllColumns();
        try {
            if (CourseGrade.find(authentication.getConnection(), authentication.getStudent().get().id, courseInstance.instanceId).isPresent()) {
                Optional<CourseGrade> cgOpt = CourseGrade.find(authentication.getConnection(), authentication.getStudent().get().id, courseInstance.instanceId); //Shortens method parameters
                if (cgOpt.get().graded()) {
                    addComponent(new Label("Kurssi on arvioitu " + cgOpt.get().gradeDate + " arvosanalla " + cgOpt.get().grade));
                    if (cgOpt.get().adminDate.isEmpty()) {
                        addComponent(new Label("Arviointia ei ole vielä lisätty opintorekisteriin"));
                    } else {
                        addComponent(new Label("Arviointi on lisätty opintorekisteriin " + cgOpt.get().adminDate));
                    }
                }
            }
        } catch (SQLException | AppLogicException e) {
            e.printStackTrace();
        }
        addComponent(new Label(""));
        exercisesGrid.setItems(exercises);
        exercisesGrid.addColumn(ExerciseSpec::getDescription).setCaption("Harjoituksen nimi");
        exercisesGrid.addColumn(exercise -> "Siirry harjoitukseen",
                new ButtonRenderer(clickevent -> {
                    try {
                        getUI().getNavigator().addView("ExerciseView", new ExerciseView(authentication, (ExerciseSpec)clickevent.getItem(), courseInstance));
                    } catch (SQLException | AppLogicException e) {
                        e.printStackTrace();
                    }
                    getUI().getNavigator().navigateTo("ExerciseView");
                })
        );
        addComponent(exercisesGrid);
    }
}