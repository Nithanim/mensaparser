package jkumensa.parser.jku;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;

public class Cleaner {
    private static final char HTML_NBSP = '\u00a0';

    public static void replaceNbsp(List<? extends Node> nodes) {
        for (Node n : nodes) {
            if (n instanceof TextNode) {
                TextNode tn = (TextNode) n;
                tn.text(tn.text().replaceAll(HTML_NBSP + "", " "));
            } else if(n instanceof Element) {
                replaceNbsp(((Element)n).childNodes());
            } else {
                System.out.println("?");
            }
        }
    }
    
    public static void removeEmptyAndTrimText(List<? extends Node> nodes) {
        for (Node n : nodes) {
            if (n instanceof TextNode) {
                TextNode tn = (TextNode) n;
                tn.text(tn.text().replaceAll(HTML_NBSP + "", " "));
            } else if(n instanceof Element) {
                replaceNbsp(((Element)n).childNodes());
            } else {
                System.out.println("?");
            }
        }
    }

    public static List<List<Node>> splitFuckedUpFreeformUserInput(List<Element> nodes) {
        return groupByDiscardingTag("br", flattenPTags(nodes));
    }

    public static List<Node> flattenPTags(List<? extends Node> nodes) {
        ArrayList<Node> out = new ArrayList<>();

        ListIterator<? extends Node> it = nodes.listIterator();
        while (it.hasNext()) {
            Node n = it.next();
            if (equals(n, "p")) {
                out.add(new Element("br"));
                out.addAll(flattenPTags(n.childNodes()));
                out.add(new Element("br"));
            } else {
                out.add(n);
            }

        }
        filterMultiBrAndTrim(out);
        return out;
    }

    public static void filterMultiBrAndTrim(List<? extends Node> nodes) {
        ListIterator<? extends Node> it = nodes.listIterator();

        Node prev = null;
        while (it.hasNext()) {
            Node n = it.next();
            if (isBr(n) && (prev == null || isBr(prev))) {
                it.remove();
            }
            prev = n;
        }

        //remove trailing br
        if (!nodes.isEmpty()) {
            Node l = nodes.get(nodes.size() - 1);
            if (isBr(l)) {
                nodes.remove(nodes.size() - 1);
            }
        }
    }
    
      public static void removeLeadingAndTrailingBrs(List<Node> nodes) {
        ListIterator<Node> it = nodes.listIterator();
        while(it.hasNext()) {
            if(isBr(it.next())) {
                it.remove();
            } else {
                break;
            }
        }
        
        it = nodes.listIterator(nodes.size()-1);
        while(it.hasPrevious()) {
            if(isBr(it.previous())) {
                it.remove();
            } else {
                break;
            }
        }
    }

    public static List<List<Node>> groupByDiscardingTag(String tag, List<? extends Node> nodes) {
        ArrayList<List<Node>> out = new ArrayList<>();

        ArrayList<Node> curr = new ArrayList<>();
        for (Node n : nodes) {
            if (equals(n, "br")) {
                out.add(curr);
                curr = new ArrayList<>();
                continue;
            }
            curr.add(n);
        }

        if (!curr.isEmpty()) {
            out.add(curr);
        }
        return out;
    }

    public static List<List<Node>> groupByKeepingTag(String tag, List<? extends Node> nodes) {
        ArrayList<List<Node>> out = new ArrayList<>();

        ArrayList<Node> curr = new ArrayList<>();
        for (Node n : nodes) {
            if (equals(n, tag)) {
                if(!curr.isEmpty()) {
                    out.add(curr);
                }
                curr = new ArrayList<>();
            }
            curr.add(n);
        }

        if (!curr.isEmpty()) {
            out.add(curr);
        }
        return out;
    }

    private static boolean isBr(Node n) {
        return n instanceof Element && equals((Element) n, "br");
    }

    private static boolean equals(Node n, String tag) {
        return n instanceof Element && equals((Element) n, tag);
    }

    private static boolean equals(Element e, String tag) {
        return Tag.valueOf(tag).equals(e.tag());
    }
}
