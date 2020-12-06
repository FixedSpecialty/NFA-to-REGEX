# CS361P3

Project 3 - Regular Expressions
Andre Murphy and Josh Dixon

REGEX
This program uses the recursive descent algorithm(DFS) to construct an NFA given a REGEX.

HOW DOES IT WORK? 
Given a package of Java files, such as drivers and interfaces to work off, we had to create one class, which was the RE.java file. The class parses through the regex and assigns associated values to each character in the REGEX. It hands the symbols such as '(,),*,|' generally with the 4 basic methods eat, peek, next, and more. The class essentially takes a character and turns it into a state with relating transitions from the leaves of the descent to the root; bottom up.

USAGE 
This program is run through the REDriver.

This is used to link the jar file to your driver.
javac -cp ".:./CS361FA.jar" re/REDriver.java

Then, run the Driver with your test files.
java -cp ".:./CS361FA.jar" re.REDriver ./tests/p3tc1.txt

TESTING 
For testing we used custom .txt files to verify if out output was correct.