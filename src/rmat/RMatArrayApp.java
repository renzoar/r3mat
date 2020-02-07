/* 
 * Copyright 2020 Renzo Angles (http://renzoangles.com/)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rmat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Comparator;
import java.util.Random;

public class RMatArrayApp {

    private long nodes = 0;
    private long edges = 0;
    private long edges_final = 0;
    private int edge_type = 0;
    private int UNDIRECTED = 0;
    private int DIRECTED = 1;
    private int dist_type = 0;
    private int method = 0;
    private int data_format = 0;
    private int EDGELIST = 0;
    private int GRAPHML = 1;
    //R-MAT Probabilities
    double alpha = 0.6;
    double beta = 0.15;
    double gamma = 0.15;
    double delta = 0.10;
    private double offset1;
    private double offset2;
    private double offset3;
    private double offset4;
    Random rand = new Random();

    public RMatArrayApp(long nodes_number, int _edge_type, int distribution_type, int _method, int _format, long seed) {
        nodes = nodes_number;
        // tau = 2.0
        edges = (long) ((2.0 / 3.0) * nodes * Math.log(nodes) + (0.38481 * nodes));
        // tau = 2.5 -> m = 15/7 * n - 12/7 *sqrt(n)
        //edges = (long) ((15.0 / 7.0) * nodes - (12.0 / 7.0) * Math.sqrt(nodes));        
        // tau = 3.0 -> m = 3/2 * n - 1
        //edges = (long) ((3.0 / 2.0) * nodes - 1.0);        
        // tau = 3.5 -> m = 35/27*n -20/(27*n^1.5)
        //edges = (long) ((35.0 / 27.0) * nodes - (20.0 / (27 * Math.pow(nodes,1.5))));        
        // tau = 4,0 -> m = 6/5*n - 3/(5n)
        //edges = (long) ((6.0 / 5.0) * nodes - 3.0 / ( 5.0 * nodes));        
        this.edge_type = _edge_type;
        this.dist_type = distribution_type;
        if (this.dist_type == 0) {
            this.alpha = 0.25;
            this.beta = 0.25;
            this.gamma = 0.25;
            this.delta = 0.25;
        }
        this.method = _method;
        this.data_format = _format;
        if (seed != 0) {
            rand = new Random(seed);
        }
        System.out.println("Number of nodes: " + nodes);
        System.out.println("Estimated number of edges: " + edges);
    }

    public long nodesNumber() {
        return this.nodes;
    }

    public long edgesNumber() {
        return this.edges_final;
    }

    public void Run() {
        if (data_format == 0) {
            this.GenerateEdgeList();
        } else {
            this.GenerateGraphML();
        }
    }

    public void GenerateEdgeList() {
        try {
            String filename = "graph-" + nodes + "-" + edge_type + "-" + dist_type + "-" + method + ".txt";
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
            System.out.println("Writing edges ...");
            long itime = System.currentTimeMillis();
            if (this.edge_type == 0) {
                this.GenerateUndirectedEdges(writer);
            } else {
                this.GenerateDirectedEdges(writer);
            }
            writer.close();

            long etime = System.currentTimeMillis() - itime;

            File file = new File(filename);

            String line = "Final number of nodes: " + nodes + "\n";
            line += "Final number of edges: " + this.edges_final + "\n";
            line += "Execution time: " + etime + " ms \n";
            line += "Output file size: " + file.length() + " bytes \n";
            System.out.println(line);

            String logfile = "graph-" + nodes + "-" + edge_type + "-" + dist_type + "-" + method + ".log";
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logfile), "UTF-8"));
            writer.write(line);
            writer.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void GenerateGraphML() {
        long itime = System.currentTimeMillis();
        try {
            String filename = "graph-" + nodes + "-" + edge_type + "-" + dist_type + "-1.graphml";
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            writer.write("\n<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"");
            writer.write("\n\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
            writer.write("\n\txsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns");
            writer.write("\n\thttp://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">");
            if (this.edge_type == 1) {
                writer.write("\n\t<graph id=\"G\" edgedefault=\"directed\" >");
            } else {
                writer.write("\n\t<graph id=\"G\" edgedefault=\"undirected\" >");
            }
            String line;
            System.out.println("Writing nodes ...");
            for (int n = 0; n < nodes; n++) {
                line = "\n\t<node id=\"" + n + "\" />";
                writer.write(line);
            }

            System.out.println("Writing edges ...");
            if (this.edge_type == 0) {
                this.GenerateUndirectedEdges(writer);
            } else {
                this.GenerateDirectedEdges(writer);
            }

            writer.write("\n\t</graph>");
            writer.write("\n</graphml>");
            writer.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
        long etime = System.currentTimeMillis() - itime;
        System.out.println("Execution time: " + etime + "ms (" + etime / 1000 + "s)");

    }


    /*
    Method to generate an undirected graph 
     */
    private void GenerateUndirectedEdges(Writer writer) throws Exception {
        int e = 0;
        long missed_edges = 0;
        String line;
        Array array = this.GenerateDistribution((int) nodes, edges);
        int source_node = 0;
        int target_node = 0;
        while (source_node < array.size) {
            target_node = source_node + 1;
            while (target_node < array.size) {
                if (array.getElement(source_node) <= 0) {
                    break;
                }
                if (array.getElement(target_node) > 0) {
                    array.decreaseElement(source_node, 1);
                    array.decreaseElement(target_node, 1);
                    if (this.data_format == EDGELIST) {
                        line = source_node + " " + target_node + "\n";
                    } else {
                        line = "\n\t<edge id=\"" + e + "\" source=\"" + source_node + "\" target=\"" + target_node + "\"/>";
                    }
                    writer.write(line);
                    e++;
                }
                target_node++;
            }
            if (array.getElement(source_node) > 0) {
                missed_edges = missed_edges + array.getElement(source_node);
            }
            source_node++;
        }
        System.out.println("Missed edges: " + missed_edges);
        this.edges_final = e;
    }

    private void GenerateDirectedEdges(Writer writer) throws Exception {
        int e = 0;
        long missed_edges = 0;
        String line;
        Array array = this.GenerateDistribution((int) nodes, edges);
        int source_node = 0;
        int target_node;
        while (source_node < array.size) {
            long degree = array.getElement(source_node);
            target_node = source_node - 1;
            while (degree > 0 && target_node >= 0) {
                if (this.data_format == EDGELIST) {
                    line = source_node + " " + target_node + "\n";

                } else {
                    line = "\n\t<edge id=\"" + e + "\" source=\"" + source_node + "\" target=\"" + target_node + "\"/>";
                }
                writer.write(line);
                degree--;
                target_node--;
                e++;
            }
            target_node = source_node + 1;
            while (degree > 0 && target_node < array.size) {
                if (this.data_format == EDGELIST) {
                    line = source_node + " " + target_node + "\n";
                } else {
                    line = "\n\t<edge id=\"" + e + "\" source=\"" + source_node + "\" target=\"" + target_node + "\"/>";
                }
                writer.write(line);
                degree--;
                target_node++;
                e++;
            }
            if (degree > 0) {
                missed_edges = missed_edges + degree;
            }
            source_node++;
        }
        System.out.println("Missed edges: " + missed_edges);
        edges_final = e;
    }

    //method to build an arrays containing the distribution of degrees
    private Array GenerateDistribution(int N, long E) {
        Array array;
        if (this.method == 1) {
            array = new PrimitiveArray(N);
        } else {
            array = new HashArray(N);
        }
        long pair[];
        //R-MAT Probabilities
        double a = alpha;
        double b = beta;
        double c = gamma;
        double d = delta;
        double m = 0.25;
        double depth = Math.ceil(Math.log(N) / Math.log(2));
        offset1 = (m - a) / depth;
        offset2 = (m - b) / depth;
        offset3 = (m - c) / depth;
        offset4 = (m - d) / depth;

        //initialize array: each node have degree 1
        //this step could be avoided to obtain a disconnected graph
        if (this.edge_type == UNDIRECTED) {
            for (int i = 0; i < N; i = i + 2) {
                if ((i + 1) < N) {
                    array.setElement(i, 1);
                    array.setElement(i + 1, 1);
                } else {
                    array.setElement(i, 1);
                    array.setElement(0, 2);
                }
                E--;
            }
        } else {
            for (int i = 0; i < N; i++) {
                array.setElement(i, 1);
                E--;
            }
        }

        //generate the degrees
        int n1 = 0;
        int n2 = 0;
        for (int e = 0; e < E; e++) {
            do {
                pair = this.chooseEdge(1, 1, N, N, a, b, c, d);
                n1 = (int) pair[0] - 1;
                n2 = (int) pair[1] - 1;
            } while (array.getElement(n1) >= N - 1 || array.getElement(n2) >= N - 1);
            array.increaseElement(n1, 1);
            if (this.edge_type == UNDIRECTED) {
                array.increaseElement(n2, 1);
            }
        }
        return array;
    }

    Comparator<Long> comparator = new Comparator<Long>() {
        @Override
        public int compare(Long o1, Long o2) {
            return o2.compareTo(o1);
        }
    };

    //method that simulates the RMat partition method
    public long[] chooseEdge(long x1, long y1, long xn, long yn, double a, double b, double c, double d) {
        double r = rand.nextDouble();
        long pair[] = new long[2];

        if ((xn - x1) == 0 && (yn - y1) == 0) {
            pair[0] = x1;
            pair[1] = y1;
            return pair;
        }
        if ((xn - x1) == 0 && (yn - y1) == 1) {
            if (r < 0.5) {
                pair[0] = x1;
                pair[1] = y1;
            } else {
                pair[0] = x1;
                pair[1] = yn;
            }
            return pair;
        }
        if ((xn - x1) == 1 && (yn - y1) == 0) {
            if (r < 0.5) {
                pair[0] = x1;
                pair[1] = y1;
            } else {
                pair[0] = xn;
                pair[1] = yn;
            }
            return pair;
        }

        double ab = a + b;
        double abc = a + b + c;
        double new_a = Math.abs(a + offset1);
        double new_b = Math.abs(b + offset2);
        double new_c = Math.abs(c + offset3);
        double new_d = Math.abs(d + offset4);
        long halfx;
        long halfy;
        if (r < a) {
            halfx = (long) Math.floor((x1 + xn) / 2);
            halfy = (long) Math.floor((y1 + yn) / 2);
            return chooseEdge(x1, y1, halfx, halfy, new_a, new_b, new_c, new_d);
        } else if (r >= a && r < ab) {
            halfx = (long) Math.ceil((x1 + xn) / 2);
            halfy = (long) Math.floor((y1 + yn) / 2);
            return chooseEdge(halfx, y1, xn, halfy, new_a, new_b, new_c, new_d);
        } else if (r >= ab && r < abc) {
            halfx = (long) Math.floor((x1 + xn) / 2);
            halfy = (long) Math.ceil((y1 + yn) / 2);
            return chooseEdge(x1, halfy, halfx, yn, new_a, new_b, new_c, new_d);
        } else {
            halfx = (long) Math.ceil((x1 + xn) / 2);
            halfy = (long) Math.ceil((y1 + yn) / 2);
            return chooseEdge(halfx, halfy, xn, yn, new_a, new_b, new_c, new_d);
        }
    }

}
