package de.kelchner.surefire.junit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import de.kelchner.surefire.result.TestSuite;

@RunWith(SurefireResultSuite.class)
public class SurefireResultSuiteTest {
	@SurefireResultSource
	public static TestSuite loadSuite() throws JAXBException {
		InputStream suite = SurefireResultSuiteTest.class.getResourceAsStream("/suite.xml");
		
		return JAXB.unmarshal(new StreamSource(suite), TestSuite.class);
	}
	
	@BeforeClass
	public static void before() {
		TestA.executedCases.clear();
		TestB.executedCases.clear();
		TestC.executedCases.clear();
	}
	
	@AfterClass
	public static void after() {
		assertThat(TestA.executedCases.size(), is(2));
		assertTrue(TestA.executedCases.contains("a1"));
		assertTrue(TestA.executedCases.contains("a4"));

		assertThat(TestB.executedCases.size(), is(2));
		assertTrue(TestB.executedCases.contains("b2"));
		assertTrue(TestB.executedCases.contains("b4"));

		assertThat(TestC.executedCases.size(), is(1));
		assertTrue(TestC.executedCases.contains("c3"));
	}
}
