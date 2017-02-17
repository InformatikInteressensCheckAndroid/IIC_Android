package at.ac.htlstp.app.iic;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import at.ac.htlstp.app.cocolib.CocoLib;
import at.ac.htlstp.app.cocolib.CocoLibConfiguration;
import at.ac.htlstp.app.iic.parser.JsonMapParser;

/**
 * Created by alexnavratil on 29/12/15.
 */
public class CocoLibSingleton {
    public static final String URL = "https://iic.htlstp.ac.at/api/v1/";
    private static Map<Context, CocoLib> cocoLibMap = new HashMap<>();

    public static synchronized CocoLib getInstance(Context context) {
        //wenn noch keine Instanz gespeichert ist, oder die Instanz gecancelt wurde
        try {
            //URI uri = new URI(URL);
            if (!cocoLibMap.containsValue(context) || cocoLibMap.get(context).isDetached()) {

                // Load CAs from an InputStream
                // (could be from a resource or ByteArrayInputStream or ...)
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                // IIC HTTPS Certificate
                //new BufferedInputStream(new FileInputStream());
                InputStream caInput = context.getResources().openRawResource(R.raw.key);
                Certificate ca;
                try {
                    ca = cf.generateCertificate(caInput);
                    System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
                } finally {
                    caInput.close();
                }

                // Create a KeyStore containing our trusted CAs
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);

                // Create a TrustManager that trusts the CAs in our KeyStore
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                // Create an SSLContext that uses our TrustManager
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tmf.getTrustManagers(), null);

                URL certificateUrl = new URL(URL+"country/list");
                URL url = new URL(URL);
                //URLConnection urlConnection = url.openConnection();
                HttpsURLConnection urlConnection =
                        (HttpsURLConnection)certificateUrl.openConnection();
                urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                InputStream in = urlConnection.getInputStream();
                copyStream(in, System.out);
                cocoLibMap.put(context, new CocoLib(new CocoLibConfiguration(context, new JsonMapParser(), url)));
                return cocoLibMap.get(context);
            }
            //} catch (URISyntaxException e) {
            //    e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[1024]; // Adjust if you want
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

}