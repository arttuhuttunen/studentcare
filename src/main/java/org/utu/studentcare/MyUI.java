

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import org.sqlite.SQLiteConnection;
import org.sqlite.core.DB;
import org.utu.studentcare.applogic.AppLogicException;
import org.utu.studentcare.applogic.DBApp;
import org.utu.studentcare.db.SQLConnection;
import org.utu.studentcare.db.orm.*;

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
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        

        /* final TextField name = new TextField();
        name.setCaption("Type your name here:");

        Button button = new Button("Click Me");
        button.addClickListener(e -> {
            layout.addComponent(new Label("Thanks " + name.getValue()
                    + ", it works!"));
        });*/





        LoginForm loginForm = new LoginForm();
        loginForm.setUsernameCaption("Käyttäjätunnus");
        loginForm.setPasswordCaption("Salasana");
        loginForm.setLoginButtonCaption("Kirjaudu sisään");
        loginForm.addLoginListener(new LoginForm.LoginListener() {
            @Override
            public void onLogin(LoginForm.LoginEvent loginEvent) {
                String uname = loginEvent.getLoginParameter("username");
                String pword = loginEvent.getLoginParameter("password");
                try {
                    SQLConnection connection = SQLConnection.createConnection("value4life.db", false);
                    String authTest = Student.authenticate(connection, uname, pword).toString();
                    layout.addComponent(new Label("DEBUG; authTest returns: " + authTest));
                } catch (Exception e) {e.printStackTrace();}
            }
        });


        JavaScript.getCurrent().execute("window.alert('This is popup'");
        layout.addComponents(loginForm);

        setContent(layout);


    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }

    /**Method for checking whether user with given username and password exists
     * If user does exist, pass auth values to mainView (WIP)
     * Currently return value is boolean, in future might be Stringlist etc.
     */

    private boolean loginControl(SQLConnection sqlConnection ,String username, String password) throws SQLException, AppLogicException {
        if (Student.authenticate(sqlConnection, username, password).equals("Optional.empty")) {
            return false;
        } else {return true;}
    }
}
