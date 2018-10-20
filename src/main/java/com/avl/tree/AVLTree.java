package com.avl.tree;

import avl.Direction;
import avl.Hash;
import avl.LeafNode;
import avl.Node;
import avl.NodeHeightInfo;
import avl.Proof;
import avl.ProofEntity;
import avl.ProofHash;
import avl.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class AVLTree {

    private static Hash EMPTY_HASH = new Hash(new byte[0]);

    private TreeNode root = null;

    public AVLTree() {
        root = new TreeNode(null, null, null, null, null, EMPTY_HASH, 0, 0);
    }

    public Proof add(LeafNode newNode) throws Exception {
        addHelper(root, null, newNode);
        Proof proof = getProof(newNode.getKey());
        balance(root);
        return proof;
    }

    public Proof find(int key) throws Exception {
        // Нужно еще не забыть, что ключа в дереве то мозжет и не быть!!!
        return getProof(key);
    }

    private Proof getProof(int key) throws Exception {
        List<ProofEntity> almostProof = getProofHelper(root, key, false, false);
        List<ProofHash> entries = new ArrayList<ProofHash>();
        List<NodeHeightInfo> heightInfos = new ArrayList<NodeHeightInfo>();
        List<Direction> directions = new ArrayList<Direction>();
        for (ProofEntity entity : almostProof) {
            entries.add(entity.getEntry());
            heightInfos.add(entity.getHeight());
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
            ProofEntity proofEntity = new ProofEntity(
                new ProofHash(leaf.getHash()),
                new NodeHeightInfo(0, 0),
                moveLeft ? Direction.RIGHT : Direction.LEFT
            );
            if (justAns) {
                result.add(proofEntity);
            }
        } else if (node instanceof TreeNode) {
            TreeNode tree = (TreeNode) node;
            if (justAns) {
                ProofEntity proofEntity = new ProofEntity(
                    new ProofHash(tree.getHash()),
                    new NodeHeightInfo(tree.getLeftHeight(), tree.getRightHeight()),
                    moveLeft ? Direction.RIGHT : Direction.LEFT
                );
                result.add(proofEntity);
                return result;
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
                rightPart.add(leftPart.get(0));
                result = rightPart;
            } else {
                List<ProofEntity> leftPart = getProofHelper(tree.getLeft(), key, false, true);
                List<ProofEntity> rightPart = getProofHelper(tree.getRight(), key, true, false);
                if (rightPart.size() != 1) {
                    throw new Exception("Invalid sizes when key < rightMin");
                }
                leftPart.add(rightPart.get(0));
                result = leftPart;
            }
        }

        return result;
    }

    private void balance(Node node) {
        // do something
    }

    private Node merge(Node nodeA, Node nodeB, Node prev, boolean noSwap) {
        TreeNode result;
        if (!noSwap) {
            result = new TreeNode(nodeA, nodeB, prev, null, null, EMPTY_HASH, 0, 0);
        } else {
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
}
