package org.utu.studentcare;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import org.sqlite.SQLiteConnection;
import org.sqlite.core.DB;
import org.utu.studentcare.SessionAuthentication;
import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.applogic.DBApp;
import org.utu.studentcare.db.SQLConnection;
import org.utu.studentcare.db.orm.*;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Optional;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@PreserveOnRefresh
public class MyUI extends UI {

    Navigator navigator;
    SessionAuthentication authentication = new SessionAuthentication();

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        VaadinSession.getCurrent().getSession().setMaxInactiveInterval(-1); //disables session timeout
        if (!authentication.isUserSignedIn()) {

            final VerticalLayout layout = new VerticalLayout();
            LoginForm loginForm = new LoginForm();
            loginForm.setUsernameCaption("Käyttäjätunnus");
            loginForm.setPasswordCaption("Salasana");
            loginForm.setLoginButtonCaption("Kirjaudu sisään");
            loginForm.addLoginListener((LoginForm.LoginListener) loginEvent -> {
                String uname = loginEvent.getLoginParameter("username");
                String pword = loginEvent.getLoginParameter("password");

                    try {
                        SQLConnection connection = SQLConnection.createConnection("value4life.db", false);
                        if (authentication.loginControl(connection, uname, pword)) {
                            MainView mw = new MainView(MyUI.this, authentication);
                            setContent(mw);
                            //MainView mw = new MainView(MyUI.this, authentication);
                            //navigator.addView("", mw);
                        } else {
                            connection.close();
                            Notification.show("Väärä käyttäjätunnus tai salasana", Notification.Type.ERROR_MESSAGE).setPosition(Position.TOP_CENTER);
                        }
                    } catch (Exception e) {e.printStackTrace();}

            });


            layout.addComponents(loginForm);
            layout.setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);

            setContent(layout);
        } else {
            MainView mw = null;
            try {
                mw = new MainView(MyUI.this, authentication);
            } catch (AppLogicException | SQLException e) {
                e.printStackTrace();
            }
            setContent(mw);

        }
    }

    @Override
    protected void refresh(VaadinRequest request) {
    }

    public static MyUI get() {
        return (MyUI) UI.getCurrent();
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
