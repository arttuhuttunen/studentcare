package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.db.orm.CourseInstance;
import org.utu.studentcare.db.orm.Student;

import java.sql.SQLException;
import java.util.List;

public class GradeCourses extends VerticalLayout implements View {
    Grid<CourseInstance> courseGrid = new Grid<>();
    List<CourseInstance> courses;
    SessionAuthentication authentication;
    public GradeCourses(SessionAuthentication authentication) {
        addComponent(new Label("Arvostele kursseja näkymä"));
        this.authentication = authentication;
        courseGrid.setWidth("1200");
        addComponent(courseGrid);
    }
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        try {
            loadColumns();
        } catch (AppLogicException | SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadColumns() throws AppLogicException, SQLException {
        courseGrid.removeAllColumns();
        Student opt = authentication.getStudent().get();
        courses = opt.asTeacher().teaching(authentication.getConnection());
        courseGrid.setItems(courses);
        courseGrid.addColumn(courseInstance -> courseInstance.wholeNameId(40)).setCaption("Kurssin nimi").setId("courseName");
        courseGrid.sort("courseName", SortDirection.ASCENDING);
        courseGrid.addColumn(grade -> "Siirry arvostelemaan kurssisuorituksia",
                new ButtonRenderer<>(clickevent -> {
                    getUI().getNavigator().addView("GradeCourse", new GradeCourse(authentication, clickevent.getItem()));
                    getUI().getNavigator().navigateTo("GradeCourse");
                }));
        courseGrid.addColumn(course -> "Lopeta kurssin opettaminen",
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
                    popUpContent.addComponent(new Label("Haluatko varmasti lopettaa kurssin " + clickevent.getItem().wholeNameId(50) + " opettamisen?"));

                    Button confirmBtn = new Button("Kyllä");
                    confirmBtn.addClickListener((Button.ClickListener) clickEvent -> {
                        try {
                            if (opt.asTeacher().abandonCourse(authentication.getConnection(), clickevent.getItem())) {
                                popupView.close();
                                Notification.show("Lopetettu kurssin " + clickevent.getItem().wholeNameId(40) + " opettaminen").setDelayMsec(3000);
                                loadColumns();
                            } else {
                                Notification.show("VIRHE: Kurssin opettamisen lopettaminen epäonnistui, yritä myöhemmin uudestaan", Notification.Type.ERROR_MESSAGE).setDelayMsec(3000);
                            }
                        } catch (SQLException | AppLogicException e) {
                            e.printStackTrace();
                            Notification.show("Tapahtui odottamaton virhe, päivitä sivu ja yritä uudelleen", Notification.Type.ERROR_MESSAGE).setDelayMsec(3000);
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
