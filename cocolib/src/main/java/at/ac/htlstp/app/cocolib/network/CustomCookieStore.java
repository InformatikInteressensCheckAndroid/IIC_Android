package at.ac.htlstp.app.cocolib.network;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by alexnavratil on 11.10.15.
 */
public class CustomCookieStore implements CookieStore {
    private Map<URI, List<HttpCookie>> cookieMap = new HashMap<>();

    @Override
    public synchronized void add(URI uri, HttpCookie httpCookie) {
        uri = cookiesUri(uri); //remove subfolders from uri (only contains protocol + host)

        List<HttpCookie> cookieList;
        if (cookieMap.containsKey(uri)) {
            cookieList = cookieMap.get(uri);
            remove(uri, httpCookie);
        } else {
            cookieList = new ArrayList<>(1);
            cookieMap.put(uri, cookieList);
        }

        cookieList.add(httpCookie);
    }

    @Override
    public synchronized List<HttpCookie> get(URI uri) {
        List<HttpCookie> cookieList = cookieMap.get(cookiesUri(uri));
        if (cookieList != null) {
            Iterator<HttpCookie> i = cookieList.iterator();
            while (i.hasNext()) {
                HttpCookie c = i.next();
                if (c.hasExpired()) {
                    i.remove();
                }
            }

            return cookieMap.get(cookiesUri(uri));
        }
        return Collections.emptyList();
    }

    @Override
    public synchronized List<HttpCookie> getCookies() {
        List<HttpCookie> cookieList = new ArrayList<>();
        Iterator<Map.Entry<URI, List<HttpCookie>>> ii = cookieMap.entrySet().iterator();

        while (ii.hasNext()) {
            Map.Entry<URI, List<HttpCookie>> entry = ii.next();

            cookieList.addAll(entry.getValue());
        }

        Iterator<HttpCookie> i = cookieList.iterator();
        while (i.hasNext()) {
            HttpCookie c = i.next();
            if (c.hasExpired()) {
                i.remove();
            }
        }

        return cookieList;
    }

    @Override
    public synchronized List<URI> getURIs() {
        List<URI> uriList = new ArrayList<>();
        Iterator<Map.Entry<URI, List<HttpCookie>>> ii = cookieMap.entrySet().iterator();

        while (ii.hasNext()) {
            Map.Entry<URI, List<HttpCookie>> entry = ii.next();
            uriList.add(entry.getKey());
        }

        return uriList;
    }

    private URI cookiesUri(URI uri) {
        if (uri == null) {
            return null;
        }
        try {
            return new URI("https", uri.getHost(), null, null);
        } catch (URISyntaxException e) {
            return uri; // probably a URI with no host
        }
    }

    @Override
    public synchronized boolean remove(URI uri, HttpCookie cookie) {
        if (cookie == null) {
            throw new NullPointerException("cookie == null");
        }

        List<HttpCookie> cookies = cookieMap.get(cookiesUri(uri));
        if (cookies != null) {
            return cookies.remove(cookie);
        } else {
            return false;
        }
    }

    @Override
    public synchronized boolean removeAll() {
        int size = cookieMap.size();
        cookieMap.clear();

        return size != cookieMap.size();
    }

    private void add(List<HttpCookie> cookieList, HttpCookie cookie) {
        for (HttpCookie c : cookieList) {
            if (c.getName().equals(cookie.getName())) {
                cookieList.remove(c);
                cookieList.add(cookie);
                return;
            }
        }

        cookieList.add(cookie);
    }
}
