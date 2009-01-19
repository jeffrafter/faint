package de.offis.faint.detection.plugins.haar;

import java.util.ArrayList;
import java.util.List;

/**
 * Group a number of rectangles into fewer instances that share mostly the same areas.
 *
 * @author <a href="mailto:matt.nathan@paphotos.com">Matt Nathan</a>
 */
public class Groups {


    private static boolean isNeighbour(CvRect r1, CvRect r2) {
        // based on the 20% factor in opencv
        int distance = Math.round(r1.width * 0.2f);
        return r2.x <= r1.x + distance &&
               r2.x >= r1.x - distance &&
               r2.y <= r1.y + distance &&
               r2.y >= r1.y - distance &&
               r2.x + r2.width <= r1.x + r1.width + distance &&
               r2.x + r2.width >= r1.x + r1.width - distance &&
               r2.y + r2.height <= r1.y + r1.height + distance &&
               r2.y + r2.height >= r1.y + r1.height - distance;
    }





    /**
     * Reduce the number of rectangles in the given areas into fewer similar groups of rectangles.
     *
     * @param areas The areas to combine
     * @return the grouped areas.
     */
    public static ArrayList<CvRect> reduceAreas(List<CvRect> areas) {
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
            CvRect a = areas.get(i);
            Group group = groups.get(groupId);

            group.neighbours++;
            group.area.x += a.x;
            group.area.y += a.y;
            group.area.width += a.width;
            group.area.height += a.height;
        }

        ArrayList<CvRect> results = new ArrayList<CvRect>(groupCount);

        for (Group group : groups) {
            if (group.neighbours > 3) { // the min number of rectangles to form a group
                CvRect r = group.area;
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
    public static int merge(List<CvRect> areas, List<Integer> result) {
        if (result == null) {
            throw new IllegalArgumentException("result cannot be null");
        }

        List<Node<CvRect>> trees = new ArrayList<Node<CvRect>>(areas.size());
        for (CvRect area : areas) {
            Node<CvRect> node = new Node<CvRect>();
            node.userData = area;
            trees.add(node);
        }

        for (Node<CvRect> tree : trees) {
            Node<CvRect> root = tree.getRoot();

            for (Node<CvRect> tree2 : trees) {
                if (isNeighbour(tree.userData, tree2.userData)) {
                    Node<CvRect> root2 = tree2.getRoot();
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
                            Node<CvRect> temp = tree2;
                            tree2 = tree2.parent;
                            temp.parent = root;
                        }

                        // Compress path from node to the root:
                        tree2 = tree;
                        while (tree2.parent != null) {
                            Node<CvRect> temp = tree2;
                            tree2 = tree2.parent;
                            temp.parent = root;
                        }
                    }
                }
            }
        }

        int classIndex = 0;
        for (Node<CvRect> tree : trees) {
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
        private CvRect area = new CvRect();
    }
}


