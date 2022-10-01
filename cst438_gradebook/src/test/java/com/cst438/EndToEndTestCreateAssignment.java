package com.cst438;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

/*
 * This example shows how to use selenium testing using the web driver 
 * with Chrome browser.
 * 
 *  - Buttons, input, and anchor elements are located using XPATH expression.
 *  - onClick( ) method is used with buttons and anchor tags.
 *  - Input fields are located and sendKeys( ) method is used to enter test data.
 *  - Spring Boot JPA is used to initialize, verify and reset the database before
 *      and after testing.
 *      
 *  In SpringBootTest environment, the test program may use Spring repositories to 
 *  setup the database for the test and to verify the result.
 */

@SpringBootTest
public class EndToEndTestCreateAssignment {

	public static final String CHROME_DRIVER_FILE_LOCATION = "../../../../downloads/chromedriver";
	public static final String URL = "http://localhost:3000";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int SLEEP_DURATION = 1000; // 1 second.
	public static final String TEST_ASSIGNMENT_NAME = "Test Assignment";
	public static final String TEST_COURSE_TITLE = "Test Course";
	public static final String TEST_CREATE_ASSIGNMENT_LINK_TEXT = "ADD ASSIGNMENT";

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentRepository assignmentRepository;

	@Test
	public void addAssignmentTest() throws Exception {

//		Database setup:  create course		
		Course c = new Course();
		c.setCourse_id(99999);
		c.setInstructor(TEST_INSTRUCTOR_EMAIL);
		c.setSemester("Fall");
		c.setYear(2021);
		c.setTitle(TEST_COURSE_TITLE);
		
		Date dueDate = new java.sql.Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);

		courseRepository.save(c);

		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on
		
		/*
		 * initialize the WebDriver and get the home page. 
		 */

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);
		

		try {
			/*
			* locate button to add assignment
			* 
			* To select a radio button in a Datagrid display
			* 1.  find the elements in the assignmentName column of the data grid table.
			* 2.  locate the element with test assignment name and click the input tag.
			*/
			WebElement addAssignmentElement  = driver.findElement(By.id("add-assignment-link-button"));
			System.out.println(addAssignmentElement.getText());
			boolean foundAddAssignmentElement = false;
			if (addAssignmentElement.getText().equals(TEST_CREATE_ASSIGNMENT_LINK_TEXT)) {
				foundAddAssignmentElement = true;
			}
		
			// Found the link we were looking for.
			assertTrue(foundAddAssignmentElement, "Unable to locate ADD ASSIGNMENT link in the homepage.");
			
			// Click the link and wait for navigation.
			addAssignmentElement.click();
			Thread.sleep(SLEEP_DURATION);
			
			
			// Find the inputs on the form and enter data.
			WebElement assignmentNameInputElement = driver.findElement(By.id("assignment-name-input"));
			WebElement assignmentCourseInputElement = driver.findElement(By.id("course-id-input"));
			assignmentNameInputElement.sendKeys(TEST_ASSIGNMENT_NAME);
			assignmentCourseInputElement.sendKeys(Integer.toString(c.getCourse_id()));

			/*
			 *  Locate and click the button to add the assignment to the database.
			 */
			
			WebElement addAssignmentSubmitButton = driver.findElement(By.id("add-assignment-submit-button"));
			addAssignmentSubmitButton.click();
			Thread.sleep(SLEEP_DURATION);


			// Get the first row contents for each cell and test that they hold the correct value.			
			WebElement assignmentNameElement = driver.findElement(By.xpath("//div[@data-field='assignmentName' and @role='cell']"));
			assertTrue(assignmentNameElement.getText().equals(TEST_ASSIGNMENT_NAME));
			WebElement courseTitleElement = driver.findElement(By.xpath("//div[@data-field='courseTitle' and @role='cell']"));
			assertTrue(courseTitleElement.getText().equals(TEST_COURSE_TITLE));
			WebElement courseDueDateElement = driver.findElement(By.xpath("//div[@data-field='dueDate' and @role='cell']"));
			assertTrue(courseDueDateElement.getText().equals(dueDate.toString()));
			
			Thread.sleep(SLEEP_DURATION);

		} catch (Exception ex) {
			throw ex;
		} finally {

			/*
			 *  clean up database so the test is repeatable.
			 */
			assignmentRepository.deleteAll();
			courseRepository.deleteAll();
			

			driver.quit();
		}

	}
}