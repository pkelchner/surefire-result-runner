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

import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

class GroupingRunner extends ParentRunner<Runner> {
	private final String name;
	private final List<Runner> runners;

	protected GroupingRunner(String name, List<Runner> runners) throws InitializationError {
		super(null);
		
		this.name = name;
		this.runners = runners;
	}

	public static GroupingRunner namedGroup(String name, List<Runner> runners) {
		try {
			return new GroupingRunner(name, runners);
		} catch (InitializationError e) {
			// GroupingRunner must declare an InitializationError but should actually never 
			// throw one because it has no underlying TestClass that could be invalid
			throw new IllegalStateException(e);
		}
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