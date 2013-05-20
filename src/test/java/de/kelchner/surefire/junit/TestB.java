package de.kelchner.surefire.junit;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class TestB {
	public static final Set<String> executedCases = new HashSet<String>();
	
	@Test
	public void case1() {
		executedCases.add("b1");
	}

	@Test
	public void case2() {
		executedCases.add("b2");
	}

	@Test
	public void case3() {
		executedCases.add("b3");
	}

	@Test
	public void case4() {
		executedCases.add("b4");
	}
}
