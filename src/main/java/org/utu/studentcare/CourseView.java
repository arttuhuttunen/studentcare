package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.db.orm.CourseInstance;

import java.sql.SQLException;

public class CourseView extends HorizontalLayout implements View {
    public CourseView(SessionAuthentication authentication, CourseInstance courseInstance) throws SQLException, AppLogicException {
        addComponent(new Label("Kurssin " + courseInstance.name + " näkymä"));
    }
}