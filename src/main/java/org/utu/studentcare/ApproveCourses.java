package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ButtonRenderer;
import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.db.orm.CourseGrade;

import java.sql.SQLException;
import java.util.List;

//This view shows administrator course grades waiting for approval
public class ApproveCourses extends VerticalLayout implements View {
    SessionAuthentication authentication;
    Grid<CourseGrade> gradeGrid;

    public ApproveCourses(SessionAuthentication authentication) {
        this.authentication = authentication;
        addComponent(new Label("Kurssiarvostelujen lisääminen opintorekisteriin"));
        gradeGrid = new Grid<>();
        addComponent(gradeGrid);
        gradeGrid.setWidth("1400");
    }

    public void enter(ViewChangeListener.ViewChangeEvent event) {
        try {
            loadColumns();
        } catch (AppLogicException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadColumns() throws AppLogicException, SQLException {
        gradeGrid.removeAllColumns();
        List<CourseGrade> gradeList = CourseGrade.waitingApproval(authentication.getConnection());
        gradeGrid.setItems(gradeList);
        gradeGrid.addColumn(courseGrade -> {
            try {
                return courseGrade.course(authentication.getConnection()).wholeNameId(40);
            } catch (SQLException | AppLogicException e) {
                e.printStackTrace();
                return Notification.show("Tapahtui odottamaton virhe, yritä myöhemmin uudestaan", Notification.Type.ERROR_MESSAGE);
            }
        }).setCaption("Kurssin nimi").setId("courseName");
        gradeGrid.sort("courseName", SortDirection.ASCENDING);
        gradeGrid.addColumn(courseGrade -> {
            try {
                return courseGrade.student(authentication.getConnection()).familyName;
            } catch (SQLException | AppLogicException e) {
                e.printStackTrace();
                return Notification.show("Tapahtui odottamaton virhe, yritä myöhemmin uudestaan", Notification.Type.ERROR_MESSAGE);
            }
        }).setCaption("Sukunimi");
        gradeGrid.addColumn(courseGrade -> {
            try {
                return courseGrade.student(authentication.getConnection()).firstNames;
            } catch (SQLException | AppLogicException e) {
                e.printStackTrace();
                return Notification.show("Tapahtui odottamaton virhe, yritä myöhemmin uudestaan", Notification.Type.ERROR_MESSAGE);
            }
        }).setCaption("Etunimet");
        gradeGrid.addColumn(courseGrade -> {
            try {
                return courseGrade.student(authentication.getConnection()).id;
            } catch (SQLException | AppLogicException e) {
                e.printStackTrace();
                return Notification.show("Tapahtui odottamaton virhe, yritä myöhemmin uudestaan", Notification.Type.ERROR_MESSAGE);
            }
        }).setCaption("Opiskelijanumero");
        gradeGrid.addColumn(courseGrade -> courseGrade.grade).setCaption("Arvosana");
        gradeGrid.addColumn(courseGrade -> courseGrade.gradeDate).setCaption("Arvosteltu");
        gradeGrid.addColumn(courseGrade -> "Kirjaa opintorekisteriin",
                new ButtonRenderer<>(clickevent -> {
                    try {
                        if (clickevent.getItem().approve(authentication.getConnection(), authentication.getStudent().get().id)) {
                            loadColumns();
                            Notification.show("Opiskelijan " + clickevent.getItem().student(authentication.getConnection()).wholeName() + " suoritus kirjattu opintorekisteriin!").setDelayMsec(5000);
                        } else {
                            Notification.show("VIRHE: Kurssin kirjaaminen epäonnistui, yritä myöhemmin uudestaan");
                        }
                    } catch (SQLException | AppLogicException e) {
                        e.printStackTrace();
                    }
                }));
    }
}
