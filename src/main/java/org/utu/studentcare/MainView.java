package org.utu.studentcare;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import org.utu.studentcare.applogic.AppLogicException;

import java.sql.SQLException;

public class MainView extends VerticalLayout {
    MenuBar menuBar = new MenuBar(); //Menu bar in top of application
    Navigator navigator;
    SessionAuthentication authentication;

    //Main view of application, all other views opens inside
    public MainView(MyUI ui, SessionAuthentication authentication) throws AppLogicException, SQLException {
        this.authentication = authentication;

        HorizontalLayout viewContainer = new HorizontalLayout(); //Container of other views
        navigator = new Navigator(ui, viewContainer);

        //If user reloads page for some reason while in mainview without any view open, it loads StudentCourses to dodge unknown state ('') error
        navigator.addView("", new StudentCourses(authentication));

        //Basic views for every user (everyone is student)
        try {
            navigator.addView("JoinCourses", new JoinCourses(authentication));
            navigator.addView("StudentCourses", new StudentCourses(authentication));
        } catch (Exception e) {e.printStackTrace();}

        addComponent(menuBar);

        //Binds basic views to menubar
        MenuBar.MenuItem joinCourses = menuBar.addItem("Liity kursseille", (menuItem -> getUI().getNavigator().navigateTo("JoinCourses")));
        MenuBar.MenuItem studentCourses = menuBar.addItem("Omat kurssisi", (menuItem -> getUI().getNavigator().navigateTo("StudentCourses")));

        //If user is teacher -> create and show teacher views
        authentication.getStudent().filter(student -> student.isTeacher).ifPresent(student -> {
            addTeacherButtons();
        });

        //If user is administrator -> create and show admin views
        authentication.getStudent().filter(student -> student.isAdmin).ifPresent(student -> {
            addAdminButtons();
        });

        menuBar.addItem("Kirjaudu ulos", (menuItem -> {
            try {
                if (authentication.logOut()) {
                    getUI().close(); //closes current Vaadin instance
                    JavaScript.getCurrent().execute("location.reload()"); //performs browser reload after webapp closing -> returns to loginpage
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        addComponent(new Label("Tervetuloa Studentcare-järjestelmään " + authentication.getStudent().get().firstNames
                + " " +  authentication.getStudent().get().familyName));

        addComponent(viewContainer);

        ui.setContent(this);

    }
    private void addTeacherButtons() {
        navigator.addView("TeachCourses", new TeachCourses(authentication));
        MenuBar.MenuItem teachCourses = menuBar.addItem("Opeta kursseja", (menuItem -> getUI().getNavigator().navigateTo("TeachCourses")));
        navigator.addView("GradeCourses", new GradeCourses(authentication));
        MenuBar.MenuItem gradeCourses = menuBar.addItem("Arvioi kurssisuorituksia", (menuItem -> getUI().getNavigator().navigateTo("GradeCourses")));
    }
    private void addAdminButtons() {
        navigator.addView("ApproveCourses", new ApproveCourses(authentication));
        MenuBar.MenuItem approveCourses = menuBar.addItem("Suoritusten hyväksyminen", (menuItem -> getUI().getNavigator().navigateTo("ApproveCourses")));
        navigator.addView("DbControl", new DbControl(authentication));
        MenuBar.MenuItem dbControl = menuBar.addItem("Tietokannan hallinta", (menuItem -> getUI().getNavigator().navigateTo("DbControl")));
    }

}
