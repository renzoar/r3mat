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
import rmat.RMatArrayApp;
import rmat.RMatMatrixApp;

public class r3mat {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage");
            System.out.println("java -jar r3mat [options]");
            System.out.println("-n <integer> // Number of nodes (mandatory)");
            System.out.println("-t [0|1]     // Type of edges: 0 = undirected (default), 1 = directed");
            System.out.println("-d [0|1]     // Statistical distribution of edges: 0 = normal (default), 1 = powerlaw");
            System.out.println("-m [0|1|2]   // Generation method: 0 = Matrix (default), 1 = Primitive Array, 2 = Hash-based Array");
            System.out.println("-f [0|1]     // Output data format: 0 = edge list (default), 1 = graphml");
            System.out.println("-s <integer> // Random seed");
            return;
        }

        long n = 0;
        int t = 0;
        int d = 0;
        int m = 0;
        int f = 0;
        long s = 0;
        int i = 0;
        while (i < args.length) {
            try {
                if (args[i].compareTo("-n") == 0) {
                    n = Long.parseLong(args[i + 1]);
                    if (n < 1) {
                        throw new IllegalArgumentException("Invalid value for -n");
                    }
                } else if (args[i].compareTo("-t") == 0) {
                    t = Integer.parseInt(args[i + 1]);
                    if (t < 0 || t > 1) {
                        throw new IllegalArgumentException("Invalid value for -t");
                    }
                } else if (args[i].compareTo("-d") == 0) {
                    d = Integer.parseInt(args[i + 1]);
                    if (d < 0 || d > 1) {
                        throw new IllegalArgumentException("Invalid value for -d");
                    }
                } else if (args[i].compareTo("-m") == 0) {
                    m = Integer.parseInt(args[i + 1]);
                    if (m < 0 || m > 2) {
                        throw new IllegalArgumentException("Invalid value for -m");
                    }
                } else if (args[i].compareTo("-f") == 0) {
                    f = Integer.parseInt(args[i + 1]);
                    if (f < 0 || f > 1) {
                        throw new IllegalArgumentException("Invalid value for -f");
                    }
                } else if (args[i].compareTo("-s") == 0) {
                    s = Long.parseLong(args[i + 1]);
                    if (s < 1) {
                        throw new IllegalArgumentException("Invalid value for -s");
                    }
                } else {
                    System.out.println("Error: Invalid parameters");
                    return;
                }
                i = i + 2;
            } catch (Exception ex) {
                System.out.println("Error: Invalid parameters");
                return;

            }
        }
        System.out.println("=== Parameters ===");
        System.out.println("-n = " + n);
        System.out.println("-t = " + t);
        System.out.println("-d = " + d);
        System.out.println("-m = " + m);
        System.out.println("-f = " + f);
        System.out.println("-s = " + s);
        System.out.println("=== Begin ===");        
        try {
            if (m == 0) {
                RMatMatrixApp rm = new RMatMatrixApp(n, t, d, f, s);
                rm.Run();

            } else {
                RMatArrayApp rm = new RMatArrayApp(n, t, d, m, f, s);
                rm.Run();
            }
        } catch (Exception ex) {
            System.out.println("Error during generation process");
            System.out.println(ex);
            return;
        }
        System.out.println("=== End ===");                
    }

}
