## Goal
This app is s an emotional mirror of how the user is feeling over period of time. 
It will track mood/energy it daily. We'll have an avatar that visualizes that internal state.

This app is meant to help people spot patterns in their mood and nudge towards positive behaviors.

## Features
### Starting state
 The first time user opens the app, it will take initial assessment.
1. Ask user to take three deep breaths and then assess how you're feeling on a scale of 1-5 with 1 being the worst, and 5 being most awesome.
2. Ask user to select avatar that best represents their inner self.
3. Explain that as they track their mood the avatar will change to represent their inner state.

### Home screen
* When user opens the app they see their avatar in a mirror. 
* They have a CTA to record their current mood on the same 1-5 scale.
* Avatar's "face" changes to represent that mood specifically.
* There's a timeline of last 1 week of moods that can be tapped to get full information.

### Avatars
1. Tree. There is a face in it that represents current mood and leaves that can turn yellow and shrivel if it feels worse.
2. Robot. with a face that represents current mood. The robot gets rust patches when the user feels bad for long durations.
3. Bucket of water. The color of water changes based on mood - clean blue for good, murky green-brown for bad. The level of water changes based on trend. 
4. Pile of spoons. Gets larger or smaller based on trends, with one spoon being the smallest value it could be.

### Trends
When there is at least one week's worth of data, the avatar starts changing. If the mood data is positive, it gets shinier and better. If the data is negative, it looks sad and more beaten up.
A value of one on the mood scale creates a big dip that takes multiple days of positive mood to return back to baseline. At the same time a value of five gives a big boost that lasts for a while.
If user's mood is less than 4 for multiple weeks in a row, it will eventually get to the lowest possible state. At that point there will be a counter that tracks how long the user has not been feeling well.
Any time a user has not been feeling well for longer than two weeks, it creates a permanent scar on their avatar. The scar can be tapped on and it will show the time period that it is associated with. 

### Daily check in
User is prompted to enter their mood on a scale of 1 to 5 daily. There is a CTA inside the app as well as a daily notification at a time that can be configured.
Alongside that number the user can add words indicating what contributed to their mood today. There is a free text field as well as suggestions. Suggestions include words like:
- work
- family
- relationships
- illness
- politics
  
The most commonly used for that mood are surfaced first.

### Past entries
There are two modes to view past entries:
1. A slider that will show your avatar state as well as the mood and any keywords for that date and you can go back and forth.
2. A calendar view that shows just your score and the user can tap into it to see the full values. 

They are able to edit any of the past entries and the results will be reflected on the condition of their avatar.

### Messages
Depending on the state of your avatar, the app shows different messages. If the mood is good they might make a joke or say something nice.
Most depth in the messages is when the mood of the user has not been good for a duration of time. 
The app will suggest some self-care activities and make it in the style of the avatar. 
For example for the tree it will say that it needs more water, sun, or fertilizer. 
For the robot it might say that it needs some maintenance and maybe oil and polish.

### Long inactivity
If the user has not recorded a new mood in the app for more than two weeks, there's a special frozen state that the avatar will go into, represented by cobwebs or a hibernation state. 
When the user comes back, they will be prompted to redo the initial assessment. 
In this case it will not reset the initial state but it will assume that the previous two weeks were all equal to their new assessment. 