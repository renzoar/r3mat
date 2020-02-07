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
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Random;

public class RMatMatrixApp {

    private long N = 0;
    private int edge_type = 0;
    private int UNDIRECTED = 0;
    private int DIRECTED = 1;
    private int dist_type = 0;
    private int NORMAL = 0;
    private int POWERLAW = 1;
    private int data_format = 0;
    private long E = 0;
    private long E_final = 0;
    int matrix[][];
    //R-MAT Probabilities
    double alpha = 0.6; //0.6
    double beta = 0.15; //0.15
    double gamma = 0.15; //0.15
    double delta = 0.10; //0.10
    private double offset1;
    private double offset2;
    private double offset3;
    private double offset4;
    Random rand = new Random();

    public RMatMatrixApp(long _nodes_number, int _edge_type, int _distribution_type, int _format, long seed) {
        N = _nodes_number;
        E = (int) ((2.0 / 3.0) * N * Math.log(N) + (0.38481 * N));
        this.edge_type = _edge_type;
        this.dist_type = _distribution_type;
        if (this.dist_type == NORMAL) {
            this.alpha = 0.25;
            this.beta = 0.25;
            this.gamma = 0.25;
            this.delta = 0.25;
        }
        this.data_format = _format;
        if(seed != 0){
            rand = new Random(seed);
        }
        matrix = new int[(int) N][(int) N];
    }

    public long nodesNumber() {
        return this.N;
    }

    public long edgesNumber() {
        return this.E_final;
    }
    
    public void Run(){
        if(data_format == 0){
           this.GenerateEdgeList();
        }else{
           this.GenerateGraphML();
        }
    }

    public void GenerateEdgeList() {
        try {
            long itime = System.currentTimeMillis();
            String filename = "graph-" + N + "-" + edge_type + "-" + dist_type + "-0.txt";
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
            String line;
            System.out.println("Writing edges ...");
            long e = 0;

            //Initialize matrix: each node begins with degree = 1
            //this step could be removed to produce diconnected graphs
            if (this.edge_type == UNDIRECTED) {
                int i = 0;
                while (i < N) {
                    long pair[] = this.chooseEdge(1, 1, N, N, alpha, beta, gamma, delta);
                    int j = (int) pair[0] - 1;
                    if (matrix[i][j] == 0) {
                        matrix[i][j] = 1;
                        matrix[j][i] = 1;
                        i++;
                        e++;
                    }
                }
            } else {
                for (int i = 0; i < N; i++) {
                    long pair[] = this.chooseEdge(1, 1, N, N, alpha, beta, gamma, delta);
                    int j = (int) pair[0] - 1;
                    matrix[i][j] = 1;
                    e++;
                }
            }

            //Fill the matrix
            while (e <= E) {
                long pair[] = this.chooseEdge(1, 1, N, N, alpha, beta, gamma, delta);
                int n1 = (int) pair[0] - 1;
                int n2 = (int) pair[1] - 1;
                if (this.edge_type == UNDIRECTED && matrix[n1][n2] == 0 && matrix[n2][n1] == 0) {
                    matrix[n1][n2] = 1;
                    matrix[n2][n1] = 1;
                    e++;
                    line = n1 + " " + n2 + "\n";
                    writer.write(line);
                }
                if (this.edge_type == DIRECTED && matrix[n1][n2] == 0) {
                    matrix[n1][n2] = 1;
                    e++;
                    line = n1 + " " + n2 + "\n";
                    writer.write(line);
                }
            }
            writer.close();
            long etime = System.currentTimeMillis() - itime;

            line = "Number of nodes: " + N + "\n";
            line += "Number of edges: " + E + "\n";
            line += "Execution time: " + etime + " ms";
            System.out.println(line);
            this.E_final = e;

            String logfile = "graph-" + N + "-" + edge_type + "-" + dist_type + "-0.log";
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logfile), "UTF-8"));
            writer.write(line);
            writer.close();

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            return;
        }
    }

    public void GenerateGraphML() {
        long pair[];
        long itime = System.currentTimeMillis();
        try {
            String filename = "graph-" + N + "-" + edge_type + "-" + dist_type + "-0.graphml";
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
            for (int n = 0; n < N; n++) {
                line = "\n\t<node id=\"" + n + "\" />";
                writer.write(line);
            }

            System.out.println("Writing edges ...");

            long e = 1;
            int n1;
            int n2;
            while (e <= E) {
                pair = this.chooseEdge(1, 1, N, N, alpha, beta, gamma, delta);
                n1 = (int) pair[0] - 1;
                n2 = (int) pair[1] - 1;
                if (edge_type == 0 && matrix[n1][n2] == 0 && matrix[n2][n1] == 0) {
                    matrix[n1][n2] = 1;
                    matrix[n2][n1] = 1;                    
                    e++;
                    line = "\n\t<edge id=\"" + e + "\" source=\"" + n1 + "\" target=\"" + n2 + "\"/>";
                    writer.write(line);
                }
                if (edge_type == 1 && matrix[n1][n2] == 0) {
                    matrix[n1][n2] = 1;
                    e++;
                    line = "\n\t<edge id=\"" + e + "\" source=\"" + n1 + "\" target=\"" + n2 + "\"/>";
                    writer.write(line);
                }                
            }

            writer.write("\n\t</graph>");
            writer.write("\n</graphml>");
            writer.close();
            System.out.println("Number of nodes: " + N);
            System.out.println("Number of edges: " + E);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
        long etime = System.currentTimeMillis() - itime;
        System.out.println("Execution time: " + etime + "ms (" + etime / 1000 + "s)");
    }
    
    
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
