package qataker;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.ous.jtoml.ParseException;

public class SlagalicaBot { 
	// A program that takes questions and answers from slagalica.tv and exports them as a .json file.
	// These questions and answers are used in an android game that I am developing with my friend.
	// You can check a video presentation of a program at this link https://youtu.be/MQoqNX4J7k8

	
	static WebDriver driver;
	static String buttonBezRezultataId = "div5";
	static String endGameButton = "/html/body/div[12]/div[3]/button";
	static String okButton = "/html/body/div[%d]/div[3]/button";
	public static void main(String[] args) throws InterruptedException, ParseException, IOException, java.text.ParseException {
		
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.manage().timeouts().scriptTimeout(Duration.ofMinutes(2));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
		driver.manage().window().maximize();

		driver.get("http://www.slagalica.tv/korisnik/prijava");
		login();
		
	
		String gameDate = "2020-09-03"; // Start date
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(gameDate));
		PrintWriter writer = new PrintWriter("d:/export/questionsandanswers.json","UTF-8");
		writer.println("{ 'questions' : [");

		for (int k = 0; k < 1; k++) {

			openGame(sdf.format(calendar.getTime()));
			calendar.add(Calendar.DATE, 1);

			List<QuestionsAndAnswers> qaList = new ArrayList<QuestionsAndAnswers>();
			for (int i = 0; i < 10; i++) {
				qaList.add(getQuestionsAndAnswersFromGame(i));
				driver.findElement(By.xpath(String.format(okButton, 2 + i))).click();
				Thread.sleep(1000);
			}

			for (QuestionsAndAnswers qa : qaList) {
				qa.print();

			}

			driver.findElement(By.xpath(endGameButton)).click();
			writeToJson(qaList, writer);
		}
			writer.println("]}");
			writer.close();
	}

	public static void login() throws InterruptedException {

		String fieldKorisnickoIme = "openid";
		String fieldLozinka = "lozinka";
		String username = "test_igrac123";
		String password = "test_lozinka123";
		String buttonPrijaviSe = "/html/body/div/div/div[4]/div[2]/div[1]/form/div/div[2]/p/input";

		driver.findElement(By.name(fieldKorisnickoIme)).sendKeys(username);
		Thread.sleep(500);
		driver.findElement(By.name(fieldLozinka)).sendKeys(password);
		Thread.sleep(500);
		driver.findElement(By.xpath(buttonPrijaviSe)).click();
		Thread.sleep(500);

	}

	public static void openGame(String dateText) throws InterruptedException {

		String buttonKoZnaZna = "/html/body/div/div/div[3]/div/ul/li[6]/a";
		String buttonStart = "/html/body/div/div/div[4]/div[2]/div[1]/p[5]/button";

		driver.get("http://www.slagalica.tv/game/slagalica/" + dateText);
		Thread.sleep(500);
		driver.findElement(By.xpath(buttonKoZnaZna)).click();
		Thread.sleep(500);
		driver.findElement(By.xpath(buttonStart)).click();
		Thread.sleep(500);

	}

	public static QuestionsAndAnswers getQuestionsAndAnswersFromGame(int i) throws InterruptedException {

		QuestionsAndAnswers qa = new QuestionsAndAnswers();
		
		qa.question = driver.findElement(By.id("pitanje")).getText();
		qa.answer1 = driver.findElement(By.id("div1")).getText();
		qa.answer2 = driver.findElement(By.id("div2")).getText();
		qa.answer3 = driver.findElement(By.id("div3")).getText();
		qa.answer4 = driver.findElement(By.id("div4")).getText();
		driver.findElement(By.id(buttonBezRezultataId)).click();
		Thread.sleep(1000);
		String correctAnswerString = driver.findElement(By.xpath("/html/body/div[" + (2 + i) + "]/div[2]")).getText();
		qa.correctAnswer = correctAnswerString.substring(18);
		return qa;
	}
	
	public static void writeToJson(List<QuestionsAndAnswers> qaList, PrintWriter writer) throws IOException {
		

		for (QuestionsAndAnswers myQa : qaList) {
			writer.println(myQa.toString() + ",");
			
		}
		
	}
	
}


class QuestionsAndAnswers {

	String question;
	String answer1;
	String answer2;
	String answer3;
	String answer4;
	String correctAnswer;
	
	public static String charReplace(String rawString){
		return rawString.replace("'", "\\'").replace("\"", "\\\"");
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"q\" : ").append('"'+charReplace(question)+"\",");
		sb.append("\"ca\" : ").append('"'+charReplace(correctAnswer)+"\",");
		sb.append("\"answers\" : ").append("[").append("\"").append(charReplace(answer1)).append("\",\"").append(charReplace(answer2)).append("\",\"").append(charReplace(answer3)).append("\",\"").append(charReplace(answer4)).append("\"").append("]");
		sb.append("}");
		
		return sb.toString();
	}


	public void print() {

		System.out.println("Question: " + question);
		System.out.println("1th Answer: " + answer1);
		System.out.println("2th Answer: " + answer2);
		System.out.println("3th Answer: " + answer3);
		System.out.println("4th Answer: " + answer4);
		System.out.println("Correct Answer: " + correctAnswer);
	}
}