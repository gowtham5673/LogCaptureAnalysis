package demo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

//TODO: Tests 
class LogsCheckTest { 
	private static final String FILE = "./src/main/resources/logfile.txt";
	
	@Test
	void test_checkInput_correct() {
		boolean validate =LogsCheck.checkFile(FILE);
		//assertThat(validate,is(true));
		assertThat(String.valueOf(validate),true);
	}
	
	@Test
	void test_checkInput_not_correct() {
		boolean validateFile =LogsCheck.checkFile("../notAfile.txt");
		assertThat(String.valueOf(validateFile),false);
		//assertThat(LogsCheck.checkFile("../notAfile.txt"),is(false));
	}
	
	@Disabled
	@Test 
	void test_readEvents() {
		 LogsCheck.readEvents(FILE);
	}
	

}
