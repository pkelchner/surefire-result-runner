package de.kelchner.surefire.junit;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class TestA {
	public static final Set<String> executedCases = new HashSet<String>();
	
	@Test
	public void case1() {
		executedCases.add("a1");
	}

	@Test
	public void case2() {
		executedCases.add("a2");
	}

	@Test
	public void case3() {
		executedCases.add("a3");
	}

	@Test
	public void case4() {
		executedCases.add("a4");
	}
}
