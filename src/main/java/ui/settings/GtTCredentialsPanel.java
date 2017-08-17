package ui.settings;

import com.intellij.openapi.project.Project;
import exceptions.GtTAuthenticationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.GtTAuthData;
import util.GtTNotifications;
import util.GtTUtil;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Блок настроек авторизации
 * Created by v.nosov on 05.06.2017.
 */
public class GtTCredentialsPanel extends JPanel {
    //todo private static final Logger LOG = GtTUtil.LOG;

    private JTextField myHostTextField;
    private JTextField myLoginTextField;
    private JPasswordField myPasswordField;
    private JButton myTestButton;

    private JPanel myPane;
    private JPanel myCardPanel;

    public GtTCredentialsPanel(@NotNull Project project) {
        super(new BorderLayout());
        add(myPane, BorderLayout.CENTER);
        //добавляем листенер для кнопки Test
        myTestButton.addActionListener(e -> testAuthData(project));
    }

    public void lockHost(@NotNull String host) {
        setHost(host);
        myHostTextField.setEnabled(false);
    }

    @NotNull
    public String getHost() {
        return myHostTextField.getText().trim();
    }

    @NotNull
    public String getLogin() {
        return myLoginTextField.getText().trim();
    }

    /**
     * Вытягиваем настройки авторизации из формы
     */
    @NotNull
    public GtTAuthData getAuthData() {
        return GtTAuthData.createBasicAuth(getHost(), getLogin(), getPassword());
    }

    public void setHost(@NotNull String host) {
        myHostTextField.setText(host);
    }

    public void setLogin(@Nullable String login) {
        myLoginTextField.setText(login);
    }

    public void setPassword(@NotNull String password) {
        myPasswordField.setText(password);
    }

    /**
     * заполняем поля авторизации из настроек
     */
    public void setAuthData(@NotNull GtTAuthData authData) {
        setHost(authData.getHost());
        GtTAuthData.BasicAuth basicAuth = authData.getBasicAuth();
        assert basicAuth != null;
        setLogin(basicAuth.getLogin());
        setPassword(basicAuth.getPassword());
    }

    public void setTestButtonVisible(boolean visible) {
        myTestButton.setVisible(visible);
    }

    //============================= private methods ====================================================================

    @NotNull
    private String getPassword() {
        return String.valueOf(myPasswordField.getPassword());
    }

    /**
     * Проверяем подключение
     */
    private void testAuthData(@NotNull Project project) {
        try {
            GtTAuthData auth = getAuthData();
            GtTUtil.computeValueInModalIO(project, "Access to TestRail", indicator ->
                    GtTUtil.checkAuthData(auth, indicator));

            GtTNotifications.showInfoDialog(myPane, "Success", "Connection successful");
        } catch (GtTAuthenticationException ex) {
            GtTNotifications.showErrorDialog(myPane, "Login Failure", "Can't login using given credentials: ", ex);
        }
        catch (IOException ex) {
            GtTNotifications.showErrorDialog(myPane, "Login Failure", "Can't login: ", ex);
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
