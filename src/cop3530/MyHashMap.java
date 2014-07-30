/* File: MyHashMap.java
 I affirm that this program is entirely my own work and
 none of it is the work of any other person.

 @author Fernando Campo 1299228 COP 3530 Data Structures MWF 10:45 Summer 2014
 */
package cop3530;

import java.util.Map;
import java.util.Iterator;

/**
 * Implement a HashMap using separate chaining. Allow two hash functions and
 * always add new items to the shorter of the two lists suggested by the hash
 * functions. Provide logic that returns an array with the distribution of the
 * list lengths.
 *
 * @author Fernando
 * @param <KeyType> Generic Key type.
 * @param <ValueType> Generic Value type.
 */
public class MyHashMap<KeyType, ValueType>
                            implements Iterable<Map.Entry<KeyType, ValueType>>
{

    //private instance variables
    private HashFunction<KeyType> hash1;
    private HashFunction<KeyType> hash2;
    private static final int DEFAULT_ARRAY_SIZE = 11;
    private Node<KeyType, ValueType>[] arr; //hash table
    private int[] listSizes; //array to match table for list sizes
    private int theSize; //Node count

    /**
     * MyHashMap constructor with no hash function passed to it.
     */
    public MyHashMap()
    {
        this( null, null );
    }

    /**
     * MyHashMap constructor with two hash functions passed to it.
     *
     * @param h1 First Hash Function.
     * @param h2 Second Hash Function.
     */
    public MyHashMap( HashFunction<KeyType> h1, HashFunction<KeyType> h2 )
    {
        doClear();
        hash1 = h1;
        hash2 = h2;
    }

    /**
     * Returns the size of MyHashMap.
     *
     * @return Int size value of MyHashMap.
     */
    public int size()
    {
        return theSize;
    }

    /**
     * Method that clear the MyHashMap. Size is set to zero, hash table is set
     * to default, and list length array is set to default.
     */
    public void clear()
    {
        doClear();
    }

    /*
     * Private method to clear and set fields to default values.
     */
    private void doClear()
    {
        theSize = 0;
        arr = new Node[ DEFAULT_ARRAY_SIZE ];
        listSizes = new int[ DEFAULT_ARRAY_SIZE ];
    }

    /*
     * Private method used to rehash the table. Increases the size of the table
     * by trying to find a decent prime number to increases it to.
     */
    private void rehash()
    {
        //temp hash table
        MyHashMap<KeyType, ValueType> bigger = new MyHashMap( hash1, hash2 );

        int number = arr.length * 2;

        //trying to find a decent prime number
        while ( number % 2 == 0
                || number % 3 == 0
                || number % 5 == 0
                || number % 7 == 0
                || number % 11 == 0
                || number % 13 == 0
                || number % 17 == 0 )
        {
            ++number;
        }

        bigger.arr = new Node[ number ];
        bigger.listSizes = new int[ number ];

        //rehashing old table to new table
        for ( Node<KeyType, ValueType> lst : arr )
            for ( Node<KeyType, ValueType> p = lst; p != null; p = p.next )
                bigger.put( p.key, p.value );

        //current table is now pointing to new table
        arr = bigger.arr;
        listSizes = bigger.listSizes;
    }

    /*
     * Method used to compute hash table index.
     */
    private int myHash( KeyType k, HashFunction<KeyType> hash )
    {
        if ( hash == null )
            return Math.abs( k.hashCode() % arr.length );
        else
            return Math.abs( hash.hashCode( k ) % arr.length );
    }

    /**
     * Method for putting KeyType and ValueType to hash table. If a key is added
     * that already exists, the new value replaces the old value. put() returns
     * the prior value or null if this is the first occurrence of the key.
     *
     * @param k KeyType to be added.
     * @param v ValueType to be added.
     * @return Returns the prior value or null if this is the first occurrence
     * of the key.
     */
    public ValueType put( KeyType k, ValueType v )
    {
        //if size is to big, rehash.
        if ( size() > arr.length )
            rehash();

        //index computed my fist hash function.
        int whichList = myHash( k, hash1 );
        //index computed my fist hash function.
        int whichList2 = myHash( k, hash2 );

        //Values to be returned if not null
        ValueType value1;
        ValueType value2;

        //Calls the putSearch method. Method returns either the value if found 
        //or null if not found. Then checks if its not null, return that value.
        if ( ( value1 = putSearch( whichList, k, v ) ) != null )
            return value1;

        if ( ( value2 = putSearch( whichList2, k, v ) ) != null )
            return value2;

        //if the key was not found, its safe to add. Add key and value to
        //the shorter of the two lists.
        if ( listSizes[whichList] <= listSizes[whichList2] )
            putNode( whichList, k, v );
        else
            putNode( whichList2, k, v );

        return null;//key was not found and was added.
    }

    /*
     * Helper method used to search in the put(). Returns the value if found, or
     * null if not found.
     */
    private ValueType putSearch( int index, KeyType k, ValueType v )
    {
        for ( Node<KeyType, ValueType> p = arr[ index]; p != null; p = p.next )
        {
            if ( p.key.equals( k ) )
            {
                ValueType old = p.value;
                p.value = v;
                return old;
            }
        }
        return null;
    }

    /*
     * Helper method used to add the new Node to the front of the list for the
     * put().
     */
    private void putNode( int index, KeyType k, ValueType v )
    {
        arr[index] = new Node<>( k, v, arr[index] );
        ++listSizes[index];
        ++theSize;
    }

    /**
     * Method for removing the KeyType from the MyHashMap. Needs to search the
     * two locations where the key might be determine by the hash function. If
     * found, remove it and return true.
     *
     * @param k KeyType to be removed.
     * @return True if founded and removed. False if not.
     */
    public boolean remove( KeyType k )
    {
        int whichList = myHash( k, hash1 );
        int whichList2 = myHash( k, hash2 );

        //if found in the first list, return true. Else just return the results
        //of the second search. Uses the removeNode() helper method.
        return ( removeNode( whichList, k ) ) ? true : ( removeNode( whichList2, k ) );
    }

    /*
     * Helper method for the remove(). Returns true if key is found. Decrements
     * the sizes.
     */
    private boolean removeNode( int index, KeyType k )
    {
        //if list is not empty
        if ( arr[index] != null )
        {
            //if key is the first Node in the list.
            if ( arr[index].key.equals( k ) )//key found.
            {
                arr[ index] = arr[index].next;//first now point to the next.
                --listSizes[index];//decrement the size in the sizes array.
                --theSize;//decrement the size.
                return true;
            }
            //not the first, check the rest of the list.
            for ( Node<KeyType, ValueType> p = arr[index]; p.next != null; p = p.next )
            {
                if ( p.next.key.equals( k ) )//key found.
                {
                    p.next = p.next.next;
                    --listSizes[index];//decrement the size in the sizes array.
                    --theSize;//decrement the size.
                    return true;
                }
            }
        }
        return false; //not found.
    }

    /**
     * Method to return the value of the given key.
     *
     * @param k Key to search for.
     * @return Return the value of the given key.
     */
    public ValueType get( KeyType k )
    {
        //location of the two index key can be found.
        int whichList = myHash( k, hash1 );
        int whichList2 = myHash( k, hash2 );

        //values to be returned if found.
        ValueType value1 = getNode( whichList, k );
        ValueType value2 = getNode( whichList2, k );

        //if value1 is null, just return the results of value2.
        return ( value1 != null ) ? value1 : value2;
    }

    /*
     * Helper method used to find the key in the given index location. Returns
     * the value if the key is found. Returns null if not found.
     */
    private ValueType getNode( int index, KeyType k )
    {
        for ( Node<KeyType, ValueType> p = arr[index]; p != null; p = p.next )
        {
            if ( p.key.equals( k ) )
                return p.value;
        }
        return null;
    }

    /**
     * Method to return a String of the Hash Map. Uses the iterator to traverse
     * the hash table.
     *
     * @return Returns a string of the Hash Map.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder( "[ " );

        for ( Map.Entry<KeyType, ValueType> s : this )
        {
            System.out.println( s );
            sb.append( s );
            sb.append( " " );
        }
        sb.append( "]" );

        return new String( sb );
    }

    /**
     * Method that returns an array with the distribution of the list lengths.
     * The array will tell you how many lists have length 0, 1, 2, etc.
     *
     * @return Return an int array of the list lengths.
     */
    public int[] getLengths()
    {
        int[] temp = new int[ 31 ];
        for ( int i = 0; i < listSizes.length; ++i )
            ++temp[listSizes[i]];//increment the count of lists with i length.
        
        return temp;
    }

    /**
     * Iterator method tranverse the list.
     * @return Returns an iterator.
     */
    @Override
    public Iterator<Map.Entry<KeyType, ValueType>> iterator()
    {
        return new Iterator<Map.Entry<KeyType, ValueType>>()
        {
            Node<KeyType, ValueType> current;   // current node
            int listNum = 0;                // current list #

            @Override
            public boolean hasNext()
            {
                return current != null;
            }

            @Override
            public Map.Entry<KeyType, ValueType> next()
            {
                final Node<KeyType, ValueType> theCurrent = current;
                current = current.next;

                if ( current == null )
                {
                    listNum++;
                    advanceToNewList();
                }

                Map.Entry<KeyType, ValueType> nextItem = new Map.Entry<KeyType, ValueType>()
                {
                    @Override
                    public KeyType getKey()
                    {
                        return theCurrent.key;
                    }

                    @Override
                    public ValueType getValue()
                    {
                        return theCurrent.value;
                    }

                    @Override
                    public ValueType setValue( ValueType newValue )
                    {
                        ValueType oldValue = theCurrent.value;
                        theCurrent.value = newValue;
                        return oldValue;
                    }
                };
                return nextItem;
            }

            private void advanceToNewList()
            {
                while ( listNum < arr.length && arr[ listNum] == null )
                {
                    listNum++;
                }

                if ( listNum != arr.length )  // current is already null
                    current = arr[ listNum];
            }
            
            {  // instance initializer (like a constructor)
                advanceToNewList();
            }
        };
    }//end of iterator()

    /*
     * Inner class for the Node.
     */
    private static class Node<KeyType, ValueType>
    {

        KeyType key;
        ValueType value;
        Node<KeyType, ValueType> next;

        Node( KeyType k, ValueType v, Node<KeyType, ValueType> n )
        {
            key = k;
            next = n;
            value = v;
        }

        @Override
        public String toString()
        {
            return key + "=" + value;
        }
    }//end of inner Node class.
}//end of MyHashMap.java
