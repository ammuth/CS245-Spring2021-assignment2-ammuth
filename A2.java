import org.apache.commons.csv.CSVFormat;	
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.simple.parser.ParseException;

import java.io.Reader;
import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.*;
import java.util.NoSuchElementException;

public class A2 {
	
	public static class Graph {

		private HashMap<String, ArrayList<String>> adjHash;


		public Graph(){
			adjHash = new HashMap<String, ArrayList<String>>();
		}


		public void addVertex(String vertex){
			ArrayList<String> list = new ArrayList<String>();
			adjHash.putIfAbsent(vertex, list);
		}


		public void addEdge(String v1, String v2){
			ArrayList<String> v1list = adjHash.get(v1);
			if (!v1list.contains(v2)){
				v1list.add(v2);
			}
			adjHash.replace(v1, v1list);
		}


		public boolean edgeExists(String v1, String v2){
			ArrayList<String> v1edges = adjHash.get(v1);
			if (v1edges.contains(v2)){
				return true;
			}
			return false;
		}


		public int getSize(){
			return adjHash.size();
		}


		public ArrayList<String> neighbors(String vertex){
			return adjHash.get(vertex);
		}


		public ArrayList<String> getVertices(){
			ArrayList<String> keys = new ArrayList<String>(adjHash.keySet());
			return keys;
		}


		public void printGraph(){
			for (String key : adjHash.keySet()){
				String value = adjHash.get(key).toString();
				System.out.println();
				System.out.println(key + " " + value);
			}
		}
	}


	public static class ArrayQueue<T> {

		Object[] tempArray = new Object[10];
		T[] arr;
		int head;
		int tail;


		public ArrayQueue(){
			arr = (T[]) tempArray;
			head = 0;
			tail = 0;
		}
		

		public boolean empty(){
			if (head == tail){
				return true;
			}
			return false;
		}


		public T dequeue(){
			if (empty()){
				throw new NoSuchElementException();
			}
			T temp = arr[head];
			head = (head + 1) % arr.length;
			return temp;
		}


		public void enqueue(T item) {

			if ((tail + 1) % arr.length == head){
				grow_array();
			}
			arr[tail++] = item;
			tail = tail % arr.length;
		}


		protected void grow_array(){
			Object[] tempObject = new Object[arr.length * 2];
			T[] temp = (T[]) tempObject;
			for (int i = 0; i < arr.length; i++){
				temp[i] = arr[(head + i) % arr.length];
			}
			tail = arr.length - 1;
			arr = temp;
			head = 0;
		}			
	}


	private static ArrayList<String> breadthFS(Graph graph, String actor1, String actor2) {

		HashMap<String, Boolean> visited = new HashMap<String, Boolean>();
		HashMap<String, String> previous = new HashMap<String, String>();
		ArrayList<String> path = new ArrayList<String>();
		ArrayQueue<String> queue = new ArrayQueue<String>();

		String current = actor1;
		queue.enqueue(current);
		visited.put(current, true);

		while (!queue.empty()) {

			current = queue.dequeue();
			if (current.equalsIgnoreCase(actor2)) {

				break;
			}
			else {

				for (String actor : graph.neighbors(current)) {

					if (visited.get(actor) == null) {

						queue.enqueue(actor);
						visited.put(actor, true);
						previous.put(actor, current);
					}
				}
			}
		}
		if (!current.equalsIgnoreCase(actor2)) {

			System.out.println("No such path exists between Actor 1 and Actor 2.");
			return null;
		}
		
		String m = actor2;
		while (m != null) {

			path.add(m);
			m = previous.get(m);
		}

		return path;
	}
	
    public static void main(String[] args) {
    	
    	Graph graph = new Graph();
    	
    	try {
        	Reader reader = new FileReader(args[0]);
        	CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
        	JSONParser jsonParser = new JSONParser();

            
                
            boolean firstround = true;
            for (CSVRecord csvRecord : csvParser) {
                	
            	if (!firstround) {
            		
            		try {
                		
            			String cast = csvRecord.get(2);
            			Object obj = jsonParser.parse(cast);
            			JSONArray castarray = (JSONArray)obj;
                			
            			for (int j = 0; j < castarray.size(); j++) {
                				
            				Object item1 = castarray.get(j);
            				JSONObject jsonitem1 = (JSONObject)item1;
            				String name1 = (String)jsonitem1.get("name");
            				graph.addVertex(name1);
                				
            				for (int i = 0; i < castarray.size(); i++) {
                					
            					Object item2 = castarray.get(i);
            					JSONObject jsonitem2 = (JSONObject)item2;
            					String name2 = (String)jsonitem2.get("name");
            					graph.addEdge(name1, name2);
                					
            				}
            			}

            		}
    				catch(ParseException e){
    					 //e.printStackTrace();
    				}
             }
             firstround = false;
            }
         } catch(Exception e) {
        	 System.out.println("File " + args[0] + "is invalid or is in the wrong format.");
    	}

        	
            

       
        

        
     	Scanner scan = new Scanner(System.in);
     	System.out.println();
     	System.out.println("------------------------------------------------");
     	System.out.print("Actor 1 name: ");
     	String actor1 = scan.nextLine();
     	System.out.print("Actor 2 name: ");
     	String actor2 = scan.nextLine();
     		

     	boolean actor1found = false;
     	boolean actor2found = false;
     	ArrayList<String> vertices = graph.getVertices();
     	for (String vertex : vertices) {
     		if (actor1.equalsIgnoreCase(vertex)) {
    				actor1found = true;
    				actor1 = vertex;
     		}
     			if (actor2.equalsIgnoreCase(vertex)) {
     				actor2found = true;
     				actor2 = vertex;
     			}
     	}
     		if (!actor1found || !actor2found) {
     			System.out.println("No such actor.");
     			return;
     		}


     		ArrayList<String> path = breadthFS(graph, actor1, actor2);
     		

     		System.out.println("------------------------------------------------");
     		System.out.println("Path from " + actor1 + " to " + actor2 + ": ");
     		System.out.print(path.get(path.size() - 1) + " ");
     		for (int k = path.size() - 2; k >= 0; k--) {
     			System.out.print("--> ");
     			System.out.print(path.get(k) + " ");
     		}
     		System.out.println();
     		System.out.println("------------------------------------------------");
     	}

    }


