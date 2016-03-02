# Chronverna

A mobile-based game timer for the board game Caverna.

## Overview

Caverna is a great board game! But if you're anything like me, sometimes
you want to play a game where everyone doesn't take forever for their turns.
This app will help keep your friends in line.

Caverna runs on a phone and keeps track of turn order as well as how much time
each player has taken. It understands the starting player/worker-placement turn
order rules of Terra Mystica so you can rely on it to keep track of who should
be going at any given time. By tracking how much time each player has used, you
can make your friends take shots every seven minutes, or just make fun of them
for being slow.

## Installation

Chronverna is a website designed to be viewed as a full screen app on a mobile
device. To start using it, follow these steps:

1. On your mobile device, view the app at
   <http://dphilipson.github.io/chronverna>.

2. Save the page to your homescreen.

3. Open the page from your homescreen to get a fullscreen view.

4. It is recommended to lock rotation on your device while using this app. It
   only really works in portrait orientation so this will reduce some minor
   annoyances.

## Use

On the initial screen, enter names and colors for each player in the game and
press the button to start the game. In the game, on each players turn hand the
mobile device to that player. When they are done, they should press either
"Next Player", "Take Start Player", or "Grow Family" and then hand the mobile
device to the player whose turn is next. "Next Player" should be used for most
actions, while "Take Start Player" should be used when the player used their
turn on the "Starting Player" action and "Grow Family" should be used if the
player gained an additional family member that turn.

The buttons at the top give some control over the timing. The Pause button
stops the clock from running until it is pressed again to resume. The Undo
button moves back through the turn history, which is particularly useful when a
player accidentally presses "Next Player" instead of "Grow Family" or vice
versa.

## Dev Setup

To get an interactive development environment run:

    rlwrap lein figwheel dev test

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

You can run the tests by opening your browser at
[localhost:3449/test.html](http://localhost:3449/test.html). The test results
are shown in the console and in the color of the favicon, and are automatically
re-run whenever the source changes.

If `rlwrap` is not installed, then it can be omitted from the above command,
but you will lose some command line conveniences like history and arrow
navigation.

To clean all compiled files:

    lein clean

To create a production build run:

    lein cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL.

Copyright © 2016 David Philipson
