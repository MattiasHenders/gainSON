select * from Exercises as e 
join MuscleGroups as mg on e.exerciseID = mg.muscleGroupsID
join Locations as l on e.exerciseID = l.locationsID
join Tracking as t on e.exerciseID = t.trackingID
join ForeignData as fd on fd.foreignDataID = e.exerciseID
join SpanishData as sd on fd.foreignDataID = sd.spanishID
join FrenchData as frd on fd.foreignDataID = frd.frenchID
join Media as md on e.exerciseID = md.mediaID
