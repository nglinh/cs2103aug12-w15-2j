package test.logic;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AddTest.class, DeleteTest.class, EditTest.class,
		LogicTest.class })
public class LogicTests {

}
