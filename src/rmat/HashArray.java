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

import java.util.HashMap;

public class HashArray extends Array {
    
    HashMap<Long,Long> array;
    
    public HashArray(long array_size){
        this.size = array_size;
        array = new HashMap<>();
    }

    @Override
    public long getElement(long index){
        if(index < size){
            return array.get(index);
        }
        throw new RuntimeException("Invalid index");
    }
    
    @Override
    public boolean setElement(long index, long value){
        if(index < size){
            array.put(index, value);
            return true;
        }
        throw new RuntimeException("Invalid index");
    }
    
    @Override
    public long size(){
        return size;
    }    
    
    @Override
    long increaseElement(long index, long value){
        if(index < size){
            long nvalue = array.get(index) + value;
            array.put(index, nvalue);
            return nvalue;
        }
        throw new RuntimeException("Invalid index");
    }
    
    @Override
    long decreaseElement(long index, long value){
        if(index < size){
            long nvalue = array.get(index) - value;
            array.put(index, nvalue);
            return nvalue;
        }
        throw new RuntimeException("Invalid index");
    }    
    
}
