package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.db.orm.CourseInstance;
import org.utu.studentcare.db.orm.Student;

import java.sql.SQLException;
import java.util.List;

public class JoinCourses extends HorizontalLayout implements View {
    public JoinCourses(SessionAuthentication authentication) throws SQLException, AppLogicException {
        addComponent(new Label("Kurssi-ilmo näkymä"));
        Student opt = authentication.getStudent().get();
        Grid<CourseInstance> courseGrid = new Grid<>();
        List<CourseInstance> courses = opt.notAttending(authentication.getConnection());
        courseGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        courseGrid.setItems(courses);
        courseGrid.addColumn(courseInstance -> courseInstance.wholeNameId(40)).setCaption("Kurssin nimi").setId("courseName");
        courseGrid.sort("courseName" ,SortDirection.ASCENDING);
        addComponent(courseGrid);
    }
}
