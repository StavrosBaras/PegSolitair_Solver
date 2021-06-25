import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Main {
	
	static boolean SHOW_COMMENTS = false;
	
	static Auxiliary aux = new Auxiliary();
	
	static int depth = 1;   // Constants denoting the two algorithms
	static int best = 2;
	
	static int N; // number of rows
	static int M; // number of columns
	static int blankX = 0; // Row of current blank
	static int blankY = 0; // Column of current blank
	
	static Frontier_Node frontier_head = null;	// The one end of the frontier
	static Frontier_Node frontier_tail = null;	// The other end of the frontier
	
	static long t1;					// Start time of the search algorithm
	static long t2;					// End time of the search algorithm
	static int timeout = 60;		// Program terminates after TIMEOUT seconds
	
	static int solution_length;		// The length of the solution table.
	static int solution[][];		// Pointer to a dynamic table with the moves of the solution.
	
	//Auxiliary function that displays a message in case of wrong input parameters.
	public static void syntax_message()
	{
		System.out.print("main <method> <input-file> <output-file>\n\n");
		System.out.print("where: ");
		System.out.print("<method> = depth|best\n");
		System.out.print("<input-file> is a file containing a Peg Solitaire puzzle description.\n");
		System.out.print("<output-file> is the file where the solution will be written.\n");
	}

	// This function checks whether a puzzle is a solution puzzle.
	// Inputs:
	//			int p[N][M]		: A puzzle
	// Outputs:
	//			true --> The puzzle is a solution puzzle
	//			false --> The puzzle is NOT a solution puzzle
	static boolean is_solution(int p[][])
	{
	    int counter = 0;
		int i,j;

		for(i=0;i<N;i++)
			for(j=0;j<M;j++)
				if (p[i][j] == 1)
					counter++;

		if(counter == 0 || counter > 1){ //Checks if there is only one peg left
			//System.out.println(counter);
	        return false;
		}else{
		    return true;
		}
	}
	
	// This function writes the solution into a file
	// Inputs:
	//			String filename	: The name of the file where the solution will be written.
	// Outputs:
	//			Nothing (apart from the new file)
	static void write_solution_to_file(String filename)
	{
		int i;
		FileWriter myWriter;
		
		try {
			myWriter = new FileWriter(filename);
			myWriter.write(solution_length + "\n");
			for (i=0;i<solution_length;i++) {
				myWriter.write(solution[i][0] + 1 + " ");
				myWriter.write(solution[i][1] + 1 + " ");
				myWriter.write(solution[i][2] + 1 + " ");
				myWriter.write(solution[i][3] + 1 + " \n");
			}
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	// Giving a (solution) leaf-node of the search tree, this function computes
	// the moves of the pegs that have to be done, starting from the root puzzle,
	// in order to go to the leaf node's puzzle.
	// Inputs:
	//			Tree_Node solution_node	: A leaf-node
	// Output:
	//			The sequence of blank's moves that have to be done, starting from the root puzzle,
	//			in order to receive the leaf-node's puzzle, is stored into the global variable solution.
	static void extract_solution(Tree_Node solution_node)
	{
		int i;

		Tree_Node temp_node=solution_node;
		solution_length=solution_node.g;

		//solution= (int*) malloc(solution_length*sizeof(int));
		solution = new int[solution_length][4];
		temp_node=solution_node;
		i=solution_length;
		while (temp_node.parent!=null)
		{
			i--;
			//solution[i]=temp_node->direction;
			solution[i][0]=temp_node.x1;
			solution[i][1]=temp_node.y1;
			solution[i][2]=temp_node.x2;
			solution[i][3]=temp_node.y2;
			temp_node=temp_node.parent;
		}
	}
	
	// Reading run-time parameters.
	public static int get_method(String s)
	{
		if (s.equals("depth"))
			return depth;
		else if (s.equals("best"))
			return best;
		else
			return -1;
	}
	
	// Giving a number n and its position (i,j) in a puzzle,
	// this function returns the manhattan distance of this peg
	// from all the other pegs.
	// Inputs:
	//			int[][] p; 	The current puzzle.
	//			int i;		The current vertical position of the number, 0<=i<N.
	//			int j;		The current horizontal position of the number, 0<=j<M.
	// Output:
	//			The manhattan distance of this peg from all the other pegs.
	static int manhattan_distance(int[][] p,int i, int j)
	{	
		int man = 0;
		
		for(int ii=0;ii<N;ii++) {
			for(int jj=0;jj<M;jj++) {
				if(p[ii][jj]==1) {
					man += Math.abs(i - ii) + Math.abs(j - jj);
				}
			}
		}
		
		return man;
		
	}
	
	// Giving a puzzle, this function computes the sum of the manhattan
	// distances between the pegs.
	// Inputs:
	//			int p[N][M];	A puzzle
	// Output:
	//			As described above.
	static int heuristic(int p[][])
	{
		int i,j;
		int score=0;
		int nPegs = 0;
		
		for (i=0;i<N;i++)
			for (j=0;j<M;j++)
				if(p[i][j]==1) {
					nPegs++;
					score+=manhattan_distance(p,i,j);
				}
		
		return score / (2 * nPegs);
	}
	
	// This function adds a pointer to a new leaf search-tree node at the front of the frontier.
	// This function is called by the depth-first search algorithm.
	// Inputs:
	//			Tree_Node node	: A (leaf) search-tree node.
	// Output:
	//			0 --> The new frontier node has been added successfully.
	//			-1 --> Memory problem when inserting the new frontier node .
	static int add_frontier_front(Tree_Node node)
	{
		// Creating the new frontier node
		Frontier_Node new_frontier_node = new Frontier_Node();
		new_frontier_node.n=node;
		new_frontier_node.previous=null;
		new_frontier_node.next=frontier_head;

		if (frontier_head==null)
		{
			frontier_head=new_frontier_node;
			frontier_tail=new_frontier_node;
		}
		else
		{
			frontier_head.previous=new_frontier_node;
			frontier_head=new_frontier_node;
		}

		if(SHOW_COMMENTS) {
			System.out.printf("Added to the front...\n");
			aux.display_puzzle(N,M,node.p);
		}
		
		return 0;
	}

	// This function adds a pointer to a new leaf search-tree node at the back of the frontier.
	// This function is called by the breadth-first search algorithm.
	// Inputs:
	//			Tree_Node node	: A (leaf) search-tree node.
	// Output:
	//			0 --> The new frontier node has been added successfully.
	//			-1 --> Memory problem when inserting the new frontier node .
	static int add_frontier_back(Tree_Node node)
	{
		// Creating the new frontier node
		Frontier_Node new_frontier_node = new Frontier_Node();
		new_frontier_node.n=node;
		new_frontier_node.previous=null;
		new_frontier_node.next=frontier_tail;

		if (frontier_tail==null)
		{
			frontier_head=new_frontier_node;
			frontier_tail=new_frontier_node;
		}
		else
		{
			frontier_tail.next=new_frontier_node;
			frontier_tail=new_frontier_node;
		}

		if(SHOW_COMMENTS) {
			System.out.printf("Added to the back...\n");
			aux.display_puzzle(N,M,node.p);
		}

		return 0;
	}

	// This function adds a pointer to a new leaf search-tree node within the frontier.
	// The frontier is always kept in increasing order wrt the f values of the corresponding
	// search-tree nodes. The new frontier node is inserted in order.
	// This function is called by the heuristic search algorithm.
	// Inputs:
	//			Tree_Node node	: A (leaf) search-tree node.
	// Output:
	//			0 --> The new frontier node has been added successfully.
	//			-1 --> Memory problem when inserting the new frontier node .
	static int add_frontier_in_order(Tree_Node node)
	{
		// Creating the new frontier node
		Frontier_Node new_frontier_node = new Frontier_Node();
		new_frontier_node.n=node;
		new_frontier_node.previous=null;
		new_frontier_node.next=null;

		if (frontier_head==null)
		{
			frontier_head=new_frontier_node;
			frontier_tail=new_frontier_node;
		}
		else
		{
			Frontier_Node pt;
			pt=frontier_head;

			// Search in the frontier for the first node that corresponds to either a larger f value
			// or to an equal f value but larger h value
			// Note that for the best first search algorithm, f and h values coincide.
			while (pt!=null && (pt.n.f<node.f || (pt.n.f==node.f && pt.n.h<node.h)))
				pt=pt.next;

			if (pt!=null)
			{
				// new_frontier_node is inserted before pt .
				if (pt.previous!=null)
				{
					pt.previous.next=new_frontier_node;
					new_frontier_node.next=pt;
					new_frontier_node.previous=pt.previous;
					pt.previous=new_frontier_node;
				}
				else
				{
					// In this case, new_frontier_node becomes the first node of the frontier.
					new_frontier_node.next=pt;
					pt.previous=new_frontier_node;
					frontier_head=new_frontier_node;
				}
			}
			else
			{
				// if pt==NULL, new_frontier_node is inserted at the back of the frontier
				frontier_tail.next=new_frontier_node;
				new_frontier_node.previous=frontier_tail;
				frontier_tail=new_frontier_node;
			}
		}

		if(SHOW_COMMENTS) {
			System.out.printf("Added in order (f=%d)...\n",node.f);
			aux.display_puzzle(N,M,node.p);
		}

		return 0;
	}
	
	// This function expands a leaf-node of the search tree.
	// A leaf-node may have up to X childs. A table with X pointers
	// to these childs is created, with NULLs for those childrens that do not exist.
	// In case no child exists (due to loop-detections), the table is not created
	// and a 'higher-level' NULL indicates this situation.
	// Inputs:
	//			Tree_Node current_node	: A leaf-node of the search tree.
	// Output:
	//			The same leaf-node expanded with pointers to its children (if any).
	static int find_children(Tree_Node current_node, int method)
	{
		int x,y;
		int t[] = new int[2];
		blankX = 0;
		blankY = 0;
		
		while(blankX != -1 && blankY != -1) {
			
			// Find the next blank position in the current puzzle
			t = aux.find_blank(N , M, current_node.p,blankX,blankY);
			blankX = t[0];
			blankY = t[1];
			
			if(blankX == -1 || blankY == -1) {
				//System.out.printf("Did not find blank... \n");
				return 1;
			}
			
			//System.out.printf("found blank at %d , %d \n", blankX, blankY);
			//aux.display_puzzle(N,M,current_node.p);
			
			// Make left move
			if ( blankY < M-2 && current_node.p[blankX][blankY + 1] == 1 && current_node.p[blankX][blankY + 2] == 1)
			{
	
				// Initializing the new child
				Tree_Node child=new Tree_Node(N,M);
				child.parent=current_node;
				child.x1 = blankX;
				child.x2 = blankX;
				child.y1 = blankY + 2;
				child.y2 = blankY;
				child.g=current_node.g+1;		// The depth of the new child
	
				// Computing the puzzle for the new child
		        for(x=0;x<N;x++){
		            for(y=0;y<M;y++){
		                if(x == blankX && y == blankY){
		                    child.p[x][y] = 1;
		                }
		                else if(x==blankX && y == blankY + 1)
		                    child.p[x][y] = 2;
		                else if(x==blankX && y == blankY + 2)
		                    child.p[x][y] = 2;
		                else
		                    child.p[x][y] = current_node.p[x][y];
		            }
		        }
	
		        //System.out.printf("Made left move \n");
		        //aux.display_puzzle(N,M,child.p);
	
		        // Computing the heuristic value
		        child.h=heuristic(child.p);
		        if (method==best)
		            child.f=child.h;
		        else
		            child.f=0;
	
		        int err=0;
		        if (method==depth)
		            err=add_frontier_front(child);
		        else if (method==best)
		            err=add_frontier_in_order(child);
		        if (err<0)
		            return -1;
	
			}
				
			// Make right move
			if (blankY > 1 && current_node.p[blankX][blankY - 1] == 1 && current_node.p[blankX][blankY - 2] == 1)
			{
				// Initializing the new child
				Tree_Node child=new Tree_Node(N,M);
				child.parent=current_node;
				child.x1 = blankX;
				child.x2 = blankX;
				child.y1 = blankY - 2;
				child.y2 = blankY;
				child.g=current_node.g+1;		// The depth of the new child
				// Computing the puzzle for the new child
				for(x=0;x<N;x++){
		            for(y=0;y<M;y++){
		                if(x == blankX && y == blankY)
		                    child.p[x][y] = 1;
		                else if(x==blankX && y == blankY - 1)
		                    child.p[x][y] = 2;
		                else if(x==blankX && y == blankY - 2)
		                    child.p[x][y] = 2;
		                else
		                    child.p[x][y] = current_node.p[x][y];
		            }
		        }
	
				//System.out.printf("Made right move \n");
		        //aux.display_puzzle(N,M,child.p);
	
				// Computing the heuristic value
		        child.h=heuristic(child.p);
		        if (method==best)
		            child.f=child.h;
		        else
		            child.f=0;
	
		        int err=0;
		        if (method==depth)
		            err=add_frontier_front(child);
		        else if (method==best)
		            err=add_frontier_in_order(child);
		        if (err<0)
		            return -1;
			}
				
			// Make up move
			if (blankX < N-2  && current_node.p[blankX + 1][blankY] == 1 && current_node.p[blankX + 2][blankY] == 1)
		    {
				// Initializing the new child
				Tree_Node child=new Tree_Node(N,M);
				child.parent=current_node;
				child.x1 = blankX + 2;
				child.x2 = blankX;
				child.y1 = blankY;
				child.y2 = blankY;
				child.g=current_node.g+1;		// The depth of the new child
	
				// Computing the puzzle for the new child
		        for(x=0;x<N;x++){
		            for(y=0;y<M;y++){
		                if(x == blankX && y == blankY)
		                    child.p[x][y] = 1;
		                else if(x==blankX + 1 && y == blankY)
		                    child.p[x][y] = 2;
		                else if(x==blankX + 2 && y == blankY)
		                    child.p[x][y] = 2;
		                else
		                    child.p[x][y] = current_node.p[x][y];
		            }
		        }
	
		        //System.out.printf("Made up move \n");
		        //aux.display_puzzle(N,M,child.p);
	
		        // Computing the heuristic value
		        child.h=heuristic(child.p);
		        if (method==best)
		            child.f=child.h;
		        else
		            child.f=0;
	
		        int err=0;
		        if (method==depth)
		            err=add_frontier_front(child);
		        else if (method==best)
		            err=add_frontier_in_order(child);
		        if (err<0)
		            return -1;
	
			}
	
			// Make down move
			if (blankX > 1 && current_node.p[blankX - 1][blankY] == 1 && current_node.p[blankX - 2][blankY] == 1)
		    {
				// Initializing the new child
				Tree_Node child=new Tree_Node(N,M);
				child.parent=current_node;
				child.x1 = blankX - 2 ;
				child.x2 = blankX;
				child.y1 = blankY;
				child.y2 = blankY;
				child.g=current_node.g+1;		// The depth of the new child
	
				// Computing the puzzle for the new child
		        for(x=0;x<N;x++){
		            for(y=0;y<M;y++){
		                if(x == blankX && y == blankY)
		                    child.p[x][y] = 1;
		                else if(x==blankX - 1 && y == blankY)
		                    child.p[x][y] = 2;
		                else if(x==blankX - 2 && y == blankY)
		                    child.p[x][y] = 2;
		                else
		                    child.p[x][y] = current_node.p[x][y];
		            }
		        }
	
		        //System.out.printf("Made down move \n");
		        //aux.display_puzzle(N,M,child.p);
	
		        // Computing the heuristic value
		        child.h=heuristic(child.p);
		        if (method==best)
		            child.f=child.h;
		        else
		            child.f=0;
	
		        int err=0;
		        if (method==depth)
		            err=add_frontier_front(child);
		        else if (method==best)
		            err=add_frontier_in_order(child);
		        if (err<0)
		            return -1;
	
			}

		}

		return 1;
	}
	
	// This function initializes the search, i.e. it creates the root node of the search tree
	// and the first node of the frontier.
	static void initialize_search(int puzzle[][], int method)
	{
		Tree_Node root = new Tree_Node(N, M);	// the root of the search tree.
		int i,j;

		// Initialize search tree
		root.parent = null;
		root.x1=-1;
		root.x2=-1;
		root.y1=-1;
		root.y2=-1;

		for(i=0;i<N;i++)
			for(j=0;j<M;j++){
				root.p[i][j]=puzzle[i][j];
			}

		root.g=0;
		root.h=heuristic(root.p);
		if (method==best)
			root.f=root.h;
		else
			root.f=0;

		// Initialize frontier
		add_frontier_front(root);
	}
	
	// This function implements at the highest level the search algorithms.
	// The various search algorithms differ only in the way the insert
	// new nodes into the frontier, so most of the code is common for all algorithms.
	// Inputs:
	//			Nothing, except for the global variables root, frontier_head and frontier_tail.
	// Output:
	//			NULL --> The problem cannot be solved
	//			Tree_node	: An object of a search-tree leaf node that corresponds to a solution.
	static Tree_Node search(int method)
	{
		long t;
		int err;
		Tree_Node current_node;

		while (frontier_head!=null)
		{
			t=System.currentTimeMillis();
			if (t-t1>1000*timeout)
			{
				System.out.printf("Timeout\n");
				return null;
			}

			// Extract the first node from the frontier
			current_node=frontier_head.n;

	        if(SHOW_COMMENTS) {
	        	System.out.printf("Extracted from frontier...\n");
	            aux.display_puzzle(N,M,current_node.p);
	        }
	        
			if (is_solution(current_node.p))
				return current_node;

			frontier_head=frontier_head.next;
			if (frontier_head==null)
				frontier_tail=null;
			else
				frontier_head.previous=null;

			// Find the children of the extracted node
			err=find_children(current_node, method);

			if (err<0)
	        {
	            System.out.printf("Memory exhausted while creating new frontier node. Search is terminated...\n");
				return null;
	        }
		}

		return null;
	}
	
	public static void main(String[] args) {
		
		int method;
		int err;
		int[] dimensions;
		Tree_Node solution_node;
		
		if (args.length!=3)
		{
			System.out.print("Wrong number of arguments. Use correct syntax:\n");
			syntax_message();
			System.exit(-1);
		}
		
		method=get_method(args[0]);
		if (method<0)
		{
			System.out.print("Wrong method. Use correct syntax:\n");
			syntax_message();
			System.exit(-1);
		}
		
		dimensions = aux.get_dimensions(args[1]);
		N = dimensions[0];
		M = dimensions[1];
		
		int[][] puzzle = new int[N][M];
		
		err=aux.read_puzzle(args[1],N,M, puzzle);
		if (err<0){
			System.exit(-1);
		}
		
		System.out.printf("Solving %s using %s...\n",args[1],args[0]);
		t1=System.currentTimeMillis();
		
		initialize_search(puzzle, method);
		
		solution_node=search(method);			// The main call

		t2=System.currentTimeMillis();

		if (solution_node!=null) {
			extract_solution(solution_node);
		}
		else {
			System.out.printf("No solution found.\n");
			System.out.printf("Time spent: %d secs\n",(t2-t1)/1000);
		}

		if (solution_length>0)
		{
			System.out.printf("Solution found! (%d steps)\n",solution_length);
			System.out.println("Time spent: " + (float)(t2-t1)/1000  + " secs");
			write_solution_to_file(args[2]);
		}

	}

}
