STAVOR stands for Spacecraft Three-dimensional Attitude Visualization using [Orekit](http://orekit.org/) library.

It is an application for smart-phones and tablets used to visualize the attitude and orbit information of a simulated spacecraft in three-dimensional and cartographic environments.

STAVOR allows you to learn and teach about space mechanics in new-generation devices, mobile and tactile.
But STAVOR can also enhance your space mission simulations with visualizations focusing on some not-so-intuitive properties of trajectories and attitude.

Features
========

2D View
-------
The 2D cartographic vizualization shows the sub-satellite trajectory as well as day/nigh separation.
It includes ground stations visibility circles but dynamically grow and shrink them depending on spacecraft altitude. This helps understand operational constraints in highly elliptical orbits.

3D View
-------
The 3D vizualization is spacecraft-centric.
It was designed to visualize attitude problems. Users see the bodies and their directions *around* the spacecraft, from its own perspective.
It is different from regular 3D space simulations that show the spacecraft flying around a central body.
In this mode, it is possible to display the evolution of many geometrical properties like angles between vectors or between planes.

Embedded demonstrations
-----------------------
There is a list of available missions where you can create and modify your space missions.

Remote simulation
-----------------
The visualization module can be connected to an external simulator through a network using sockets. The information expected to be received is the serialized Orekit object SpacecraftState, which includes all the information of the simulated spacecraft.

Dependencies
============

The simulator included in the application is powered by the open-source library Orekit.
