package at.ac.htlstp.app.cocolib;

import android.content.Context;

import java.net.URI;

import at.ac.htlstp.app.cocolib.parse.MapParser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author alexnavratil
 */
public class CocoLibConfiguration {
    private MapParser mapParser;
    private URI APIUrl;
    private Context context;

    public CocoLibConfiguration() {
    }

    public CocoLibConfiguration(Context context, MapParser mapParser, URI APIUrl) {
        this.mapParser = mapParser;
        this.APIUrl = APIUrl;
        this.context = context;
    }

    public MapParser getMapParser() {
        return mapParser;
    }

    public CocoLibConfiguration setMapParser(MapParser mapParser) {
        this.mapParser = mapParser;
        return this;
    }

    public URI getAPIUrl() {
        return APIUrl;
    }

    public CocoLibConfiguration setAPIUrl(URI APIUrl) {
        this.APIUrl = APIUrl;
        return this;
    }

    public Context getContext() {
        return context;
    }

    public CocoLibConfiguration setContext(Context context) {
        this.context = context;
        return this;
    }
}
