package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class ApproveCourses extends HorizontalLayout implements View {
    public ApproveCourses() {
        addComponent(new Label("Hyväksy arvostelut näkymä"));
    }
}
