//A node of the search tree. Note that the search tree is not binary,
//however we exploit the fact that each node of the tree has at most four children.
public class Tree_Node {
	
	int[][] p;
	int h;				// the value of the heuristic function for this node
	int g;				// the depth of this node wrt the root of the search tree
	int f;				// f=0 or f=h or f=h+g, depending on the search algorithm used.
	Tree_Node parent;	// pointer to the parrent node (NULL for the root).
	int x1,x2,y1,y2;    //coordinates of the moves
	
	public Tree_Node (int n,int m) {
		p = new int[n][m];
	}
	
}
