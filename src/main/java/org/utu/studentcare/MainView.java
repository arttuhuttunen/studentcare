package org.utu.studentcare;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.*;

public class MainView extends VerticalLayout {
    MenuBar menuBar = new MenuBar();
    Navigator navigator;
    public MainView(MyUI ui, SessionAuthentication authentication) {

        HorizontalLayout viewContainer = new HorizontalLayout();
        navigator = new Navigator(ui, viewContainer);

        navigator.addView("JoinCourses", new JoinCourses());
        navigator.addView("StudentCourses", new StudentCourses());
        navigator.addView("TeachCourses", new TeachCourses());


        addComponent(menuBar);
        MenuBar.MenuItem joinCourses = menuBar.addItem("Liity kursseille", (menuItem -> getUI().getNavigator().navigateTo("JoinCourses")));
        MenuBar.MenuItem studentCourses = menuBar.addItem("Omat kurssisi", (menuItem -> getUI().getNavigator().navigateTo("StudentCourses")));


        authentication.getStudent().filter(student -> student.isTeacher).ifPresent(student -> {
            addTeacherButtons();
        });

        authentication.getStudent().filter(student -> student.isAdmin).ifPresent(student -> {
            addAdminButtons();
        });
        //setStyleName("main-screen");

        addComponent(new Label("Tervetuloa Studentcare-järjestelmään " + authentication.getStudent().get().firstNames
                + " " +  authentication.getStudent().get().familyName));

        addComponent(viewContainer);

        ui.setContent(this);

    }
    private void addTeacherButtons() {
        navigator.addView("TeachCourses", new TeachCourses());
        MenuBar.MenuItem teachCourses = menuBar.addItem("Opeta kursseja", (menuItem -> getUI().getNavigator().navigateTo("TeachCourses")));
        navigator.addView("GradeCourses", new GradeCourses());
        MenuBar.MenuItem gradeCourses = menuBar.addItem("Arvioi kurssisuorituksia", (menuItem -> getUI().getNavigator().navigateTo("GradeCourses")));
    }
    private void addAdminButtons() {
        navigator.addView("ApproveCourses", new ApproveCourses());
        MenuBar.MenuItem approveCourses = menuBar.addItem("Suoritusten hyväksyminen", (menuItem -> getUI().getNavigator().navigateTo("ApproveCourses")));
        navigator.addView("DbControl", new DbControl());
        MenuBar.MenuItem dbControl = menuBar.addItem("Tietokannan hallinta", (menuItem -> getUI().getNavigator().navigateTo("DbControl")));
    }

}
