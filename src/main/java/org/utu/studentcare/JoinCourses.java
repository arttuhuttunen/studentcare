package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class JoinCourses extends HorizontalLayout implements View {
    public JoinCourses() {
        addComponent(new Label("Kurssi-ilmo näkymä"));
    }
}
