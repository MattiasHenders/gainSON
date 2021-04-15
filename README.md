# gainSON - Open Source Workouts

![gainSON: Because you didn't tell me a better name in time.](assets/img/logo.svg)

Welcome to the **open source** library for workouts.
To get started go to [installation](#installation) to get a copy for yourself.

## Table of Contents

####  [Description](#description)  - Why make this? Who is it for?
####  [Installation](#installation) - How do I get this for my app?
####  [Roadmap](#roadmap) - Where is this project going?
####  [Contributors](#contributors) - Who helped out with this project?

___

### <a name="description"></a> Description

This repo is an effort for future developers to easily get a database of exercises in JSON format for any kind of development that may need it.

Developing apps is hard enough, you shouldn't waste time creating your own database of workouts. 

#### How the final product should work
```
mermaid

graph LR 
A((Start gainSON App)) --> B[Select options for JSON]
B --> C[Generate FULL JSON]
C -->E
B --> D[Generate CUSTOM JSON]
D --> E[Save JSON to system]
E --> F((Add JSON to App))
```
More features could be added, such as 
___

### <a name="installation"></a> Installation

Clone the repo:
```
Main.main()

...run it

View the JSON object in the console

(Currently working on a better way to get the JSON)
```
View the [roadmap](#roadmap) to see our plan to get to a better user experience
___

### <a name="contributors"></a> Contributors

We add devs with accepted pull requests. Also any meaningful contributions.  

<a href="https://github.com/MattiasHenders">
  <img src="https://contrib.rocks/image?repo=MattiasHenders/gainSON" />
</a>

<!-- Made with [contributors-img](https://contrib.rocks) -->
___


### <a name="roadmap"></a> Roadmap

These are the goals we hope to hit mostly in order. Create an issue if you want to add to this.

**Milestones**
 - ~~Create the basic JSON object format~~
 - ~~Create the database with exercises~~
 - ~~Generate a JSON from the SQL database~~
 - Add a solid amount of exercises, most workout apps have around 100 so we hope to exceed that
 - Host the database
 - Create an executable app *.exe* 
 - Host a website for quick downloads of the full and some custom JSONs
 - Create ionic plugins that access the database directly
 - Skip the Java executable and find a good way to access the database in a meaningful way

___

![gainSON: Because you didn't tell me a better name in time.](assets/img/logo.svg)

## Copyright 2021 - Mattias Henders
