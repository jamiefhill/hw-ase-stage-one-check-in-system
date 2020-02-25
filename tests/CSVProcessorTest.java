import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.io.File;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import CheckIn.CSVProcessor;
import CheckIn.CheckInIOException;

public class CSVProcessorTest {
	
	// Test file names
	private String validCSVFile = "bookings.csv";
	private String invalidCSVFile = "noBookings.csv";
	private String outputCSVFileName = "test.csv";
	// Expected row and column values for existing file
	private int expectedRows = 5;
	private int expectedCols = 4;
	// Expected row and column values for generated file contents
	private int outputExpectedRows = 2;
	private int outputExpectedCols = 3;
	// Column contents
	private String colOneA = "Col One A";
	private String colTwoA = "Col Two A";
	private String colThreeA = "Col Three A";
	private String colOneB = "Col One B";
	private String colTwoB = "Col Two B";
	private String colThreeB = "Col Three B";
	// Test output content varaible
	private ArrayList<String[]> outputCSV;
	// Test output lines one and two
	private String[] oneLine = {colOneA, colTwoA, colThreeA};
	private String[] twoLine = {colOneB, colTwoB, colThreeB};
	// Empty test content
	private ArrayList<String[]> emptyCSV = new ArrayList<String[]>();
	
	@Before
	public void beforeEach() {
		// Create a new array list to hold test output data
		outputCSV = new ArrayList<String[]>();
		// Add each of the string array lines to the array list
		outputCSV.add(oneLine);
		outputCSV.add(twoLine);
	}
	
	@Test
	public void testReadCSV() throws CheckInIOException {
		// Create a new csv processor object
		CSVProcessor csvProc = new CSVProcessor();
		// Read the test csv file
		ArrayList<String[]> csvResult = csvProc.parseCSVToStringArray(validCSVFile);
		// Assert that the expected number of rows are there
		assertEquals(csvResult.size(), expectedRows);
		// Remove one row from the file content array list
		String[] csvRow = csvResult.remove(0);
		// Assert the expected number of columns are there
		assertEquals(csvRow.length, expectedCols);
	}
	
	@Test(expected = CheckInIOException.class)
	public void testReadCSVFileNotFound() throws CheckInIOException {
		// Create a csv processor object
		CSVProcessor csvProc = new CSVProcessor();
		// Attempt to read a file that does not exist
		// the @Test expected syntax will catch the exception
		// and allow the test to pass
		csvProc.parseCSVToStringArray(invalidCSVFile);
		// If the file we try to read some how does exist, report this issue in a fail
		fail("The file that should not exist does exist, you should go and delete this");
	}
	
	@Test
	public void testWriteCSV() throws CheckInIOException {
		// Create a new csv processor object
		CSVProcessor csvProc = new CSVProcessor();
		// Attempt to write the test csv content out to a test file
		csvProc.parseStringArrayToCSV(outputCSVFileName, outputCSV);
		// Read the csv content back in via the csv processor
		ArrayList<String[]> readCSVTest = csvProc.parseCSVToStringArray(outputCSVFileName);
		// Assert the expected number of rows has been read in
		assertEquals(readCSVTest.size(), outputExpectedRows);
		// Extract one line of the read in test csv file
		String[] oneLine = readCSVTest.remove(0);
		// Assert that the expected number of columns are present
		assertEquals(oneLine.length, outputExpectedCols);
		// Get a file handle
		File file = new File(outputCSVFileName); 
		// Delete the test file as part of test clean up
		file.delete();
	}
	
	@Test
	public void testWriteBlankCSV() throws CheckInIOException {
		// Create a csv processor object
		CSVProcessor csvProc = new CSVProcessor();
		// Create a blank line test data variable
		ArrayList<String[]> blankLine = new ArrayList<String[]>();
		// Build 3 test lines, one blank
		String[] lineOne = {"One", "Two", "Three"};
		String[] lineTwo = {}; 
		String[] lineThree = {"One", "Two", "Three"};
		// Add them to our test file array list
		blankLine.add(lineOne);
		blankLine.add(lineTwo);
		blankLine.add(lineThree);
		// Attempt to write out our test file content
		csvProc.parseStringArrayToCSV(outputCSVFileName, blankLine);
		// Read out test file content back in
		ArrayList<String[]> readCSVTest = csvProc.parseCSVToStringArray(outputCSVFileName);
		// Assert that the read in file content is one 
		// less than the size of array that was written to file
		assertEquals(readCSVTest.size(), outputExpectedRows);
		// Extract the file line of the read in test file
		String[] oneLine = readCSVTest.remove(0);
		// Assert that the expected number of columns are present
		assertEquals(oneLine.length, outputExpectedCols);
		// Get a file handler for the test csv file
		File file = new File(outputCSVFileName); 
		// Delete the test file as part of test clean up
		file.delete();
	}
	
	@Test(expected = CheckInIOException.class)
	public void testWriteCSVFileNoContent() throws CheckInIOException {
		// Create a new csv processor object
		CSVProcessor csvProc = new CSVProcessor();
		// Attempt to write an empty csv file
		// The @Test expected syntax will catch
		// the thrown error and pass the test
		csvProc.parseStringArrayToCSV(outputCSVFileName, emptyCSV);
		// If the method above does not throw an error, fail the test
		fail("We wrote an empty csv file!");
	}

	@Test(expected = CheckInIOException.class)
	public void testWriteFileReadOnlyFail() throws CheckInIOException {
		try {
			// Try to create a new csv processor object
			CSVProcessor csvProc = new CSVProcessor();
			// Write out test content out to a specified test file
			csvProc.parseStringArrayToCSV(outputCSVFileName, outputCSV);
			// Get a file handler for the test file
			File file = new File(outputCSVFileName);
			// Set its properties to be read only
			file.setReadOnly();
			// Attept to write out the same test content
			// to the same test file. THe @Test expected
			// syntax will catch the thrown error and
			// pass the test.
			csvProc.parseStringArrayToCSV(outputCSVFileName, outputCSV);
			// If the file is written without error, fail the test
			fail("The file is still writable");
		} finally {
			// Finally, get a file handle for the test file
			File file = new File(outputCSVFileName); 
			// Delete the file.
			file.delete();
		}
	}
}
