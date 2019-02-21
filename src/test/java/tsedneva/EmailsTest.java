package tsedneva;

import static org.junit.Assert.assertFalse;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/** Validates operations with emails. */
public final class EmailsTest {
    private WebDriver driver;
    private EmailTemplate testemail = new EmailTemplate(
            "lllapoklyaka@ya.ru", "topic of test email", "We are going to Mars");

    @Before
    public void setup(){
        driver = Utils.createWindow();
    }

    @After
    public void tearDown(){
         Utils.closeWindow(driver);
    }

    @Test
    public void saveInDrafts() {
        Utils.login(driver, config.login1,config.pass1);

        EmailTemplate mail1 = new EmailTemplate();
        Utils.checkDrafts(driver, mail1);
    }
/*
    @Test
    public void sendWithCorrectAddress() {
        Utils.login(driver, config.login1,config.pass1);
        Utils.sendEmail(driver, testemail);
    }
    @Test
    public void receiveEmail() {
        Utils.login(driver, config.login2,config.pass2);
        Utils.receiveEmail(driver,testemail);
    }
 */
    @Test
    public void sendAndReceiveEmail () {
        Utils.login(driver, config.login1,config.pass1);
        Utils.sendEmail(driver, testemail);

        //Utils.logout(driver, config.logout);
        Utils.closeWindow(driver);
        driver = Utils.createWindow();

        Utils.login(driver, config.login2, config.pass2);
        Utils.receiveEmail(driver, testemail);
    }

    @Test
    public void shouldNot_sendWithWrongAddress() {
        Utils.login(driver, config.login1,config.pass1);
        Utils.wrongEmail(driver,"wrongEmail");
    }


    @Test
    public void replyEmail() {
        //open and login
        Utils.login(driver, config.login2,config.pass2);

        WebElement openEmail= driver.findElement(By.cssSelector(
                ".mail-MessagesList > div.mail-MessageSnippet-Wrap:first-child"));
        openEmail.click();

        //reply
        Utils.reply(driver, new EmailTemplate().body);
    }

    @Test
    public void deleteEmail() {
        //open and login
        Utils.login(driver, config.login2,config.pass2);

        //open last email first
        WebElement openEmailButton = driver.findElement(
                By.cssSelector(".mail-MessagesList > div.mail-MessageSnippet-Wrap:first-child"));
        String emailKey = openEmailButton.getAttribute("data-key");

        openEmailButton.click();

        // delete
        Utils.deleteEmail(driver);

        List<WebElement> emails = driver.findElements(
                By.cssSelector(".mail-MessagesList > div.mail-MessageSnippet-Wrap"));

        List<String> emailKeys = emails
            .stream()
            .map(el -> el.getAttribute("data-key"))
            .collect(Collectors.toList());

        assertFalse(emailKeys.contains(emailKey));
    }
}
