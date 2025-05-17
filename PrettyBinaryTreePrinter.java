import java.util.*;

public class PrettyBinaryTreePrinter {

    public static class TreeNode {
        public String data;
        public TreeNode left, right;

        public TreeNode(String data) {
            this.data = data;
        }
    }

    // 외부에서 중첩 리스트로 트리 입력받기
    public static TreeNode fromNestedList(Object obj) {
        if (obj instanceof String s) {
            return new TreeNode(s);
        } else if (obj instanceof List<?> list && !list.isEmpty()) {
            String rootVal = list.get(0).toString();
            TreeNode root = new TreeNode(rootVal);

            // 왼쪽 자식
            if (list.size() > 1) {
                root.left = fromNestedList(list.get(1));
            }

            // 오른쪽 자식
            if (list.size() > 2) {
                root.right = fromNestedList(list.get(2));
            }

            return root;
        }
        return null;
    }

    public static void print(TreeNode root) {
        int maxDepth = getMaxDepth(root);
        printInternal(Collections.singletonList(root), 1, maxDepth);
    }

    private static void printInternal(List<TreeNode> nodes, int level, int maxDepth) {
        if (nodes.isEmpty() || allNull(nodes)) return;

        int floor = maxDepth - level;
        int edgeLines = (int) Math.pow(2, Math.max(floor - 1, 0));
        int firstSpaces = (int) Math.pow(2, floor) - 1;
        int betweenSpaces = (int) Math.pow(2, floor + 1) - 1;

        printSpaces(firstSpaces);

        List<TreeNode> nextLevel = new ArrayList<>();
        for (TreeNode node : nodes) {
            if (node != null) {
                System.out.print(node.data);
                nextLevel.add(node.left);
                nextLevel.add(node.right);
            } else {
                System.out.print(" ");
                nextLevel.add(null);
                nextLevel.add(null);
            }
            printSpaces(betweenSpaces);
        }
        System.out.println();

        for (int i = 1; i <= edgeLines; i++) {
            for (int j = 0; j < nodes.size(); j++) {
                printSpaces(firstSpaces - i);
                TreeNode node = nodes.get(j);

                if (node == null) {
                    printSpaces(edgeLines * 2 + i + 1);
                    continue;
                }

                System.out.print(node.left != null ? "/" : " ");
                printSpaces(i * 2 - 1);
                System.out.print(node.right != null ? "\\" : " ");
                printSpaces(edgeLines * 2 - i);
            }
            System.out.println();
        }

        printInternal(nextLevel, level + 1, maxDepth);
    }

    private static void printSpaces(int count) {
        System.out.print(" ".repeat(Math.max(0, count)));
    }

    private static int getMaxDepth(TreeNode node) {
        if (node == null) return 0;
        return 1 + Math.max(getMaxDepth(node.left), getMaxDepth(node.right));
    }

    private static boolean allNull(List<TreeNode> list) {
        for (TreeNode node : list) {
            if (node != null) return false;
        }
        return true;
    }
}
