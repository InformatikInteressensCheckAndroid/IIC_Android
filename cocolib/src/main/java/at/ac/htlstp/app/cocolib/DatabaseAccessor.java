/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.htlstp.app.cocolib;

import android.content.Context;

import java.util.List;

import at.ac.htlstp.app.cocolib.helper.Variable;

/**
 * @author alexnavratil
 */
public interface DatabaseAccessor<T> {
    APIResult<T> getFromDatabase(Context context, List<Variable> variableList);

    void saveToDatabase(Context context, T object);
}
