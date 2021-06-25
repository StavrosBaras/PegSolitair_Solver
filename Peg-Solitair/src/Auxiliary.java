import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Auxiliary {
	
	// Auxiliary function that displays a puzzle on the screen (without lines).
	void display_puzzle(int N,int M,int p[][])
	{
		int i,j;
		for(i=0;i<N;i++)
		{
			for(j=0;j<M;j++)
				System.out.printf("%d\t",p[i][j]);
			System.out.printf("\n");
		}
	}
	
	// Auxiliary function that checks whether a puzzle is valid.
	public boolean is_valid(int N,int M,int p[][])
	{
		int i,j;
		int counter1 = 0;
		int counter2 = 0;

		for(i=0;i<N;i++)
			for(j=0;j<M;j++)
			{
				if (p[i][j]<0 || p[i][j]>2)		// Checks whether numbers are within bounds
					return false;
	            else if(p[i][j] == 1)
	                counter1++;
	            else if(p[i][j] == 2)
	                counter2++;
			}

	    if(counter1<2 || counter2 < 1 )        //Checks if there are enough pegs or enough spaces
	        return false;
	    else
	        return true;
	}

	// This function reads a file containing a puzzle and stores the dimensions
	// in an array.
	// Inputs:
	//		  String filename : The name of the file containing a puzzle.
	// Output:
	//		  int[2] : The array with the dimensions.
	public int[] get_dimensions(String filename){
		
		int tN=0;
		int tM=0;
		int[] d = {0,0};
		
		try {
			Scanner scanner = new Scanner(new File(filename));
			
			if(scanner.hasNextInt())
				tN = scanner.nextInt();
			if(scanner.hasNextInt())
				tM = scanner.nextInt();
			scanner.close();
		}catch(FileNotFoundException e)
		{
			 System.out.printf("Cannot open file %s. Program terminates.\n",filename);
			 System.exit(-1);
		}	
		
		if(tM>0 && tN>0){
	    	d[0] = tN;
	    	d[1] = tM;
	    }else{
	    	System.out.printf("False dimensions, exiting programm...");
	    	System.exit(-1);
	    }
		return d;

	}
	
	// This function reads a file containing a puzzle and stores the numbers
	// in the global variable int puzzle[N][N].
	// Inputs:
	//			String filename	: The name of the file containing a NxN puzzle.
	//			int[N][M] puzzle : The array where the puzzle will be stored
	// Output:
	//			0 --> Successful read.
	//			1 --> Unsuccessful read.
	public int read_puzzle(String filename,int N, int M, int puzzle[][]) {
		int i,j,dumb;
		
		try {
			Scanner scanner = new Scanner(new File(filename));
			
			if(scanner.hasNextInt())
				dumb = scanner.nextInt();
			if(scanner.hasNextInt())
				dumb = scanner.nextInt();
			
			for (i=0;i<N;i++)
				for(j=0;j<M;j++)
					puzzle[i][j] = scanner.nextInt();
			
			scanner.close();
		}catch(FileNotFoundException e)
		{
			 System.out.printf("Cannot open file %s. Program terminates.\n",filename);
			 System.exit(-1);
		}	

		if (is_valid(N,M,puzzle)) {
			return 0;}
		else
		{
			System.out.printf("Invalid puzzle contained in file %s. Program terminates.\n",filename);
			return -1;
		}
	}
	
	// This function finds the position of the next blank within a puzzle.
	// Inputs:
	// 			int p[N][M]	: A puzzle
	// 			int blankX	: The number where the x-position of the blank last was.
	// 			int blankY	: The number where the y-position of the blank last was.
	//Output:
	// 			int[2] : The coordinates of the blank.
	int[] find_blank(int N, int M, int p[][], int blankX, int blankY)
	{
	    int i;
	    int j;
	    int t[] = new int[2];

		for(i=0;i<N;i++){
			for(j=0;j<M;j++){
				if (p[i][j] == 2 && ((i == blankX && j > blankY) || (i>blankX))) {
	                t[0] = i;
	                t[1] = j;
					return t;
				}
	        }
	    }
		t[0]=-1;
		t[1]=-1;
		return t;
	}
	
}
