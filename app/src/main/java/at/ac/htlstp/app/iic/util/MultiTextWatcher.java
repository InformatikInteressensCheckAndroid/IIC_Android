package at.ac.htlstp.app.iic.util;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by alexnavratil on 07/02/16.
 */
public abstract class MultiTextWatcher implements TextWatcher {
    private String id;

    public MultiTextWatcher() {
    }

    public MultiTextWatcher(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
