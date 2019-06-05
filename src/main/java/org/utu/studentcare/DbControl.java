package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class DbControl extends HorizontalLayout implements View {
    public DbControl() {
        addComponent(new Label("Tietokannan hallinta näkymä"));
    }
}
