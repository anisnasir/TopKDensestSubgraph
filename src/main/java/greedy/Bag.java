package greedy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class Bag<T> implements Iterable<T>
{
   ArrayList<T> list;  
   HashMap<T, Integer>  set;
 
   public Bag() {
       list = new ArrayList<T>();
       set = new HashMap<T, Integer>();
   }
   void add(T item ) {
      if(set.containsKey(item))
    	  return;
 
      int s = list.size();
      list.add(item);
 
      set.put(item,s);
   }
 
   void remove(T item)
   {
       if(!set.containsKey(item))
    	   return;
 
       int index = set.get(item);
       // If present, then remove element from hash
       set.remove(item);
 
       // Swap element with last element so that remove from
       // arr[] can be done in O(1) time
       int size = list.size();
       T last = list.get(size-1);
       Collections.swap(list, index,  size-1);
 
       // Remove last element (This is O(1))
       list.remove(size-1);
 
       // Update hash table for new index of last element
       set.put(last, index);
    }
 
    boolean contains(String x)
    {
       return set.containsKey(x);
    }
    public Iterator<T> iterator() {
    	return list.iterator();
    }
    int size() {
    	return list.size();
    }
}