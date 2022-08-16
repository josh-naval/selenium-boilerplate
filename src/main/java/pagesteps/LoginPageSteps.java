package pagesteps;

import org.openqa.selenium.WebDriver;
import pages.LoginPage;
import static utilities.actions.SeleniumActions.*;

public class LoginPageSteps extends BasePageSteps {
    protected LoginPage loginPage;

    public LoginPageSteps(WebDriver webDriver) {
        super(webDriver);
        loginPage = new LoginPage(webDriver);
    }

    public void login() {
        type(waitForVisibilityOf(loginPage.txtEmail), "user@phptravels.com");
        type(loginPage.txtPassword, "demouser");
        click(waitToBeClickable(loginPage.btnLogin));
    }

}
