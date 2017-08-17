package ui.settings;

import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import gnu.trove.Equality;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.GtTAuthData;
import util.GtTSettings;
import javax.swing.*;

/**
 * Фрейм настроек
 * Created by v.nosov on 06.06.2017.
 */
public class GtTSettingsPanel {
    private final GtTSettings mySettings;

    private JPanel myPane;
    private JSpinner myTimeoutSpinner;
    private GtTCredentialsPanel myCredentialsPanel;

    public GtTSettingsPanel() {
        mySettings = GtTSettings.getInstance();
        reset();
    }

    private void createUIComponents() {
        myCredentialsPanel = new GtTCredentialsPanel(ProjectManager.getInstance().getDefaultProject());
        myTimeoutSpinner = new JSpinner(new SpinnerNumberModel(5000, 0, 60000, 500));
    }

    /**
     * Вызывается по нажатию на пкопку OK или Apply
     */
    public void reset() {
        myCredentialsPanel.setAuthData(mySettings.getAuthData());
        setConnectionTimeout(mySettings.getConnectionTimeout());
    }

    /**
     * Вызывается нажатием кнопки apply
     */
    public void apply() {
        if (!equal(mySettings.getAuthData(), getAuthData())) {
            mySettings.setAuthData(getAuthData(), true);
        }
        mySettings.setConnectionTimeout(getConnectionTimeout());
    }

    /**
     * Используется для включения/выключения кнопки apply
     * true - apply станет активной
     */
    public boolean isModified() {
        return  !equal(mySettings.getAuthData(), getAuthData()) ||
                !Comparing.equal(mySettings.getConnectionTimeout(), getConnectionTimeout());
    }

    //================================ get and set =====================================================================

    /**
     * Вытягиваем настройки из формы
     */
    @NotNull
    public GtTAuthData getAuthData() {
        GtTAuthData authData = myCredentialsPanel.getAuthData();
        //если мы не заполнили поля, то создаем анонимного пользователя
        if (authData.getBasicAuth() != null && StringUtil.isEmptyOrSpaces(authData.getBasicAuth().getLogin())) {
            return GtTAuthData.createAnonymous(myCredentialsPanel.getHost());
        }
        return authData;
    }

    public void setConnectionTimeout(int timeout) {
        myTimeoutSpinner.setValue(Integer.valueOf(timeout));
    }

    public int getConnectionTimeout() {
        return ((SpinnerNumberModel)myTimeoutSpinner.getModel()).getNumber().intValue();
    }

    public JComponent getPanel() {
        return myPane;
    }

    //============================== private methods ===================================================================

    private static boolean equal(@NotNull GtTAuthData data1, @NotNull GtTAuthData data2) {
        return Comparing.equal(data1.getHost(), data2.getHost()) &&
                Comparing.equal(data1.getAuthType(), data2.getAuthType()) &&
                equal(data1.getBasicAuth(), data2.getBasicAuth(),
                        (auth1, auth2) -> Comparing.equal(auth1.getLogin(), auth2.getLogin()) &&
                                Comparing.equal(auth1.getPassword(), auth2.getPassword()));
    }

    private static <T> boolean equal(@Nullable T o1, @Nullable T o2, @NotNull Equality<T> notNullEquality) {
        if (o1 == o2) return true;
        if (o1 == null) return false;
        if (o2 == null) return false;
        return notNullEquality.equals(o1, o2);
    }
}
