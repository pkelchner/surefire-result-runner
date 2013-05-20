/*
 * Copyright 2013 Patrick Kelchner
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.kelchner.surefire.junit;

import static java.lang.String.format;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import de.kelchner.surefire.result.TestCase;
import de.kelchner.surefire.result.TestElement;
import de.kelchner.surefire.result.TestSuite;

/**
 * Runs a test-class as a suite that is constructed from a {@linkplain TestSuite Surefire result}.
 * 
 *<pre>@RunWith(SurefireResultSuite.class)
 *public class SurefireSuite {
 *    
 *    &#64;{@link SurefireResultSource}
 *    public static TestSuite loadSuite() throws JAXBException {
 *        return JAXB.unmarshal(new File("surefire-results.xml"), TestSuite.class);
 *    }
 *}</pre>
 */
public class SurefireResultSuite extends ParentRunner<Runner> {
	private final String name;
	private final List<Runner> runners;
	
	public SurefireResultSuite(Class<?> testClass, RunnerBuilder builder) throws InitializationError {
		super(testClass);
		
		TestSuite suite = getTestSuite(testClass);
		name = suite.getName();
		runners = runnersFor(builder, suite);
	}

	protected TestSuite getTestSuite(Class<?> testClass) throws InitializationError {
		Method loadingMethod = getSingleStaticMethodAnnotatedWith(testClass, SurefireResultSource.class);
		
		if (!TestSuite.class.equals(loadingMethod.getReturnType())
				|| loadingMethod.getParameterTypes().length > 0) {
			
			throw new InitializationError(format(
					"method [%s] must have return type %s and no parameters",
					loadingMethod, TestSuite.class.getSimpleName()));
		}
		
		try {
			return (TestSuite) loadingMethod.invoke(null);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e); // impossible, public method
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException(e); // impossible, no parameters
		} catch (InvocationTargetException e) {
			throw new InitializationError(Collections.singletonList(e.getCause()));
		}
	}
	
	private Method getSingleStaticMethodAnnotatedWith(Class<?> cls, Class<? extends Annotation> a) throws InitializationError {
		if (cls == null) {
			return null;
		}
		
		Method result = null;
		
		for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
			for (Method m : cls.getDeclaredMethods()) {
				
				if (Modifier.isPublic(m.getModifiers()) 
						&& Modifier.isStatic(m.getModifiers())
						&& m.isAnnotationPresent(a)) {
					
					if (result != null) {
						throw new InitializationError(format(
								"[%s] must have exactly one public static method annotated with @%s." +
								"The class has more than one.",
								cls.getName(), a.getSimpleName()));
					}
					
					result = m;
				}
				
			}
			
			if (result != null) {
				return result;
			}
		}
		
		throw new InitializationError(format(
					"[%s] must have exactly one public static method annotated with @%s." +
					"The class has none.",
					cls.getName(), a.getSimpleName()));
	}

	private List<Runner> runnersFor(RunnerBuilder builder, TestSuite surefireSuite) throws InitializationError {
		List<Runner> runners = new ArrayList<Runner>();
		
		Map<String, Set<String>> casesOfTestClasses = new LinkedHashMap<String, Set<String>>();
		
		for (TestElement element : surefireSuite.getTestElements()) {
			if (element instanceof TestSuite) {
				TestSuite nestedSuite = (TestSuite) element;
				runners.add(GroupingRunner.namedGroup(nestedSuite.getName(), runnersFor(builder, nestedSuite)));
				
			} else if (element instanceof TestCase) {
				TestCase testCase = (TestCase) element;
				
				Set<String> testCases = casesOfTestClasses.get(testCase.getClassname());
				if (testCases == null) {
					testCases = new LinkedHashSet<String>();
					casesOfTestClasses.put(testCase.getClassname(), testCases);
				}
				
				testCases.add(testCase.getName());
			
			} else {
				throw new IllegalStateException("unhandleded element type "+element.getClass().getName());
			}
		}
		
		for (Entry<String, Set<String>> testCases : casesOfTestClasses.entrySet()) {
			
			try {
				Class<?> testClass = getTestClass(testCases.getKey());
				runners.add(selectedTestsOnly(builder, testClass, testCases.getValue()));
			} catch (ClassNotFoundException e) {
				throw new InitializationError(Collections.<Throwable>singletonList(e));
			}
		}
		
		return runners;
	}

	private static Runner selectedTestsOnly(final RunnerBuilder builder, final Class<?> testClass, final Set<String> testsToRun) 
			throws InitializationError  {
		
		Runner runner;
		try {
			runner = builder.runnerForClass(testClass);
		} catch (Throwable e) {
			throw new InitializationError(Collections.singletonList(e));
		}

		if (!(runner instanceof Filterable)) {
			throw new InitializationError(format("%s is not filterable", runner.getClass()));
		}
		
		try {
			((Filterable)runner).filter(new Filter() {
				@Override
				public boolean shouldRun(Description description) {
					return testsToRun.contains(description.getMethodName());
				}
				
				@Override
				public String describe() {
					return "a predetermined set of tests from test-class "+testClass.getName();
				}
			});
		} catch (NoTestsRemainException e) {
			throw new IllegalArgumentException("The set of tests does not match any methods from " + testClass.getName(), e);
		}
		
		return runner;
	}
	
	protected Class<?> getTestClass(String className) throws ClassNotFoundException {
		return Class.forName(className);
	}
	
	@Override
	protected String getName() {
		return name;
	}

	@Override
	protected List<Runner> getChildren() {
		return runners;
	}

	@Override
	protected Description describeChild(Runner child) {
		return child.getDescription();
	}

	@Override
	protected void runChild(Runner child, RunNotifier notifier) {
		child.run(notifier);
	}
	}
