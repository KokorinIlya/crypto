import avl.LeafData;
import avl.LeafNode;
import com.avl.tree.AVLTree;

public class Main {

    public static void main(String[] args) throws Exception {
        AVLTree avlTree = new AVLTree();
        byte[] b = new byte[]{1};
        avlTree.add(new LeafNode(10, null, new LeafData(b), null));
        System.out.println(avlTree);
    }
}
