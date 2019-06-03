package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import org.utu.studentcare.applogic.Session;
import org.utu.studentcare.db.orm.Student;

import java.util.Optional;

public class MainView extends VerticalLayout {
    MenuBar menuBar = new MenuBar();
    public MainView(MyUI ui, SessionAuthentication authentication) {


        addComponent(menuBar);
        MenuBar.MenuItem joinCourses = menuBar.addItem("Liity kursseille", null, null);
        MenuBar.MenuItem studentCourses = menuBar.addItem("Omat kurssisi", null, null);

        addComponent(new Label("DEBUG: toString of isTecher returns: " + authentication.getStudent().filter(student -> student.isTeacher).toString()));

        authentication.getStudent().filter(student -> student.isTeacher).ifPresent(student -> {
            addTeacherButtons();
                });

        authentication.getStudent().filter(student -> student.isAdmin).ifPresent(student -> {
            addAdminButtons();
        });
        setStyleName("main-screen");



        addComponent(new Label("Login successful, welcome " + authentication.getStudent().toString()));
        TextField textField = new TextField("textfield");
        addComponents(textField);
        textField.setValue("you passed login screen");
        ui.setContent(this);
        System.out.println("Test123");
    }
    private void addTeacherButtons() {
        MenuBar.MenuItem teachCourses = menuBar.addItem("Opeta kursseja", null, null);
        MenuBar.MenuItem gradeCourses = menuBar.addItem("Arvioi kurssisuorituksia", null, null);
    }
    private void addAdminButtons() {
        MenuBar.MenuItem approveCourses = menuBar.addItem("Suoritusten hyväksyminen", null, null);
        MenuBar.MenuItem dbControl = menuBar.addItem("Tietokannan hallinta", null, null);
    }

}
