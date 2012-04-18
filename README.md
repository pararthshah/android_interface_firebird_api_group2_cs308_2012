[2012 - CS308  Group 2 : Project Android Interface - Firebird API](#)
================================================================

Group Info:
------------
+   Pararth Shah (09005009)
+   Aditya Ayyar (09005001)
+   Darshan Kapashi (09005004)
+   Siddhesh Chaubal (09005008)


Project Description
-------------------

We provide a library to android app developers for developing apps for the Firebird. We have created an android API with the aim of making the functionalities of firebird available to the entire android programming community and thereby allowing them to create apps for firebird without learning about the technical hardware details of firebird.

There is only 1 C code which is burnt on the Firebird.
There is only 1 Java Library which is to be imported by any app developer.
And now, you can build any app without worrying about the finer configuration details and giving commands in a more intuitive way.

We have also created 4 sample apps to illustrate the working of our interface as listed here:
1) Mapper Bot : The app makes the firebird go along the left wall of a room, and accepts signals from the bot about its movement(the forward distance covered and any turns taken) using which the android phone creates an approximate map of the room in which the bot has moved.
2) Peter Parker. The app makes the firebird move in a parking area, with parking slots to its left and when it senses an unoccupied parking slot, it parks there.
3) Mission Control. This app provides a simple gui to control the bot from the android device. The user can give commands to move the bot forward, backward, right, left. The sensor values at each instant of time are also relayed by the bot and printed on the android device.
4) FB Control. This app provides a gui to set and get different port values of the firebird.

Technologies Used
-------------------

+   Embedded C
+   JAVA
+   Android
+   Bluetooth Module (5V Serial UART)
+	Sharp Sensors (for sample apps)
+	Proximity Sensors (for sample apps)

Installation Instructions
=========================

Software required :

+	AVR Studio (http://developer.android.com/sdk/index.html)
+	Android 2.3.3 SDK (API 10)
+	Eclipse IDE with ADT Plugin (http://www.eclipse.org/downloads/)

Instructions :

Install the required software mentioned above. Download the zip file from here or clone the project. You should be able to import projects into Eclipse IDE and compile them

First import the Firebird API, then import the sample apps. Now connect the Android device via USB, and run the sample app on your device.

References
===========

+	Android Developer Reference (http://developer.android.com/reference/packages.html)
+	USB interface tutorial covering basic fundamentals bluetooth (http://www.eeherald.com/section/design-guide/esmod14.html)
+	An Introduction to Bluetooth Programming (http://people.csail.mit.edu/albert/bluez-intro/)
+	E-Yantra (http://www.e-yantra.org)
+	Swiss Army Knife Project (http://www.e-yantra.org/home/projects-wiki/item/153-firebirdv-atmega2560-swiss-knife)
