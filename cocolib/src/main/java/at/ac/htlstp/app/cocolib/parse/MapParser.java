/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.htlstp.app.cocolib.parse;

import java.util.LinkedHashMap;

/**
 * @author alexnavratil
 */
public interface MapParser {
    LinkedHashMap parse(String response);

    LinkedHashMap parseFromObject(Object object);

    String serializeToString(LinkedHashMap parseMap);

    String getContentType();
}
