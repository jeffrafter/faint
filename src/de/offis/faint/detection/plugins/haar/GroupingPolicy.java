package de.offis.faint.detection.plugins.haar;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Group a number of rectangles into fewer instances that share mostly the same areas.
 *
 * @author <a href="mailto:matt.nathan@paphotos.com">Matt Nathan</a>
 */
public class GroupingPolicy implements Serializable {

    public static final int ALL = 0;

    private int minimumGroupSize;





    public GroupingPolicy() {
        this(3);
    }





    public GroupingPolicy(int minimumGroupSize) {
        this.minimumGroupSize = minimumGroupSize;
    }





    public int getMinimumGroupSize() {
        return minimumGroupSize;
    }





    public void setMinimumGroupSize(int minimumGroupSize) {
        this.minimumGroupSize = minimumGroupSize;
    }





    private boolean isNeighbour(Rectangle r1, Rectangle r2) {
        // based on the 20% factor in opencv
        int distanceh = Math.round(r1.width * 0.2f);
        int distancev = Math.round(r1.height * 0.2f);
        return r2.x <= r1.x + distanceh &&
               r2.x >= r1.x - distanceh &&
               r2.y <= r1.y + distancev &&
               r2.y >= r1.y - distancev &&
               r2.x + r2.width <= r1.x + r1.width + distanceh &&
               r2.x + r2.width >= r1.x + r1.width - distanceh &&
               r2.y + r2.height <= r1.y + r1.height + distancev &&
               r2.y + r2.height >= r1.y + r1.height - distancev;
    }





    /**
     * Reduce the number of rectangles in the given areas into fewer similar groups of rectangles.
     *
     * @param areas The areas to combine
     * @return the grouped areas.
     */
    public ArrayList<Rectangle> reduceAreas(List<Rectangle> areas) {
        if (minimumGroupSize == ALL) {
            return new ArrayList<Rectangle>(areas);
        }

        List<Integer> groupIds = new ArrayList<Integer>(areas.size());

        // merge the rectangles into classification groups
        int groupCount = merge(areas, groupIds);

        // create the average rectangle for the groups based on a minimum number of neighbours threshold.
        List<Group> groups = new ArrayList<Group>(groupCount);
        for (int i = 0; i < groupCount; i++) {
            groups.add(new Group());
        }

        for (int i = 0; i < groupIds.size(); i++) {
            int groupId = groupIds.get(i);
            Rectangle a = areas.get(i);
            Group group = groups.get(groupId);

            group.neighbours++;
            group.area.x += a.x;
            group.area.y += a.y;
            group.area.width += a.width;
            group.area.height += a.height;
        }

        ArrayList<Rectangle> results = new ArrayList<Rectangle>(groupCount);

        for (Group group : groups) {
            if (group.neighbours >= minimumGroupSize) { // the min number of rectangles to form a group
                Rectangle r = group.area;
                r.x /= group.neighbours;
                r.y /= group.neighbours;
                r.width /= group.neighbours;
                r.height /= group.neighbours;
                results.add(r);
            }
        }

        return results;
    }





    /**
     * Combines rectangles that are similar into the same 'classification group'.
     *
     * @param areas  The areas to combine
     * @param result The resultant classification ids
     * @return The number of classes
     */
    protected int merge(List<Rectangle> areas, List<Integer> result) {
        if (result == null) {
            throw new IllegalArgumentException("result cannot be null");
        }

        List<Node<Rectangle>> trees = new ArrayList<Node<Rectangle>>(areas.size());
        for (Rectangle area : areas) {
            Node<Rectangle> node = new Node<Rectangle>();
            node.userData = area;
            trees.add(node);
        }

        for (Node<Rectangle> tree : trees) {
            Node<Rectangle> root = tree.getRoot();

            for (Node<Rectangle> tree2 : trees) {
                if (isNeighbour(tree.userData, tree2.userData)) {
                    Node<Rectangle> root2 = tree2.getRoot();
                    if (root2 != root) {
                        // assign the smaller tree to the root of the larger tree (based on rank)
                        if (root.rank > root2.rank) {
                            root2.parent = root;
                        } else {
                            root.parent = root2;
                            if (root.rank == root2.rank) {
                                root2.rank++;
                            }
                            root = root2;
                        }
                        assert root.parent == null;

                        // flatten the tree, this sets each tree to have a depth of 1
                        // Compress path from node2 to the root:
                        while (tree2.parent != null) {
                            Node<Rectangle> temp = tree2;
                            tree2 = tree2.parent;
                            temp.parent = root;
                        }

                        // Compress path from node to the root:
                        tree2 = tree;
                        while (tree2.parent != null) {
                            Node<Rectangle> temp = tree2;
                            tree2 = tree2.parent;
                            temp.parent = root;
                        }
                    }
                }
            }
        }

        int classIndex = 0;
        for (Node<Rectangle> tree : trees) {
            int index = -1;
            tree = tree.getRoot();
            if (tree.rank >= 0) {
                tree.rank = ~classIndex++;
            }
            index = ~tree.rank;
            result.add(index);
        }
        return classIndex;
    }





    /** Defines a node in a tree. The nodes represent classified objects that are similar to each other. */
    private static class Node<T> {

        private T userData;
        private Node<T> parent;
        private int rank = 0;





        public Node<T> getRoot() {
            Node<T> root = this;
            while (root.parent != null) {
                root = root.parent;
            }
            return root;
        }
    }

    /**
     * Represents a rectangle and thenumber of neighbours that it has. This is used for generating the average area of a
     * classification of rectangles.
     */
    private static class Group {

        private int neighbours = 0;
        private Rectangle area = new Rectangle();
    }
}


