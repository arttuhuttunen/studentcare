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

//This view shows chosen courses exercises
public class CourseView extends VerticalLayout implements View {
    SessionAuthentication authentication;
    CourseInstance courseInstance;
    Grid<ExerciseSpec> exercisesGrid;
    VerticalLayout exerciseLayout;

    public CourseView(SessionAuthentication authentication, CourseInstance courseInstance) {
        this.authentication = authentication;
        this.courseInstance = courseInstance;
        addComponent(new Label("Kurssin " + courseInstance.wholeNameId(40) + " tehtävät"));
        exercisesGrid = new Grid<>();
        exerciseLayout = new VerticalLayout();
        addComponent(exerciseLayout);
    }
    public void enter(ViewChangeListener.ViewChangeEvent event) {

        exerciseLayout.removeAllComponents();
        List<ExerciseSpec> exercises = null;
        try {
            exercises = courseInstance.exerciseSpecs().getExerciseDecls();
        } catch (AppLogicException e) {
            e.printStackTrace();
        }
        exercisesGrid.removeAllColumns();
        try {
            if (CourseGrade.find(authentication.getConnection(), authentication.getStudent().get().id, courseInstance.instanceId).isPresent()) { //.isPresent() checks whether Course has been graded in db
                Optional<CourseGrade> cgOpt = CourseGrade.find(authentication.getConnection(), authentication.getStudent().get().id, courseInstance.instanceId); //Shortens method parameters for better code readability
                if (cgOpt.get().graded()) {
                    exerciseLayout.addComponent(new Label("Kurssi on arvioitu " + cgOpt.get().gradeDate + " arvosanalla " + cgOpt.get().grade));
                    if (cgOpt.get().adminDate.isEmpty()) {
                        exerciseLayout.addComponent(new Label("Arviointia ei ole vielä lisätty opintorekisteriin"));
                    } else {
                        exerciseLayout.addComponent(new Label("Arviointi on lisätty opintorekisteriin " + cgOpt.get().adminDate));
                    }
                }
            }
        } catch (SQLException | AppLogicException e) {
            e.printStackTrace();
        }
        exerciseLayout.addComponent(new Label(""));
        exercisesGrid.setItems(exercises);
        exercisesGrid.addColumn(ExerciseSpec::getDescription).setCaption("Harjoituksen nimi");
        exercisesGrid.addColumn(exercise -> "Siirry harjoitukseen",
                new ButtonRenderer(clickevent -> {
                    getUI().getNavigator().addView("ExerciseView", new ExerciseView(authentication, (ExerciseSpec)clickevent.getItem(), courseInstance));
                    getUI().getNavigator().navigateTo("ExerciseView");
                })
        );
        Button cancelBtn = new Button("Peruuta");
        cancelBtn.addClickListener(clickEvent ->
                getUI().getNavigator().navigateTo("StudentCourses"));
        exerciseLayout.addComponents(exercisesGrid, cancelBtn);
    }
}