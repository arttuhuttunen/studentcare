package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class StudentCourses extends HorizontalLayout implements View {
    public StudentCourses() {
        addComponent(new Label("Omat kurssit näkymä"));
    }
}
