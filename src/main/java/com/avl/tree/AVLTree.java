package com.avl.tree;

import avl.Hash;
import avl.LeafNode;
import avl.Node;
import avl.ProofHash;
import avl.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class AVLTree {

    private TreeNode root = null;

    public AVLTree() {
        Hash hash = new Hash(new byte[0]);
        root = new TreeNode(null, null, null, null, null, hash, 0, 0);
    }

    public void add(LeafNode newNode) throws Exception {
        addHelper(root, null, newNode);
    }

    private List<ProofHash> addHelper(Node currentNode, Node parentNode, LeafNode newNode) throws Exception {
        if (currentNode instanceof LeafNode) {
            LeafNode node = (LeafNode) currentNode;
            TreeNode parent = (TreeNode) parentNode;
            if (parent.getLeft() == currentNode) {
                if (node.getKey() == newNode.getKey()) {
                    parent.setLeft(newNode);
                    parent.calculateHeights();
                    parent.calculateMins();
                } else {
                    // do something
                }
            } else if (parent.getRight() == currentNode) {
                if (node.getKey() == newNode.getKey()) {
                    parent.setRight(newNode);
                    parent.calculateHeights();
                    parent.calculateMins();
                } else {
                    // do something
                }
            } else {
                throw new Exception("Current Node is not left neither right child of the parent");
            }
        } else if (currentNode instanceof TreeNode) {
            TreeNode node = (TreeNode) currentNode;
            Integer rightMin = node.getRightMin();
            if (rightMin == null) {
                node.setRight(newNode);
                node.calculateHeights();
                node.calculateMins();
            } else {
                if (newNode.getKey() < rightMin) {
                    return addHelper(node.getLeft(), node, newNode);
                } else {
                    return addHelper(node.getRight(), node, newNode);
                }
            }
        }
    }
}
