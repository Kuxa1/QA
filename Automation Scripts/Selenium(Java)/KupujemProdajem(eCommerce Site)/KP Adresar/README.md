# KupujemProdajem Automated Selenium Test

This project contains an end-to-end Selenium WebDriver test (in Java + TestNG) that:
1. Opens KupujemProdajem  
2. Applies filters (category, group, price, condition)  
3. Verifies that results > 1000  
4. Clicks the first listing  
5. Tries to add it to the address book  
6. Confirms the login modal appears  

## Prerequisites

- **Java JDK 24** installed   
- **`JAVA_HOME` environment variable** correctly set to point to your JDK 
- **Google Chrome** browser installed  

## Notes

> Make sure no Chrome instance is running before starting the test.
> If the website changes significantly, the test might need to be updated (e.g., new XPaths or filters).
