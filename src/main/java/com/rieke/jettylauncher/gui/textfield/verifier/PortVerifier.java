package com.rieke.jettylauncher.gui.textfield.verifier;

import com.rieke.jettylauncher.gui.textfield.ValidatedJTextField;

import javax.swing.*;

public class PortVerifier extends InputVerifier {

    public static final String ERROR_MESSAGE = "Valid port numbers are 0 through 65535";
    private ValidatedJTextField field;
    private int value;

    public PortVerifier(ValidatedJTextField field) {
        this.field = field;
    }

    @Override
    public boolean shouldYieldFocus(JComponent input) {
        verify(input);
        return true;
    }

    @Override
    public boolean verify(JComponent input) {
        boolean valid;
        try {
            value = Integer.parseInt(field.getText());
            valid = value<=65535 && value>=0;
        } catch (NumberFormatException e) {
            valid = false;
        }
        if (valid) {
            field.hideError();
        } else {
            field.showError();
        }
        return valid;
    }
}
