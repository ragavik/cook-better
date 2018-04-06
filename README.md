# Cook Better Bot

Cook Better is a Slack chat bot that recommends recipes based on available ingredients, and user's dietary and health restrictions.

## Demo

![cookbetter-1](https://user-images.githubusercontent.com/18286278/38170174-fd828e36-354b-11e8-8635-256bd4c3bd20.gif)
![cookbetter-2](https://user-images.githubusercontent.com/18286278/38170175-fd923232-354b-11e8-9847-fd213625ac31.gif)

## Slash Commands

### /personalize
<ul>
<li>Use this command to configure any food allergies, dietary restrictions, your health conditions and weight goals.
<li>You only have to do this once and we'll keep your selections in mind whenever you search for recipes!
</ul>

### /searchrecipes
<ul>
<li>Use this command to search for recipes based on the ingredients you have.
<li>You can also search recipes by recipe type, cooking time or find recipes for special occasions.
</ul>

### /surpriseme
<ul>
<li>This command suggests a random recipe every time (while keeping in mind your personalization criteria)!
</ul>

### /cookbetterhelp
<ul>
<li>Come back to this space in case you get stuck and to watch out for any new features we might add!
</ul>

## Note
<ul>
<li>To test the chat bot join this <a href = "https://join.slack.com/t/cookbetter/shared_invite/enQtMzI3ODczNDA0Mjc1LTE3ZTdjZmNkZGYzMGQzMTM3ZjFlNGRjZDc5ZmYwMjkwOTRjNzZhOWFjZjJlMDYyMzNiZTQ1MmQ1NzE1ZDdmNzU">workspace</a> and try out the slash commands.
<li>Evaluation results conducted for CSC 510 (Spring 2018) can be found <a href = "https://docs.google.com/spreadsheets/d/1fQGg2BpN0p5x_VaZ33qUwYhCCYA8SKHLrHzOEIC_n4E/edit#gid=1590162455">here</a>.
 </ul>

## Instructions
Follow these instructions to set up the development environment on your local machine.

### Prerequisites
<ul>
 <li>JDK
 <li>IntelliJ IDEA
 <li>Gradle
 <li>Amazon Web Services
 </ul>
 
 ### Development Environment
 
STEP 1: Download and install Java SE Development Kit from <a href="http://www.oracle.com/technetwork/java/javase/downloads/jdk9-downloads-3848520.html">here</a>.

STEP 2: Download and install Intellij IDEA from <a href="https://www.jetbrains.com/idea/download/#section=windows">here</a>.

STEP 3: Download Gradle build tool from <a href="https://gradle.org/releases/">here</a> and follow these <a href="https://gradle.org/install/">instructions</a> for installation.

STEP 4: Clone the repository to your local machine. Open the project in Intellij IDEA. While importing the project, make sure to point to the right Gradle installation folder on your machine.

### Run the Application
To run the application on your local machine, execute the following command in the terminal.

```
gradle build
```

Check out the service.

```
$ curl localhost:8080
```

### Deployment
Execute the following command in the terminal.
```
gradle bootRun
```
The WAR file for the project will be generated in the folder /build/libs.
Deploy the WAR file on Amazon Web Services by following these <a href="https://youtu.be/-ZYQQh8G01A?t=264">instructions</a>.
