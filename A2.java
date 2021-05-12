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
	
	public static class Graph
	{
		private HashMap<String, ArrayList<String>> adjHash;

		//Constructor
		public Graph(){
			adjHash = new HashMap<String, ArrayList<String>>();
		}

		//Adds a vertex to the graph.
		public void addVertex(String vertex){
			ArrayList<String> list = new ArrayList<String>();
			adjHash.putIfAbsent(vertex, list);		//Will not add a vertex that already exists.
		}

		//Adds an edge between two vertices.
		public void addEdge(String v1, String v2){
			ArrayList<String> v1list = adjHash.get(v1);
			if (!v1list.contains(v2)){				//Will not add an edge that already exists.
				v1list.add(v2);
			}
			adjHash.replace(v1, v1list);
		}

		//Returns true if there's an edge between two vertices.
		public boolean edgeExists(String v1, String v2){
			ArrayList<String> v1edges = adjHash.get(v1);
			if (v1edges.contains(v2)){
				return true;
			}
			return false;
		}

		//Returns the number of elements in the hashtable.
		public int getSize(){
			return adjHash.size();
		}

		//Returns a list of all the edges a vertex is connected to.
		public ArrayList<String> neighbors(String vertex){
			return adjHash.get(vertex);
		}

		//Returns a list of all the vertices in the graph.
		public ArrayList<String> getVertices(){
			ArrayList<String> keys = new ArrayList<String>(adjHash.keySet());
			return keys;
		}

		//Prints out all the key-value pairs of the hashtable.
		public void printGraph(){
			for (String key : adjHash.keySet()){
				String value = adjHash.get(key).toString();
				System.out.println();
				System.out.println(key + " " + value);
			}
		}
	}

	//Circular queue implementation using arrays.
	public static class ArrayQueue<T>
	{
		Object[] tempArray = new Object[10];
		T[] arr;
		int head;
		int tail;

		//Constructor
		public ArrayQueue(){
			arr = (T[]) tempArray;
			head = 0;
			tail = 0;
		}
		
		//Checks to see if queue is empty.
		public boolean empty(){
			if (head == tail){
				return true;
			}
			return false;
		}

		//Removes and returns the first element in the queue.
		public T dequeue(){
			if (empty()){
				throw new NoSuchElementException();
			}
			T temp = arr[head];		//Holds element at head to be returned.
			head = (head + 1) % arr.length;		//Moves value of head to next index in queue.
			return temp;
		}

		//Adds item to the end of the queue.
		public void enqueue(T item)
		{
			if ((tail + 1) % arr.length == head){	//If array is too small, tail will reach the head and we must make a bigger circular array.
				grow_array();						//We have (tail + 1) to keep at least one slot open in queue.
			}
			arr[tail++] = item;		//Copies item to the tail, and increments tail.
			tail = tail % arr.length;	//Re-initializes tail to fit within the circular array.
		}

		//Makes the array bigger to fit more elements.
		protected void grow_array(){
			Object[] tempObject = new Object[arr.length * 2];	//Creates a temporary array that's 2x bigger.
			T[] temp = (T[]) tempObject;
			for (int i = 0; i < arr.length; i++){		//Iterates through each slot in queue.
				temp[i] = arr[(head + i) % arr.length];		//Copies each item in queue to temp array, starting at head.
			}
			tail = arr.length - 1;		//Re-initializes tail for new array.
			arr = temp;			//Assigns new larger array to queue.
			head = 0;		//Re-initializes head.
		}			
	}

	//BFS algorithm that outputs the shortest path between two vertices in an unweighted graph.
	private static ArrayList<String> breadthFS(Graph graph, String actor1, String actor2)
	{
		HashMap<String, Boolean> visited = new HashMap<String, Boolean>();		//Keeps track of which vertices have been taken care of.
		HashMap<String, String> previous = new HashMap<String, String>();		//Keeps track of the vertex that precedes the current one during BFS.
		ArrayList<String> path = new ArrayList<String>();						//Records the path between the source vertex to the destination vertex.
		ArrayQueue<String> queue = new ArrayQueue<String>();					//A to-do list of the vertices we will need to visit next as we go through BFS.

		String current = actor1;		//Adds the source vertex to the queue and visited table.
		queue.enqueue(current);
		visited.put(current, true);

		while (!queue.empty())
		{
			current = queue.dequeue();						//Removes the first item from the queue.
			if (current.equalsIgnoreCase(actor2))			//While loop continues until we find the destination vertex.
			{
				break;
			}
			else
			{
				for (String actor : graph.neighbors(current))		//Loops through all of the current vertex's edges, and adds them to queue and the visited table.
				{
					if (visited.get(actor) == null)
					{
						queue.enqueue(actor);
						visited.put(actor, true);
						previous.put(actor, current);				//Current is documented as the vertex that precedes each of its edges.
					}
				}
			}
		}
		if (!current.equalsIgnoreCase(actor2))				//If we don't find the destination after going through the loop, then no path exits and we can quit.
		{
			System.out.println("No such path exists between Actor 1 and Actor 2.");
			return null;
		}
		
		String m = actor2;			//Starting from the destination vertex, we continuously add the previous element to the final path.
		while (m != null)
		{
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
          
           
                
                //int movies = 0;
            
                
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

        	
            
            //csvParser.close();
       
        
        	// User Input:
        
     	Scanner scan = new Scanner(System.in);
     	System.out.println();
     	System.out.println("------------------------------------------------");
     	System.out.print("Actor 1 name: ");
     	String actor1 = scan.nextLine();
     	System.out.print("Actor 2 name: ");
     	String actor2 = scan.nextLine();
     		
     	//Correctly capitalizes the user input by looping through all the vertices in the graph and matching it with the input.
     	//Also checks to make sure the given names are valid actors.
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

     		//Performs a BFS on the graph from the first to second actor.
     		ArrayList<String> path = breadthFS(graph, actor1, actor2);
     		
     		//Prints the final results.
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


