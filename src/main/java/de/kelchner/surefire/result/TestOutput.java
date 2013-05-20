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

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributedOutput", propOrder = {
	"message",
    "type",
    "output"
})
public class TestOutput {
	@XmlAttribute(required=false)
	private String type;
	@XmlAttribute(required=false)
	private String message;
	@XmlValue
	private String output;
	
	public TestOutput() {
	}
	
	public TestOutput(Throwable t) {
		this(t.getMessage(), t);
	}

	public TestOutput(String message, Throwable t) {
		this(t.getClass().getName(), message, stackTraceToString(t));
	}
	
	public TestOutput(String type, String message, String output) {
		this.type = type;
		this.message = message;
		this.output = output;
	}

	public TestOutput(String message, String output) {
		this(null, message, output);
	}

	public TestOutput(String output) {
		this(null, null, output);
	}

	private static String stackTraceToString(Throwable t) {
		StringWriter stackTrace = new StringWriter();
		t.printStackTrace(new PrintWriter(stackTrace));
		return stackTrace.toString();
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
}
