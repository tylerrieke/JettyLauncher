package com.rieke.jettylauncher.gui.textfield.verifier;

import com.rieke.jettylauncher.gui.textfield.ValidatedJTextField;

import javax.swing.*;
import java.io.File;

public class FileVerifier extends InputVerifier {

    public static final String ERROR_MESSAGE = "File does not exist. Leave blank to use default category list.";
    private ValidatedJTextField field;
    private String value;

    public FileVerifier(ValidatedJTextField field) {
        this.field = field;
    }

    @Override
    public boolean shouldYieldFocus(JComponent input) {
        verify(input);
        return true;
    }

    @Override
    public boolean verify(JComponent input) {
        value = field.getText().trim();
        boolean valid = value.isEmpty() || new File(value).exists();
        field.setText(value);
        if (valid) {
            field.hideError();
        } else {
            field.showError();
        }
        return valid;
    }
}
