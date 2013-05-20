surefire-result-runner
======================

Runs a test-class as a suite that is constructed from a 
[Surefire test-result file](https://svn.apache.org/repos/asf/maven/surefire/trunk/maven-surefire-common/src/main/java/org/apache/maven/plugin/surefire/report/StatelessXmlReporter.java).
                                                                                                
```java
@RunWith(SurefireResultSuite.class)                                                        
public class SurefireSuite {                                                                    
                                                                                                
    @SurefireResultSource                                                          
    public static TestSuite loadSuite() throws JAXBException {                                  
        return JAXB.unmarshal(new File("surefire-results.xml"), TestSuite.class);               
    }
}
```
