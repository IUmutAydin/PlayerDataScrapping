package optaAnalystTests.optaAnalystNavigatorTest;

import org.testng.annotations.Test;

import optaAnalystPages.LeaguePage;
import optaAnalystTests.baseTest.BaseTest;

public class OptaAnalystNavigatorTest extends BaseTest {

	@Test
	public LeaguePage getLeaguePage() {
		return oanp.getLeaguePage();
	}
}
