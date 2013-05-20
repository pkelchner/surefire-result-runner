package de.kelchner.surefire.junit;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class TestC {
	public static final Set<String> executedCases = new HashSet<String>();
	
	@Test
	public void case1() {
		executedCases.add("c1");
	}

	@Test
	public void case2() {
		executedCases.add("c2");
	}

	@Test
	public void case3() {
		executedCases.add("c3");
	}

	@Test
	public void case4() {
		executedCases.add("c4");
	}
}
