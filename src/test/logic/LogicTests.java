//@author A0081007U

package test.logic;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AddTest.class, DeleteTest.class, EditTest.class, ListTest.class, SearchTest.class,
		DoneTest.class, SortTest.class, UndoneTest.class, UndoTest.class, PostponeTest.class})
public class LogicTests {

}
