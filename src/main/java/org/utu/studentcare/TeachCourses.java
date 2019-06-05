package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class TeachCourses extends HorizontalLayout implements View {
    public TeachCourses(){
        addComponent(new Label("Opetusnäkymä"));
    }
}
