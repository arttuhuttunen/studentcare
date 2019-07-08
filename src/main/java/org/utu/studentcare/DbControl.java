package org.utu.studentcare;

import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import org.atmosphere.util.ServletProxyFactory;
import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.db.DBCleaner;

import java.sql.SQLException;

public class DbControl extends VerticalLayout implements View {
    SessionAuthentication authentication;

    public DbControl(SessionAuthentication authentication) {
        this.authentication = authentication;
        addComponent(new Label("Tietokannan hallinta"));
        Button dbBtn = new Button("Alusta tietokanta");
        addComponent(dbBtn);
        dbBtn.addClickListener(click -> {
            VerticalLayout popUpContent = new VerticalLayout();
            HorizontalLayout buttonLayout = new HorizontalLayout();
            Window popUpView = new Window(null);
            popUpView.setClosable(false);
            popUpView.center();
            popUpView.setResizable(false);
            popUpView.setHeightUndefined();
            popUpView.setWidthUndefined();
            popUpContent.setHeightUndefined();
            popUpContent.setWidthUndefined();
            popUpContent.addComponent(new Label("Haluatko varmasti alustaa tietokannan? Tämän jälkeen sinut kirjataan ulos"));

            Button confirmBtn = new Button("Kyllä");
            Button cancelBtn = new Button("Ei");

            confirmBtn.addClickListener(clickEvent -> {
                try {
                    DBCleaner dbCleaner = new DBCleaner(authentication.getConnection());
                    System.out.println(dbCleaner.wipeTables().populateTables().debug());
                    if (authentication.logOut()) {
                        getUI().close();
                        JavaScript.getCurrent().execute("location.reload()");
                    } else {
                        Notification.show("Virhe uloskirjautumisessa. Päivitä sivu ja yritä uudelleen", Notification.Type.ERROR_MESSAGE).setDelayMsec(5000);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            cancelBtn.addClickListener(clickEvent -> popUpView.close());

            getUI().addWindow(popUpView);
            buttonLayout.addComponents(confirmBtn, cancelBtn);
            popUpContent.addComponent(buttonLayout);
            popUpView.setContent(popUpContent);
            popUpContent.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
        });
    }
}
