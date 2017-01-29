package com.rieke.jettylauncher.gui.textfield;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class ValidatedJTextField extends JTextField {

    public interface Custom {
        void onErrorMessageChange();
    }

    private Custom defaultActions = new Custom() {
        @Override
        public void onErrorMessageChange() {

        }
    };

    private JTextField errorText;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    private AtomicLong messageCount = new AtomicLong(0);

    public ValidatedJTextField(String errorMessage) {
        createTextField(errorMessage);
    }

    public ValidatedJTextField(String text, String errorMessage) {
        super(text);
        createTextField(errorMessage);
    }

    public ValidatedJTextField(int columns, String errorMessage) {
        super(columns);
        createTextField(errorMessage);
    }

    public ValidatedJTextField(String text, int columns, String errorMessage) {
        super(text, columns);
        createTextField(errorMessage);
    }

    public ValidatedJTextField(Document doc, String text, int columns, String errorMessage) {
        super(doc, text, columns);
        createTextField(errorMessage);
    }

    private void createTextField(String errorMessage) {
        errorText = new JTextField();
        errorText.setEditable(false);
        errorText.setVisible(false);
        errorText.setEnabled(true);
        errorText.setForeground(Color.RED);
        errorText.setBackground(Color.LIGHT_GRAY);
        errorText.setText(errorMessage);
        errorText.setHorizontalAlignment(JTextField.CENTER);
    }

    public void addErrorToPanel(JPanel panel) {
        panel.add(errorText);
    }

    public JTextField getErrorText() {
        return errorText;
    }

    public void showError() {
        final long messageNumber = messageCount.incrementAndGet();
        errorText.setVisible(true);
        executorService.submit(new Runnable() {
            private long number = messageNumber;
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    if(number==messageCount.longValue()) {
                        hideError();
                    }
                } catch (Exception e){}
            }
        });
        defaultActions.onErrorMessageChange();
    }

    public void hideError() {
        errorText.setVisible(false);
        defaultActions.onErrorMessageChange();
    }

    public boolean isFieldValid() {
        return getInputVerifier().verify(this);
    }

    public void setCustomActions(Custom customActions) {
        defaultActions = customActions;
    }
}
