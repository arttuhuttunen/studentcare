package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.utu.studentcare.applogic.Session;
import org.utu.studentcare.db.orm.Student;

import java.util.Optional;

public class MainView extends HorizontalLayout implements View {
    public MainView(MyUI ui, SessionAuthentication authentication) {
        final VerticalLayout layout = new VerticalLayout();
        layout.addComponent(new Label("Login successful, welcome " + authentication.getStudent().toString()));
        TextField textField = new TextField("textfield");
        textField.setValue("you passed login screen");
        layout.addComponent(textField);
    }
}
