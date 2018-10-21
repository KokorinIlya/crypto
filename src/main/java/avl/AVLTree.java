package avl;

import java.util.ArrayList;
import java.util.List;

public class AVLTree {

    private static Hash EMPTY_HASH = new Hash(new byte[0]);

    private TreeNode root = null;
    private LeafData findResult = null;
    private LeafNode findNextNode = null;

    public AVLTree() {
        root = new TreeNode(
            new LeafNode(Integer.MIN_VALUE, null, null, new LeafData(new byte[0]), null),
            new LeafNode(Integer.MAX_VALUE, null, null, new LeafData(new byte[0]), null),
            null, null, null, EMPTY_HASH, 0, 0);
        assert root.getLeft() != null;
        ((LeafNode)root.getLeft()).setNextKey((LeafNode)root.getRight());
        assert root.getRight() != null;
        ((LeafNode)root.getRight()).setPrevKey((LeafNode)root.getLeft());
        root.calculateAll();
    }

    public Hash getRootHash() {
        return root.getHash();
    }

    public TreeResponse<Proof, LeafData, LeafNode> add(int key, byte[] data) throws Exception {
        LeafNode newNode = new LeafNode(key, null, null, new LeafData(data), null);
        addHelper(root, null, newNode);
        TreeResponse<Proof, LeafData, LeafNode> result = find(key);
        if (key == 15) {
            System.out.println("BEFORE: " + this);
        }
        root = (TreeNode) balance(root, newNode.getKey());
        return result;
    }

    public TreeResponse<Proof, LeafData, LeafNode> find(int key) throws Exception {
        findResult = null;
        findNextNode = null;
        Proof proof = getProof(key);
        return new TreeResponse<Proof, LeafData, LeafNode>(proof, findResult, findNextNode);
    }

    private List<NodeHeightInfo> heightInfos;

    private Proof getProof(int key) throws Exception {
        heightInfos = new ArrayList<NodeHeightInfo>();
        List<ProofEntity> almostProof = getProofHelper(root, key, false, false);
        List<Hash> entries = new ArrayList<Hash>();
        List<Direction> directions = new ArrayList<Direction>();
        for (ProofEntity entity : almostProof) {
            entries.add(entity.getEntry());
            directions.add(entity.getDirection());
        }
        return new Proof(entries, heightInfos, directions);
    }

    private List<ProofEntity> getProofHelper(Node node, int key, boolean justAns, boolean moveLeft) throws Exception {
        List<ProofEntity> result = new ArrayList<ProofEntity>();
        if (node == null) {
            throw new Exception("YOUR TREE IS NOT GOOD, BRO!");
        }
        if (node instanceof LeafNode) {
            LeafNode leaf = (LeafNode) node;
            if (leaf.getKey() == key) {
                findResult = leaf.getData();
                findNextNode = leaf.getNextKey();
            }
            ProofEntity proofEntity = new ProofEntity(
                leaf.getHash(),
                moveLeft ? Direction.RIGHT : Direction.LEFT
            );
            if (justAns) {
                System.out.println(leaf.getKey() + "!");
                result.add(proofEntity);
            }
        } else if (node instanceof TreeNode) {
            TreeNode tree = (TreeNode) node;
            if (justAns) {
                System.out.println(tree.getRightMin() + "?");
                ProofEntity proofEntity = new ProofEntity(
                    tree.getHash(),
                    moveLeft ? Direction.RIGHT : Direction.LEFT
                );
                result.add(proofEntity);
                return result;
            } else {
                heightInfos.add(new NodeHeightInfo(tree.getLeftHeight(), tree.getRightHeight()));
            }
            Integer rightMin = tree.getRightMin();
            if (rightMin == null) {
                throw new Exception("YOU TREE IS REALLY BAD, BRO!");
            } else if (key >= rightMin) {
                List<ProofEntity> leftPart = getProofHelper(tree.getLeft(), key, true, true);
                List<ProofEntity> rightPart = getProofHelper(tree.getRight(), key, false, false);
                if (leftPart.size() != 1) {
                    throw new Exception("Invalid sizes when key >= rightMin");
                }
                leftPart.addAll(rightPart);
                result = leftPart;
            } else {
                List<ProofEntity> rightPart = getProofHelper(tree.getRight(), key, true, false);
                List<ProofEntity> leftPart = getProofHelper(tree.getLeft(), key, false, true);
                if (rightPart.size() != 1) {
                    throw new Exception("Invalid sizes when key < rightMin");
                }
                rightPart.addAll(leftPart);
                result = rightPart;
            }
        }

        return result;
    }

    private Node balance(Node node, int key) throws Exception {
        if (node == null || node instanceof LeafNode) {
            return node;
        }
        TreeNode tree = (TreeNode) node;
        if (tree.getRightMin() == null) {
            throw new Exception("YOU LOSE!");
        }
        if (key >= tree.getRightMin()) {
            tree.setRight(balance(tree.getRight(), key));
            return balanceNode(node);
        } else {
            tree.setLeft(balance(tree.getLeft(), key));
            return balanceNode(node);
        }
    }

    private Node balanceNode(Node node) {
        if (node == null || node instanceof LeafNode) {
            return node;
        }
        TreeNode tree = (TreeNode) node;
        tree.calculateAll();
        int diff = getDiff(tree);
        if (diff == -2) {
            if (getDiff(tree.getRight()) <= 0) {
                System.out.println("Left SMALL rotation!");
                TreeNode a = tree;
                TreeNode b = (TreeNode) tree.getRight();
                a.setRight(b.getLeft());
                b.setLeft(a);
                TreeNode result = b;
                result.setLeft(balanceNode(result.getLeft()));
                return balanceNode(result);
            } else {
                System.out.println("Left BIG rotation!");
                TreeNode b = (TreeNode) tree.getRight();
                TreeNode c = (TreeNode) b.getLeft();
                tree.setRight(c.getLeft());
                b.setLeft(c.getRight());
                c.setLeft(tree);
                c.setRight(b);
                TreeNode result = c;
                result.setLeft(balanceNode(result.getLeft()));
                result.setRight(balanceNode(result.getRight()));
                return balanceNode(result);
            }
        } else if (diff == 2) {
            if (getDiff(tree.getLeft()) >= 0) {
                System.out.println("Right SMALL rotation!");
                TreeNode b = (TreeNode) tree.getLeft();
                tree.setLeft(b.getRight());
                b.setRight(tree);
                TreeNode result = b;
                result.setRight(balanceNode(result.getRight()));
                return balanceNode(result);
            } else {
                System.out.println("Right BIG rotation!");
                TreeNode b = (TreeNode) tree.getLeft();
                TreeNode c = (TreeNode) b.getRight();
                tree.setLeft(c.getRight());
                b.setRight(c.getLeft());
                c.setRight(tree);
                c.setLeft(b);
                TreeNode result = c;
                result.setLeft(balanceNode(result.getLeft()));
                result.setRight(balanceNode(result.getRight()));
                return balanceNode(result);
            }
        }
        return node;
    }

    private int getDiff(Node node) {
        if (node == null || node instanceof LeafNode) {
            return 0;
        } else {
            TreeNode tree = (TreeNode) node;
            return tree.getLeftHeight() - tree.getRightHeight();
        }
    }

    private Node merge(LeafNode nodeA, LeafNode nodeB, Node prev, boolean noSwap) {
        TreeNode result;
        if (noSwap) {
            nodeB.setPrevKey(nodeA);
            if (nodeA.getNextKey() != null) {
                nodeA.getNextKey().setPrevKey(nodeB);
            }
            nodeB.setNextKey(nodeA.getNextKey());
            nodeA.setNextKey(nodeB);
            result = new TreeNode(nodeA, nodeB, prev, null, null, EMPTY_HASH, 0, 0);
        } else {
            if (nodeA.getPrevKey() != null) {
                nodeA.getPrevKey().setNextKey(nodeB);
            }
            nodeB.setPrevKey(nodeA.getPrevKey());
            nodeA.setPrevKey(nodeB);
            nodeB.setNextKey(nodeA);
            result = new TreeNode(nodeB, nodeA, prev, null, null, EMPTY_HASH, 0, 0);
        }
        result.calculateAll();
        return result;
    }

    private void addHelper(Node currentNode, Node parentNode, LeafNode newNode) throws Exception {
        if (currentNode instanceof LeafNode) {
            LeafNode node = (LeafNode) currentNode;
            TreeNode parent = (TreeNode) parentNode;
            if (parent.getLeft() == currentNode) {
                if (node.getKey() == newNode.getKey()) {
                    parent.setLeft(newNode);
                    parent.calculateAll();
                } else {
                    parent.setLeft(merge(node, newNode, parent, node.getKey() < newNode.getKey()));
                    parent.calculateAll();
                }
            } else if (parent.getRight() == currentNode) {
                if (node.getKey() == newNode.getKey()) {
                    parent.setRight(newNode);
                    parent.calculateAll();
                } else {
                    parent.setRight(merge(node, newNode, parent, node.getKey() < newNode.getKey()));
                    parent.calculateAll();
                }
            } else {
                throw new Exception("Current Node is not left neither right child of the parent");
            }
        } else if (currentNode instanceof TreeNode) {
            TreeNode node = (TreeNode) currentNode;
            Integer rightMin = node.getRightMin();
            if (rightMin == null) {
                node.setRight(newNode);
                node.calculateAll();
            } else {
                if (newNode.getKey() < rightMin) {
                    addHelper(node.getLeft(), node, newNode);
                } else {
                    addHelper(node.getRight(), node, newNode);
                }
            }
            node.calculateAll();
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        toStringHelper(root, stringBuilder, 0);
        return stringBuilder.toString();
    }

    private void toStringHelper(Node node, StringBuilder stringBuilder, int h) {
        if (node == null) {
            stringBuilder.append("GO BACK\n");
            return;
        }
        if (node instanceof LeafNode) {
            LeafNode leaf = (LeafNode) node;
            stringBuilder.append("Leaf{key: ")
                .append(leaf.getKey())
                .append(", hash: ")
                .append(leaf.getHash())
                .append(", height: ")
                .append(h)
                .append("}\n");
        } else if (node instanceof TreeNode) {
            TreeNode tree = (TreeNode) node;
            stringBuilder.append("TreeNode{rightMin: ")
                .append(tree.getRightMin())
                .append(", allMin: ")
                .append(tree.getAllMin())
                .append(", leftHeight: ")
                .append(tree.getLeftHeight())
                .append(", rightHeight: ")
                .append(tree.getRightHeight())
                .append(", hash: ")
                .append(tree.getHash())
                .append("}\n");
            stringBuilder.append("GO LEFT\n");
            toStringHelper(tree.getLeft(), stringBuilder, h + 1);
            stringBuilder.append("GO RIGHT\n");
            toStringHelper(tree.getRight(), stringBuilder, h + 1);
        }
        stringBuilder.append("GO BACK\n");
    }
}
