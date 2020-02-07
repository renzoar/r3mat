# r3mat
R3MAT: A Rapid and Robust Graph Generator

R3MAT is a graph generator which is based on the R-MAT generation model.
Instead of using the adjacency matrix proposed by R-MAT, R3MAT uses an array containing the degree for each node. This approach reduces the memory required by the original implementation, and allows the generation of large graphs with small computers.

R3MAT is implemented in Java and distributed as an executable JAR file.

Usage
java -jar r3mat [options]

-n <integer> // Number of nodes (mandatory)

-t [0|1]     // Type of edges: 0 = undirected (default), 1 = directed

-d [0|1]     // Statistical distribution of edges: 0 = normal (default), 1 = powerlaw

-m [0|1|2]   // Generation method: 0 = Matrix (default), 1 = Primitive Array, 2 = Hash-
based Array

-f [0|1]     // Output data format: 0 = edge list (default), 1 = graphml

-s <integer> // Random seed
