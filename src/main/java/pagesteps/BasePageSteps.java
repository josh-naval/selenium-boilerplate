package pagesteps;

import org.openqa.selenium.WebDriver;

public abstract class BasePageSteps {
    protected static final String PAGE_URL = "https://www.phptravels.net/login";

    protected static WebDriver webDriver;

    /*
     *  put methods that will be used by all page steps on this class
     */

    public void open_php_travels_page() {
        webDriver.get(PAGE_URL);
    }
}
