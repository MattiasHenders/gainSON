/*===================
BEFORE EXECUTING 
1) CHANGE ALL VALUES
2) INCREMENT @ID
===================*/

declare @ID int
set @ID = 2

insert into Tracking values(@ID,
/*boolRepsSets*/
1,
/*boolBodyWeight*/
0,
/*boolWeights*/
1,
/*boolTimer*/
0,
/*boolStopwatch*/
0,
/*boolDistance*/
0);

insert into Locations values(@ID, 
/*boolAtGym*/
1,
/*boolAtHome*/
0,
/*boolOutside*/
0
);

insert into MuscleGroups values(@ID, 
/*triceps*/
0,
/*pectorals*/
0,
/*deltoids*/
0,
/*quads*/
1,
/*hamstrings*/
1,
/*lats*/
1,
/*traps*/
1
);

insert into Media values(@ID,
/*Youtube*/
'https://www.youtube.com/watch?v=op9kVnSso6Q'
);


insert into SpanishData values(@ID,
/*spanish name*/
'Peso Muerto',
/*spanish Description*/
'Text for spanish deadlift here'
);

insert into FrenchData values(@ID,
/*French name*/
'Soulevé de Terre',
/*French Description*/
'Text for french deadlift here'
);

insert into ForeignData values(@ID,
/*Spanish*/
@ID,
/*French*/
@ID);

insert into Exercises values(@ID, 
/*Name*/
'Deadlift',
/*Description*/
'A loaded barbell or bar is lifted off the ground to the level of the hips, torso perpendicular to the floor, before being placed back on the ground. This is one repetition.',
/*Difficulty*/
4.25,

@ID,@ID,@ID,@ID,@ID);

