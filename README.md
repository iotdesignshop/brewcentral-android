# brewcentral-android

BrewCentral is an Android Things-based controller for managing the process of creating great all-grain home brewed beer. 

This repository contains the Android source code and project for building the main BrewCentral controller.

## Overview

There isn't too much to know about the project itself. You should be able to load and compile it with Android Studio and the associated Android SDK.

At the time that we created BrewCentral, it was written and compiled against SDK Version 26, with version 26.0.2 of the Android Build Tools. However, it is not overly complex, and we anticipate it should be forward compatible for the most part with new versions.

## Target Hardware

The current build is targeted at the TechNexion PICO-PI-IMX7-STARTKIT platform. It comes with a display and the PICO-PI-IMX7 board. The display is 800x480, so many of the layouts and views have been (unintentionally) optimized for that. Your mileage may vary a bit on a different screen or resolution.

## Architecture

Overall, this is a pretty simple project, most of the key logic lives in the MainActivity class with several fragments and support managers wired in to provide access to the real-time system hosted on Arduino via UART communication.

## Recipe

The data structure for recipes is contained in the BeerRecipe class. Currently, the system just has a single hard-coded recipe for testing, but the system is data-driven and intended to be extended. We'll get to that next, and hope to make an Editor and Cloud Service for publishing recipes.

