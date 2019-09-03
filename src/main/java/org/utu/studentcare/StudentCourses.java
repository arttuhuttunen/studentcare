package org.utu.studentcare;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.SingleSelectionModel;
import com.vaadin.ui.renderers.ButtonRenderer;
import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.db.orm.Course;
import org.utu.studentcare.db.orm.CourseInstance;
import org.utu.studentcare.db.orm.Student;

import javax.swing.*;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.*;

//This view shows user's current courses, and gives ability to either view exercises or leave course
public class StudentCourses extends VerticalLayout implements View {
    SessionAuthentication authentication;
    Grid<CourseInstance> courseGrid;

    public StudentCourses(SessionAuthentication authentication) throws SQLException, AppLogicException {
        this.authentication = authentication;
        courseGrid = new Grid<>();
        addComponent(new Label("Omat kurssisi"));

        Student opt = authentication.getStudent().get(); //Student opt is for shortening Optional parameters for better readability
        courseGrid.setWidthUndefined();
        courseGrid.setHeightUndefined();
        courseGrid.setWidth("1200");
        addComponent(courseGrid);
    }

    public void enter(ViewChangeListener.ViewChangeEvent event) {
        loadColumns();
    }

    private void loadColumns() {
        courseGrid.removeAllColumns(); //Removing all columns before loading them again
        List<CourseInstance> courses = null;
        Student opt = authentication.getStudent().get(); //Student opt is for shortening Optional parameters for better readability

        try {
            courses = opt.attending(authentication.getConnection());
        } catch (SQLException | AppLogicException e) {
            e.printStackTrace();
        }
        courseGrid.setItems(courses);
        courseGrid.addColumn(courseInstance -> courseInstance.wholeNameId(40)).setCaption("Kurssin nimi").setId("courseName");
        courseGrid.addColumn(status -> {
            try {
                return opt.exercises(authentication.getConnection(), status.instanceId).status();
            } catch (SQLException | AppLogicException e) {
                e.printStackTrace();
            }
            return new AppLogicException("Virhe taulukon luomisessa");
        }).setCaption("Suorituksen tilanne");
        courseGrid.sort("courseName", SortDirection.ASCENDING);
        courseGrid.addColumn(course -> "Näytä kurssin tiedot",
                new ButtonRenderer(clickevent -> {
                    getUI().getNavigator().addView("CourseView", new CourseView(authentication, (CourseInstance) clickevent.getItem())); //Navigates to exercise view of specific course
                    getUI().getNavigator().navigateTo("CourseView");
                }));

        //This (quite long) statement creates on-window pop-up, which checks whether user really wants to leave course
        courseGrid.addColumn(part -> "Poistu kurssilta",
                new ButtonRenderer<>(clickevent -> {
                    VerticalLayout popUpContent = new VerticalLayout();
                    HorizontalLayout buttonLayout = new HorizontalLayout();
                    Window popupView = new Window(null);
                    popupView.setClosable(false);
                    popupView.setVisible(true);
                    popupView.center();
                    popupView.setResizable(false);
                    popupView.setHeightUndefined();
                    popupView.setWidthUndefined();
                    popUpContent.setWidthUndefined();
                    popUpContent.setHeightUndefined();
                    popUpContent.addComponent(new Label("Haluatko varmasti poistua kurssilta " + clickevent.getItem().wholeNameId(50) + "?"));

                    Button confirmBtn = new Button("Kyllä");
                    confirmBtn.addClickListener((Button.ClickListener) clickEvent -> {
                        try {
                            if (opt.partCourse(authentication.getConnection(), clickevent.getItem())) {
                                popupView.close();
                                loadColumns();
                                Notification.show("Poistuttu kurssilta " + clickevent.getItem().wholeNameId(40)).setDelayMsec(3000);
                            } else {
                                Notification.show("VIRHE: Kurssilta poistuminen epäonnistui, yritä myöhemmin uudestaan", Notification.Type.WARNING_MESSAGE);
                            }
                        } catch (SQLException | AppLogicException e) {
                            e.printStackTrace();
                        }
                    });
                    Button cancelBtn = new Button("Ei");
                    cancelBtn.addClickListener((Button.ClickListener) clickEvent -> popupView.close());

                    getUI().addWindow(popupView);
                    buttonLayout.addComponents(confirmBtn, cancelBtn);
                    popUpContent.addComponent(buttonLayout);
                    popupView.setContent(popUpContent);
                    popUpContent.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
                }));
    }
}