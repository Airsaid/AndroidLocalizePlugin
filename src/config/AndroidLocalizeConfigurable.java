/*
 * Copyright 2018 Airsaid. https://github.com/airsaid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package config;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ItemEvent;

/**
 * @author airsaid
 */
public class AndroidLocalizeConfigurable implements Configurable {

    private JPanel       myPanel;
    private JRadioButton noProxy;
    private JLabel       hostNameLabel;
    private JRadioButton enableProxy;
    private JLabel       portNumberLabel;
    private JTextField   hostNameField;
    private JSpinner     portNumberSpinner;
    private JCheckBox translateTogether;

    private boolean isEnableProxy = PluginConfig.isEnableProxy();
    private String  hostName      = PluginConfig.getHostName();
    private int     portNumber    = PluginConfig.getPortNumber();
    private boolean isTranslateTogether = PluginConfig.isTranslateTogether();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Android Localize";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        init();
        return myPanel;
    }

    private void init() {
        enableProxy(isEnableProxy);
        hostNameField.setText(hostName);
        portNumberSpinner.setValue(portNumber);
        translateTogether.setSelected(isTranslateTogether);

        noProxy.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                enableProxy(false);
            }
        });

        enableProxy.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                enableProxy(true);
            }
        });
    }

    private void enableProxy(boolean enabled) {
        noProxy.setSelected(!enabled);
        enableProxy.setSelected(enabled);
        hostNameLabel.setEnabled(enabled);
        hostNameField.setEnabled(enabled);
        portNumberLabel.setEnabled(enabled);
        portNumberSpinner.setEnabled(enabled);
    }

    @Override
    public boolean isModified() {
        boolean modifyEnable = isEnableProxy != enableProxy.isSelected();
        boolean modifyHostName = !hostName.equals(hostNameField.getText());
        boolean modifyPortNumber = portNumber != Integer.parseInt(portNumberSpinner.getValue().toString());
        boolean modifyTogether = isTranslateTogether != translateTogether.isSelected();
        return modifyEnable || modifyHostName || modifyPortNumber || modifyTogether;
    }

    @Override
    public void apply() {
        PluginConfig.setEnableProxy(enableProxy.isSelected());
        PluginConfig.setHostName(hostNameField.getText());
        PluginConfig.setPortNumber(Integer.parseInt(portNumberSpinner.getValue().toString()));
        PluginConfig.setTranslateTogether(translateTogether.isSelected());

        isEnableProxy = enableProxy.isSelected();
        hostName      = hostNameField.getText();
        portNumber    = Integer.parseInt(portNumberSpinner.getValue().toString());
        isTranslateTogether = translateTogether.isSelected();
    }

    @Override
    public void reset() {
        PluginConfig.setEnableProxy(isEnableProxy);
        PluginConfig.setHostName(hostName);
        PluginConfig.setPortNumber(portNumber);
        PluginConfig.setTranslateTogether(isTranslateTogether);
        enableProxy(isEnableProxy);
        hostNameField.setText(hostName);
        portNumberSpinner.setValue(portNumber);
        translateTogether.setSelected(isTranslateTogether);
    }
}
