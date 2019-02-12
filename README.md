# Android-Projects
These android proojects had been made when I was an newbie to android.
I'm sorry, part of features that utilize AWS may not work since I disabled my account on AWS.


<h1>1. Super Triathlon</h1>
<li>Last Update Date: 2017/01</li>
<li>Category: Action</li>
<li>Target: teenager, man</li>
<li>Language: Java</li>
<li>Tools: Android Studio and Photoshop elements 14</li>
<p>Summary:<br>
 The action game is made up Off-Road(portrait screen), Road(portrait screen) and 
 Sea(landscape screen), combines vertical scroll and horizontal scroll.
 "I have created that action game based on past work which works on PC that I have made 
 in school.
 I have learnt Activity cycle on Android with different orientation."
</p>

<p>Functions:<br>
 1. Ranking<br>
 When the registered plater gets into the ranking view, the best each record are automatically      
 updated, and then player can check own records on the ranking or delete own records.
 “I have reduced a workload of DynamoDB, Cognito and IAM by leveraging Amazon Mobile Hub
 and therefore I could have implemented the ranking system in a smooth way.”
 </p>
<p>2. Record<br>
 I have utilized local storage to store data or load data player recored in the game.
 In the meantime, I could've learnt SQLite as well.
</p>
<p>
3. Difficulty<br>
 Player can select a difficulty (Easy, Normal and Hard) to play the game.
 Different enemies show up by the difficulty player choosed.
</p>
<p>
Sum Up:<br>
 The work has been extended features and design from the past work that runs on PC, 
 but I had to modify source in order to adjust behaiviour to run on Android.
 Besides, as an Android developer I could love to learn knowledge about androiid, technics and process of development with Super Triathlon.
</p>



<h1>Harmony</h1>
<li>Last Update Date: 2017/01</li>
<li>Category: Music</li>
<li>Target: teenager, blind people</li>
<li>Language: Java</li>
<li>Tools: Android Studio, Photoshop elements 14 and WavePad Sound Editor</li>
<p>Summary:<br>
 I had created Harmony in leveraging API which are Voice Recognition and Text-To-Speech that  
 provided by Google.
 User can choose the BGM from 20 pieces.
 The Music game allows user to play the game by player's voice along with a rule of style player   
 selected.
</p>
<p>Features and Design:<br>
 1. Buttons Table<br>
  The design is for the blind or visually impaired.
  The purpose is to make better coverage to reduce time to look for buttons.
  First of all, to touch the button on bottom of the screen, and then expanding each button, 
  the table continues to rotate unless finger separate from area of the table.
  When either button is on top of the table as double-tapped, obtain process that depending   
  on the button you choosed.
</p>
<p>
2. Voice Recognition<br>
  Google provides Voice Recognition function.
  I have implemented the function because that API is essential to create an application focused on blind people.
  This function allows user to transition the current scene or control each function, 
  not only playing the game.
</p>
<p>
3. Text-To-Speech<br>
  I thought this API also must be implemented to create an application for the blind and 
  visually impaired.
</p>
<p>
4. Web viewing<br>
  I made the scene to introduce contributors whose music I used for BGM in the game.
</p>
<p>
5. Select Mode<br>
  There are playing styles which are Sound mode, Sentence mode and Association mode to play this game.<br>
  Sound:<br>
    In Prologue scene, player remembers each sound that associated with colour, and in play  
    scene, player listens to the sound and remembers, and makes correct answer to score points.
</p>
<p>
Sentence:<br>
    In Play scene, player remembers several colours spoken by speech engine and then player says 
    colours which player remembers.
    If player marks correct answer, gets the points.<br>
  Association:<br>
    Player remembers words that associated with colour, and in play scene, player makes exact  
    association words to colour.<br>
    If player marks correct answer, gets the points.<br>
</p>
<p>6. Select Difficulty Level<br>
  Player can select difficulty to play which are Easy, Normal and Hard.
  Kind of colour will be changed by the level you choosed.<br>
</p>
<p>
7. Select BGM<br>
  Player can select BGM to play the game from 20 pieces.
  All BGMs in the game are listed on ccMixter, and they all are copyright-free.
  These pieces' URL are in Credit scene.
</p>
<p>
Sum Up:<br>
  I concerned sound, the arrangement of buttons and accessibility to focus on the blind and visually   
  impaired rather than dealing with visible design.
  I have been considering about accessibility even now.
  When player resumes the application after a wrong action or arbitrary action, which node app should   
  obtain? How app should lead player to continue game in a smooth way?
  I have considered about above things while creating this application and I got some tips.
  Unfortunately, I could not get opinions from the blind or visually impaired, so it has been remaining  
  issues to accessibility.
  However, I'm confident that I have got several tips for creating the future application.
</p>
