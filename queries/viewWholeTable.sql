select * from Exercises as e 
join MuscleGroups as mg on e.exerciseID = mg.muscleGroupsID
join Locations as l on e.exerciseID = l.locationsID
join Tracking as t on e.exerciseID = t.trackingID
join Media as md on e.exerciseID = md.mediaID
