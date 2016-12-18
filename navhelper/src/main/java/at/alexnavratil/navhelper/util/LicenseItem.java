package at.alexnavratil.navhelper.util;

/**
 * Created by alexnavratil on 12/02/16.
 */
public class LicenseItem {
    private String libraryName;
    private String developer;
    private String information;
    private String url;

    public LicenseItem(String libraryName, String developer, String information, String url) {
        this.libraryName = libraryName;
        this.developer = developer;
        this.information = information;
        this.url = url;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
