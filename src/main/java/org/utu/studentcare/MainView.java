package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import org.utu.studentcare.applogic.Session;
import org.utu.studentcare.db.orm.Student;

import java.util.Optional;

public class MainView extends VerticalLayout {
    public MainView(MyUI ui, SessionAuthentication authentication) {


        setStyleName("main-screen");

        addComponent(new Label("Login successful, welcome " + authentication.getStudent().toString()));
        TextField textField = new TextField("textfield");
        addComponents(textField);
        textField.setValue("you passed login screen");
        ui.setContent(this);
        System.out.println("Test123");
    }
}
