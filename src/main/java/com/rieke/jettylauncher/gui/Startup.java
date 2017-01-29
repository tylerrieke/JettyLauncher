package com.rieke.jettylauncher.gui;

import com.rieke.jettylauncher.JettyRunner;
import com.rieke.jettylauncher.State;
import com.rieke.jettylauncher.gui.textfield.ValidatedJTextField;
import com.rieke.jettylauncher.gui.textfield.verifier.PortVerifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Startup extends JFrame {
    // This class acts as listener for ActionEvent and WindowEvent
    // A Java class can extend one superclass, but can implement multiple interfaces.
    public static final int DEFAULT_PORT = 80;

    protected JButton stopButton;  // Declare a TextField component
    protected JButton startButton;    // Declare a Button component
    protected JButton exitButton;    // Declare a Button component
    protected Set<ValidatedJTextField> fieldsToValidate = new HashSet<ValidatedJTextField>();
    private JTextField status;
    private ValidatedJTextField portNumber;
    private ExecutorService executorService;
    public static JettyRunner jettyServer;

    protected void init(JettyRunner jettyRunner, Container cp) {
        //SETTINGS PANEL
        cp.add(createSettingsPanel(),BorderLayout.CENTER);
        //SERVER PANEL
        JPanel serverPanel = new JPanel(new BorderLayout());
        status = new JTextField("STOPPED", 25);
        status.setHorizontalAlignment(JTextField.CENTER);
        status.setEditable(false);
        jettyServer.setStatusField(status);
        serverPanel.add(status,BorderLayout.PAGE_END);
        serverPanel.add(createServerButtonPanel(),BorderLayout.CENTER);
        cp.add(serverPanel,BorderLayout.PAGE_END);
    }

    // Constructor to setup the GUI components and event handlers
    public Startup(JettyRunner jettyRunner) {
        jettyServer = jettyRunner;

        executorService = Executors.newFixedThreadPool(2);
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());   // The content-pane sets its layout

        init(jettyRunner,cp);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Exit program if close-window button clicked
        setVisible(true);

        jettyServer.addStateListener(new JettyRunner.StateListener() {
            public void onStateChange(State state) {
                switch (state) {
                    case STARTING:
                        startButton.setEnabled(false);
                        stopButton.setEnabled(true);
                        break;
                    case STOPPED:
                        startButton.setEnabled(true);
                        stopButton.setEnabled(false);
                        break;
                }
            }
        });
    }

    protected Collection<JPanel> settingsBeforePortNumber() {
        return new ArrayList<JPanel>();
    }

    protected Collection<JPanel> settingsAfterPortNumber() {
        return new ArrayList<JPanel>();
    }

    protected void addFieldToBeValidated(ValidatedJTextField field) {
        fieldsToValidate.add(field);
    }

    protected JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridLayout(0,1));
        ArrayList<JPanel> panelItems = new ArrayList<JPanel>();

        panelItems.addAll(settingsBeforePortNumber());

        JPanel portNumberPanelLayout = new JPanel(new BorderLayout());
        JPanel portNumberPanel = new JPanel();
        portNumberPanel.add(new JLabel("Port number:"));
        portNumber = new ValidatedJTextField(String.valueOf(DEFAULT_PORT), 6, PortVerifier.ERROR_MESSAGE);
        portNumber.setEditable(true);
        portNumber.setInputVerifier(new PortVerifier(portNumber));
        portNumber.setCustomActions(new ValidatedJTextField.Custom() {
            @Override
            public void onErrorMessageChange() {
                pack();
            }
        });
        portNumberPanel.add(portNumber);
        portNumberPanelLayout.add(portNumberPanel,BorderLayout.CENTER);
        portNumberPanelLayout.add(portNumber.getErrorText(),BorderLayout.PAGE_END);
        panelItems.add(portNumberPanelLayout);
        addFieldToBeValidated(portNumber);

        panelItems.addAll(settingsAfterPortNumber());

        for(JPanel item:panelItems) {
            panel.add(item);
        }
        panel.add(new JSeparator());
        panel.setBorder(BorderFactory.createTitledBorder("Settings"));

        return panel;
    }

    protected void startJetty() {
        int port;
        try {
            port = Integer.parseInt(portNumber.getText());
        } catch (NumberFormatException e) {
            port = DEFAULT_PORT;
        }
        try {
            jettyServer.startJetty(port);
        } catch (Exception ex) {
            System.out.println("Failed to start server");
        }
    }

    private JPanel createServerButtonPanel() {
        JPanel panel = new JPanel();
        startButton = new JButton("Start");
        panel.add(startButton);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(validateFields()) {
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            startJetty();
                        }
                    });
                }
            }
        });

        stopButton = new JButton("Stop");
        panel.add(stopButton);
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jettyServer.stop();
            }
        });
        stopButton.setEnabled(false);

        exitButton = new JButton("Exit");
        panel.add(exitButton);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jettyServer.stop();
                System.exit(1);
            }
        });
        return panel;
    }

    private boolean validateFields() {
        boolean valid = true;
        for(ValidatedJTextField field:fieldsToValidate) {
            if(!field.isFieldValid()) {
                valid = false;
                break;
            }
        }

        if(valid) {
            onFieldsValid();
        } else {
            onFieldsInvalid();
        }
        pack();
        return valid;
    }

    private void onFieldsValid() {
        //startButton.setEnabled(true);
    }

    private void onFieldsInvalid() {
        //startButton.setEnabled(false);
    }
}
