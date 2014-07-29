package cop3530;

import java.util.Map;
import java.util.Iterator;

public class MyHashMap<KeyType, ValueType> implements Iterable<Map.Entry<KeyType, ValueType>>
{

    private HashFunction<? super KeyType> hash1;
    private HashFunction<? super KeyType> hash2;
    private static final int DEFAULT_ARRAY_SIZE = 11;
    private Node<KeyType, ValueType>[] arr = null;
    private int[] length;
    private int theSize = 0;

    public MyHashMap()
    {
        this( null, null );
    }

    public MyHashMap( HashFunction<KeyType> h1, HashFunction<KeyType> h2 )
    {
        doClear();
        hash1 = h1;
        hash2 = h2;
    }

    public void doClear()
    {
        theSize = 0;
        arr = new Node[ DEFAULT_ARRAY_SIZE ];
        length = new int[ DEFAULT_ARRAY_SIZE ];
    }

    public void rehash()
    {
        MyHashMap<KeyType, ValueType> bigger = new MyHashMap(hash1, hash2);

        int[] primes =
        {
            2, 3, 5, 7, 11
        };
        int number = arr.length * 2;
//        boolean Prime = false;
//        while ( !Prime )
//        {
//            for ( int prime : primes )
//            {
//                if ( number % prime != 0 )
//                    Prime = true;
//                if ( number % prime == 0 )
//                    Prime = false;
//            }
//            if ( Prime )
//                ++number;
//        }
        bigger.arr = new Node[ number ];
        bigger.length = new int[ number ];

        for ( Node<KeyType, ValueType> lst : arr )
        {
            for ( Node<KeyType, ValueType> p = lst; p != null; p = p.next )
            {
                bigger.put( p.key, p.value );
            }
        }
        arr = bigger.arr;
        length = bigger.length;
    }

    public int size()
    {
        return theSize;
    }

    public void clear()
    {
        doClear();
    }
    
    private int myHash( KeyType k, HashFunction<? super KeyType> hash )
    {
        if ( hash == null )
            return Math.abs( k.hashCode() % arr.length );
        else
            return Math.abs( hash.hashCode( k ) % arr.length );
    }

//    private int myHash2( KeyType k )
//    {
//        if ( hash2 == null )
//            return Math.abs( k.hashCode() % arr.length );
//        else
//            return Math.abs( hash2.hashCode( k ) % arr.length );
//    }

    public ValueType put( KeyType k, ValueType v )
    {
        if ( size() > arr.length )
            rehash();
        int whichList = myHash( k, hash1 );
        int whichList2 = myHash2( k );

        for ( Node<KeyType, ValueType> p = arr[ whichList]; p != null; p = p.next )
        {
            if ( p.key.equals( k ) )
            {
                ValueType old = p.value;
                p.value = v;
                return old;
            }
        }

        for ( Node<KeyType, ValueType> p = arr[ whichList2]; p != null; p = p.next )
        {
            if ( p.key.equals( k ) )
            {
                ValueType old = p.value;
                p.value = v;
                return old;
            }
        }

        if ( length[whichList] <= length[whichList2] )
        {
            arr[whichList] = new Node<>( k, v, arr[whichList] );
            ++length[whichList];
            ++theSize;
            return null;
        }
        else
        {
            arr[whichList2] = new Node<>( k, v, arr[whichList2] );
            ++length[whichList2];
            ++theSize;
            return null;
        }
    }

    public boolean remove( KeyType k )
    {
        int whichList = myHash( k );
        int whichList2 = myHash2( k );

        if ( arr[whichList] != null )
        {
            if ( arr[whichList].key.equals( k ) )
            {
                arr[ whichList] = arr[whichList].next;
                --length[whichList];
                --theSize;
                return true;
            }
            for ( Node<KeyType, ValueType> p = arr[whichList]; p.next != null; p = p.next )
            {
                if ( p.next.key.equals( k ) )
                {
                    p.next = p.next.next;
                    --length[whichList];
                    --theSize;
                    return true;
                }
            }
        }

        if ( arr[whichList2] != null )
        {
            if ( arr[whichList2].key.equals( k ) )
            {
                arr[ whichList2] = arr[whichList2].next;
                --length[whichList2];
                --theSize;
                return true;
            }
            for ( Node<KeyType, ValueType> p = arr[whichList2]; p.next != null; p = p.next )
            {
                if ( p.next.key.equals( k ) )
                {
                    p.next = p.next.next;
                    --length[whichList2];
                    --theSize;
                    return true;
                }
            }
        }

        return false;
    }

    public ValueType get( KeyType k )
    {
        int whichList = myHash( k );
        int whichList2 = myHash2( k );

        for ( Node<KeyType, ValueType> p = arr[whichList]; p != null; p = p.next )
        {
            if ( p.key.equals( k ) )
                return p.value;
        }
        
        for ( Node<KeyType, ValueType> p = arr[whichList2]; p != null; p = p.next )
        {
            if ( p.key.equals( k ) )
                return p.value;
        }
        return null;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder( "[ " );

        for ( Map.Entry<KeyType, ValueType> s : this )
        {
            System.out.println( s );
            sb.append( s + " " );
        }
        sb.append( "]" );

        return new String( sb );
    }

    public int[] getLengths()
    {
        int[] temp = new int[ 20 ];
        for ( int i = 0; i < length.length; ++i )
        {
            ++temp[length[i]];
        }
        return temp;
    }

    public Iterator<Map.Entry<KeyType, ValueType>> iterator()
    {
        return new Iterator<Map.Entry<KeyType, ValueType>>()
        {
            public boolean hasNext()
            {
                return current != null;
            }

            public Map.Entry<KeyType, ValueType> next()
            {
                final Node<KeyType, ValueType> theCurrent = current;
                current = current.next;

                if ( current == null )
                {
                    listNum++;
                    advanceToNewList();
                }

                return new Map.Entry<KeyType, ValueType>()
                {
                    public KeyType getKey()
                    {
                        return theCurrent.key;
                    }

                    public ValueType getValue()
                    {
                        return theCurrent.value;
                    }

                    public ValueType setValue( ValueType newValue )
                    {
                        ValueType oldValue = theCurrent.value;
                        theCurrent.value = newValue;
                        return oldValue;
                    }

                };
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

            Node<KeyType, ValueType> current;   // current node
            int listNum;                // current list #

//            @Override
//            public void remove()
//            {
//                throw new UnsupportedOperationException( "Not supported yet." );
//            }
        };
    }

    private static class Node<KeyType, ValueType>
    {

        Node( KeyType k, ValueType v, Node<KeyType, ValueType> n )
        {
            key = k;
            next = n;
            value = v;
        }

        public String toString()
        {
            return key + "=" + value;
        }
        KeyType key;
        ValueType value;
        Node<KeyType, ValueType> next;
    }
}
