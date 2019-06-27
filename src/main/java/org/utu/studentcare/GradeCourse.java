package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.utu.studentcare.applogic.Session;
import org.utu.studentcare.db.orm.CourseInstance;

public class GradeCourse extends VerticalLayout implements View {
    SessionAuthentication authentication;
    CourseInstance courseInstance;
    public GradeCourse(SessionAuthentication authentication, CourseInstance courseInstance) {
        this.authentication = authentication;
        this.courseInstance = courseInstance;
        addComponent(new Label("Kurssin " + courseInstance.name + " arvostelunäkymä"));
    }
}
