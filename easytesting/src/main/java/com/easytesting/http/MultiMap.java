package com.easytesting.http;

import java.util.ArrayList;

import java.util.HashMap;

import java.util.List;

import java.util.Map;



import org.slf4j.Logger;

import org.slf4j.LoggerFactory;



/**

 * <code>MultiHashMap</code> is the default implementation of the

 * {@link org.apache.commons.collections.MultiMap MultiMap} interface.

 * <p>

 * A <code>MultiMap</code> is a Map with slightly different semantics.

 * Putting a value into the map will add the value to a List at that key.

 * Getting a value will return a List, holding all the values put to

 * that key.

 * <p>

 * This implementation uses an <code>ArrayList</code> as the collection.

 * The internal storage list is made available without cloning via the

 * <code>get(Object)</code> and <code>entrySet()</code> methods.

 * The implementation returns <code>null</code> when there are no values

 * mapped to a key.

 * <p>

 * For example:

 * <pre>

 * MultiMap mhm = new MultiHashMap();

 * mhm.put(key, "A");

 * mhm.put(key, "B");

 * mhm.put(key, "C");

 * List list = (List) mhm.get(key);</pre>

 * <p>

 * <code>list</code> will be a list containing "A", "B", "C".

 *

 * @since Commons Lists 2.0

 * @version $Revision: 372999 $ $Date: 2008-02-11 00:10:43 +0000 (Mon,

 *          11 Feb 2008) $

 *

 * @author Christopher Berry

 * @author James Strachan

 * @author Steve Downey

 * @author Stephen Colebourne

 * @author Julien Buret

 * @author Serhiy Yevtushenko

 * @author Robert Ribnitz



 * @NOTE This class has been modified to fit VC API team's needs

 */
@Deprecated
public class MultiMap<K, V> extends HashMap<K, List<V>>

{

   private static final long serialVersionUID = -3677711429061380615L;

   private static final Logger log = LoggerFactory.getLogger(MultiMap.class);



   /**

    * Constructor

    */

   public

   MultiMap()

   {

      super();

   }



   /**

    * Gets the collection mapped to the specified key.

    *

    * @param key  the key to retrieve

    * @return the collection mapped to the key, null if no mapping

    */

   public List<V>

   get(Object key)

   {

      return super.get(key);

   }



   /**

    * Gets the size of the collection mapped to the specified key.

    *

    * @param key  the key to get size for

    * @return the size of the collection at the key, zero if key not in map

    */

   public int

   size(Object key)

   {

      List<V> coll = get(key);

      return coll == null ? 0 : coll.size();

   }



   /**

    * Adds the value to the collection associated with the specified key.

    * <p>

    * Unlike a normal <code>Map</code> the previous value is not replaced.

    * Instead the new value is added to the collection stored against the key.

    *

    * @param key  the key to store against

    * @param value  the value to add to the collection at the key

    * @return the new collection if the map changed and null if the map did

    * not change

    */

   public List<V>

   put(K key,

       V value)

   {

      List<V> coll = get(key);

      if (coll == null) {

         coll = createList(null);

         super.put(key, coll);

      }

      return coll.add(value) ? coll : null;

   }



   /**

    * Adds a collection of values to the collection associated with the

    * specified key.

    *

    * @param key  the key to store against

    * @param values  the values to add to the collection at the key, null

    *                ignored

    * @return the new collection if the map changed and null if the map did

    * not change

    */

   public List<V>

   put(K key,

       List<V> values)

   {

      List<V> coll = get(key);

      if (values != null && values.size() != 0) {

         if (coll == null) {

            coll = createList(values);

            if (coll.size() == 0) {

               coll = null;

            } else {

               super.put(key, coll);

            }

         } else if(!coll.addAll(values)) {

            coll = null;

         }

      }

      return coll;

   }



   /**

    * Checks whether the collection at the specified key contains the value.

    *

    * @param value  the value to search for

    * @return true if the map contains the value

    */

   public boolean

   containsValue(Object key,

                 Object value)

   {

      List<V> coll = get(key);

      return coll == null ? false : coll.contains(value);

   }



   /**

    * Removes a specific value from map.

    * <p>

    * The first occurrence of item is removed from the collection mapped to

    * the specified key. Other values attached to that key are unaffected.

    * <p>

    * If the last value for a key is removed, <code>null</code> will be returned

    * from a subsequent <code>get(key)</code>.

    *

    * @param key  the key to remove from

    * @param item  the value to remove

    * @return the value removed (which was passed in), null if nothing removed

    */

   public Object

   remove(Object key,

          Object item)

   {

      List<V> valuesForKey = get(key);

      if (valuesForKey == null) {

         item = null;

      } else {

         boolean removed = valuesForKey.remove(item);

         if (removed == false) {

            item = null;

         } else {

            /*

             * remove the list if it is now empty (saves space, and allows

             * equals to work)

             */

            if (valuesForKey.isEmpty()) {

               remove(key);

            }

         }

      }

      return item;

   }



   /**

    * Clear the map.

    * <p>

    * This clears each collection in the map, and so may be slow.

    */

   public void

   clear()

   {

      // For gc, clear each list in the map

      for (Map.Entry<K,List<V>> keyValuePair : super.entrySet()) {

         List<V> coll = keyValuePair.getValue();

         coll.clear();

      }

      super.clear();

   }



   /**

    * Creates a new instance of the map value List container.

    * <p>

    * This method can be overridden to use your own collection type.

    *

    * @param coll  the collection to copy, may be null

    * @return the new collection

    */

   private List<V>

   createList(List<V> coll)

   {

      return coll == null ? new ArrayList<V>() : new ArrayList<V>(coll);

   }



   /**

    * Main method to run and show how to use this class.

    *

    * @param args commandline input

    */

   public static void

   main (String... args)

   {

      MultiMap<String, String> map = new MultiMap<String, String>();

      List<String> list = new ArrayList<String>();

      list.add("1");

      list.add("2");

      list.add("1");

      map.put("a", list);

      map.put("b", "3");

      map.put("b", "4");





      log.info("\nPrint all vals");

      for (String name : map.keySet()) {

         List<String> lst = map.get(name);

         for (String n : lst) {

            System.out.print(n + " ");

         }

      }

      log.info("");

      log.info("\nPrint vals for a");

      for (String name : map.get("a")) {

         System.out.print(name + " ");

      }

      log.info("");

      log.info("\nSize " + map.size());

      log.info("\nKeysize " + map.size("b"));



      //map.remove("a","1");

      //map.clear();



      log.info("");

      log.info("ContainsKey b        " + map.containsKey("b"));

      log.info("ContainsKey c        " + map.containsKey("c"));

      log.info("ContainsValue ^list^ " + map.containsValue(list));

      log.info("ContainsValue 5      " + map.containsValue("5"));

      log.info("Containsvalue a,1    " + map.containsValue("a","1"));

      log.info("ContainsValue a,3    " + map.containsValue("a","3"));



      log.info("\nPriniting again ");

      for (String name : map.keySet()) {

         List<String> lst = map.get(name);

         for (String n : lst) {

            System.out.print(n + "   ");

         }

      }

   }



}


