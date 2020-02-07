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

abstract class Array {
    
    protected long size = 0;
    
    public void Array(){
    }
    
    abstract long getElement(long index);
    abstract boolean setElement(long index, long value);
    abstract long increaseElement(long index, long value);
    abstract long decreaseElement(long index, long value);    
    abstract long size();    
    
}
