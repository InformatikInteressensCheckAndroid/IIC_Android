package at.ac.htlstp.app.iic.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import at.ac.htlstp.app.iic.R;
import at.alexnavratil.navhelper.util.LicenseItem;
import at.alexnavratil.navhelper.util.LicenseView;

public class LicenseActivity extends AppCompatActivity {
    private LicenseView mLicenseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLicenseView = (LicenseView) findViewById(R.id.licenseView);

        mLicenseView.addLicense(new LicenseItem("Realm Java", "Realm", "Realm is a mobile database that runs directly inside phones, tablets or wearables.", "https://github.com/realm/realm-java"));
        mLicenseView.addLicense(new LicenseItem("Volley", "Google", "A network library this app uses for communication with the IIC API server.", "https://android.googlesource.com/platform/frameworks/volley/"));
        mLicenseView.addLicense(new LicenseItem("Android Support Library", "Google", "The Android Support Library developed by Google.", "http://developer.android.com/tools/support-library/index.html"));
        mLicenseView.addLicense(new LicenseItem("RxJava", "ReactiveX", "RxJava is a Java VM implementation of Reactive Extensions: a library for composing asynchronous and event-based programs by using observable sequences.", "https://github.com/ReactiveX/RxJava"));
        mLicenseView.addLicense(new LicenseItem("RxAndroid", "ReactiveX", "Android specific bindings for RxJava.", "https://github.com/ReactiveX/RxAndroid"));
        mLicenseView.addLicense(new LicenseItem("Butterknife", "Jake Wharton", "Field and method binding for Android views which uses annotation processing to generate boilerplate code for you.", "http://jakewharton.github.io/butterknife/"));
        mLicenseView.addLicense(new LicenseItem("CircleImageView", "Henning Dodenhof", "A fast circular ImageView perfect for profile images.", "https://github.com/hdodenhof/CircleImageView"));
        mLicenseView.addLicense(new LicenseItem("AdjustableImageView", "Sittiphol Phanvilai", "Correct the ImageView's adjustViewBounds behaviour on API Level 17 and below with AdjustableImageView", "https://github.com/nuuneoi/AdjustableImageView"));
        mLicenseView.addLicense(new LicenseItem("TextDrawable", "Aidan Follestad", "This light-weight library provides images with letter/text like the Gmail app.", "https://github.com/afollestad/TextDrawable"));
        mLicenseView.addLicense(new LicenseItem("Material Dialogs", "Aidan Follestad", "A beautiful, easy-to-use, and customizable dialogs API, enabling you to use Material designed dialogs down to API 8.", "https://github.com/afollestad/material-dialogs"));
        mLicenseView.addLicense(new LicenseItem("ThreeTen backport project", "ThreeTen", "Backport of functionality based on JSR-310 to Java SE 6 and 7.", "https://github.com/ThreeTen/threetenbp"));
        mLicenseView.addLicense(new LicenseItem("CircleProgress", "Bruce Lee", "This library draws the beautiful circle progress bar when opening a specific quiz result.", "https://github.com/lzyzsd/CircleProgress"));
        mLicenseView.addLicense(new LicenseItem("Jackson", "FasterXML, LLC", "Core part of Jackson that defines Streaming API as well as basic shared abstractions. The libraries jackson-databind and jackson-annotation are also in use in this app.", "https://github.com/FasterXML/jackson-core"));
        mLicenseView.addLicense(new LicenseItem("Arc90 Readability", "Arc90 Labs", "This awesome scripts make the information parts much more readable and optimized for mobile devices.", "https://code.google.com/archive/p/arc90labs-readability/"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
