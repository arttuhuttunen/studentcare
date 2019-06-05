package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class GradeCourses extends HorizontalLayout implements View {
    public GradeCourses() {
        addComponent(new Label("Arvostele kursseja näkymä"));
    }
}
