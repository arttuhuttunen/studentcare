

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
        loginForm.addLoginListener(e -> {String username = e.getLoginParameter("username"); String password = e.getLoginParameter("password");});

        try {
            SQLConnection connection = SQLConnection.createConnection("value4life.db", false);
            String authTest =  Student.authenticate(connection, loginForm.getUsernameCaption(), loginForm.getPasswordCaption()).toString();
            layout.addComponent(new Label("DEBUG; authTest returns: " + authTest));
            JavaScript.getCurrent().execute("window.alert('This is popup'");
            JavaScript.getCurrent().execute("window.alert('DEBUG; authTest returns:" + authTest + "');");

        } catch (Exception e) {
            e.printStackTrace();
        }

        layout.addComponents(loginForm);

        setContent(layout);


    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }

    protected void initSQL() {

    }

}
