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
package de.kelchner.surefire.result;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.kelchner.surefire.result.adapters.SkippedAdapter;

/**
 * A single test-case result as it is generated by
 * <a href=
 * "https://svn.apache.org/repos/asf/maven/surefire/trunk/maven-surefire-common/src/main/java/org/apache/maven/plugin/surefire/report/StatelessXmlReporter.java"
 * >Maven Surefire</a>.
 * 
 * @see TestSuite
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TestCase", propOrder = {
    "classname",
    "skipped",
    "failure",
    "error",
    "systemOut",
    "systemError"
})
public class TestCase extends TestElement {
	@XmlAttribute(required=false)
	private String classname;
	@XmlJavaTypeAdapter(SkippedAdapter.class)
	@XmlElement(name="skipped", required=false)
	private Boolean skipped;
	@XmlElement(required=false)
	private TestOutput failure;
	@XmlElement(required=false)
	private TestOutput error;
	@XmlElement(name="system-out", required=false)
	private String systemOut;
	@XmlElement(name="system-err", required=false)
	private String systemError;
	
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	
	public TestOutput getFailure() {
		return failure;
	}
	public void setFailure(TestOutput failure) {
		this.failure = failure;
	}
	
	public TestOutput getError() {
		return error;
	}
	public void setError(TestOutput error) {
		this.error = error;
	}
	
	public String getSysOut() {
		return systemOut;
	}
	public void setSysOut(String systemOut) {
		this.systemOut = systemOut;
	}
	
	public String getSysErr() {
		return systemError;
	}
	public void setSysErr(String systemError) {
		this.systemError = systemError;
	}
	
	public boolean isSkipped() {
		return Boolean.TRUE.equals(skipped);
	}
	public void setSkipped(boolean skipped) {
		this.skipped = skipped;
	}	
}
