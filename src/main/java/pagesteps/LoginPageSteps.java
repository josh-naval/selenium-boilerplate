package pagesteps;

import org.openqa.selenium.WebDriver;
import pages.LoginPage;

public class LoginPageSteps extends BasePageSteps {
    protected LoginPage loginPage;

    public LoginPageSteps(WebDriver webDriver) {
        super.webDriver = webDriver;
        loginPage = new LoginPage(webDriver);
    }

    public void login() {
        loginPage.txtEmail.sendKeys("user@phptravels.com");
        loginPage.txtPassword.sendKeys("demouser");
        loginPage.btnLogin.click();
    }

}
