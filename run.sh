#!/bin/bash

# Compile all Java files
javac -d bin src/grid/Grid.java src/algorithms/AStarPathfinding.java src/algorithms/parallel/ParallelizedAStarPathfinding.java src/Main.java src/algorithms/parallel/ParallelizedBidirectional.java

# Run the Main class
java -cp bin Main