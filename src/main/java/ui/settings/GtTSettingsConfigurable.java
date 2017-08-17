package ui.settings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;

/**
 * Конфигурация и работа с меню настроек
 * Created by v.nosov on 05.06.2017.
 */
public class GtTSettingsConfigurable implements SearchableConfigurable {
    private GtTSettingsPanel mySettingsPane;

    public GtTSettingsConfigurable() {
    }

    @NotNull
    public String getDisplayName() {
        return "GtT";
    }

    @NotNull
    public String getHelpTopic() {
        return "settings.GtTSettingsConfigurable";
    }

    @NotNull
    public String getId() {
        return getHelpTopic();
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        //todo возможно, понадобится добавить прекодишн перед поиском
        return null;
    }

    @NotNull
    public JComponent createComponent() {
        if (mySettingsPane == null) {
            mySettingsPane = new GtTSettingsPanel();
        }
        return mySettingsPane.getPanel();
    }

    /**
     * Используется для включения/выключения кнопки apply
     * true - apply станет активной
     */
    public boolean isModified() {
        return mySettingsPane != null && mySettingsPane.isModified();
    }

    /**
     * Вызывается нажатием кнопки apply
     */
    public void apply() throws ConfigurationException {
        if (mySettingsPane != null) {
            mySettingsPane.apply();
        }
    }

    /**
     * Вызывается по нажатию на пкопку OK или Apply
     */
    public void reset() {
        if (mySettingsPane != null) {
            mySettingsPane.reset();
        }
    }

    public void disposeUIResources() {
        mySettingsPane = null;
    }
}
