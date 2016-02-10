package gov.nist.appvet.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

public class HashTableTest {


	public HashTableTest() {
		
	}
	
	public void runit() {
		Hashtable<String, ArrayList<String>> hash = 
				new Hashtable<String, ArrayList<String>>();
		
		String key = "test";
		ArrayList<String> list = new ArrayList<String>();
		list.add("athing1");
		list.add("cthing2");
		list.add("athing1");
		list.add("bthing3");
		
		for (int i = 0; i < list.size(); i++) {
			System.out.println("list1(" + i + "): " + list.get(i));
		}
		
		hash.put(key, list);
		
		ArrayList<String> list2 = hash.get(key);
		for (int i = 0; i < list2.size(); i++) {
			System.out.println("list2(" + i + "): " + list2.get(i));
		}
		
		list2.add("dthing4");
		
		ArrayList<String> list3 = hash.get(key);
		
		   Collections.sort(list3, new Comparator<String>() {
		        @Override
		        public int compare(String s1, String s2) {
		            return s1.compareToIgnoreCase(s2);
		        }
		    });
		   
		for (int i = 0; i < list3.size(); i++) {
			System.out.println("list3(" + i + "): " + list3.get(i));
			if (i > 0) {
				if (list3.get(i).equals(list3.get(i-1))) {
					System.out.println("Equals: " + list3.get(i));
					list3.remove(i);
					i = i-1;
				}
			}
		}
		
		for (int i = 0; i < list3.size(); i++) {
			System.out.println("Zlist3(" + i + "): " + list3.get(i));
		}
		
	}
	
	public static void main(String[] args) {
		HashTableTest test = new HashTableTest();
		test.runit();
		
	}
}
