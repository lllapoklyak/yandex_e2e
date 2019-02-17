package tsedneva;

import net.bytebuddy.build.ToStringPlugin;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.awt.event.WindowEvent;
import org.openqa.selenium.NoSuchElementException;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FirstTest {
    private WebDriver driver;
    private EmailTemplate testemail = new EmailTemplate("lllapoklyaka@ya.ru", "topic of test email", "We are going to Mars");

    @Before
    public void setup(){
        System.setProperty ("webdriver.chrome.driver", "/Users/tsedneva/driver/chromedriver");
        driver=new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        driver.get("https://mail.yandex.ru");

    }

    @After
    public void tearDown(){
         driver.quit();
    }

    @Test
    public void validateDrafts() {
        Utils.login(driver, "tsedneva123","00454381");

        EmailTemplate mail1 = new EmailTemplate();
        EmailTemplate mail2 = new EmailTemplate("lllapoklyaka@ya.ru", "poligraf karpof", "mars");
        //Utils.sendEmailToMyself(driver);
        Utils.checkDrafts(driver, mail1);
    }

    @Test
    public void emailShouldSendWithCorrectAddress() {
        Utils.login(driver, "tsedneva123","00454381");
        Utils.sendEmail(driver, testemail);

        //TODO: Add Asserts
    }

    @Test
    public void emailShouldNotSendWithWrongAddress() {
        Utils.login(driver, "tsedneva123","00454381");
        Utils.wrongEmail(driver,"wrongEmail");
    }


    @Test
    public void receiveEmail() {
        //open and login
        Utils.login(driver, "lllapoklyaka","00454381Qq");

        Utils.receiveEmail(driver,testemail);


    }
    @Test
    public void replyEmail() {
        //open and login
        Utils.login(driver, "lllapoklyaka","00454381Qq");

        //open last email first
        WebElement openEmail= driver.findElement(By.cssSelector(".mail-MessagesList > div.mail-MessageSnippet-Wrap:first-child"));
        openEmail.click();

        //reply
        Utils.reply(driver, new EmailTemplate().body);
    }

    @Test
    public void deleteEmail() {
        //open and login
        Utils.login(driver, "lllapoklyaka","00454381Qq");

        //open last email first
        WebElement openEmail = driver.findElement(
                By.cssSelector(".mail-MessagesList > div.mail-MessageSnippet-Wrap:first-child"));
        String emailKey = openEmail.getAttribute("data-key");

        openEmail.click();

        // delete
        Utils.deleteEmail(driver);

        List<WebElement> emails = driver.findElements(
                By.cssSelector(".mail-MessagesList > div.mail-MessageSnippet-Wrap"));

        List<String> emailKeys = emails
            .stream()
            .map(el -> el.getAttribute("data-key"))
            .collect(Collectors.toList());

        Assert.assertFalse(emailKeys.contains(emailKey));

    }




}
