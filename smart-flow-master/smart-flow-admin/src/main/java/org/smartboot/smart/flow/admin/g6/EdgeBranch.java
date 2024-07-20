package org.smartboot.smart.flow.admin.g6;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qinluo
 * @date 2023/2/10 22:30
 * @since 1.0.0
 */
public class EdgeBranch {
    private String label;
    private List<String> branches = new ArrayList<>();

    public EdgeBranch(String single) {
        this(single, null);
    }

    public EdgeBranch(String single, String right) {
        this.branches.add(single);
        this.label = right;
    }

    public EdgeBranch(List<String> branches) {
        this.branches.addAll(branches);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getBranches() {
        return branches;
    }

    public void setBranches(List<String> branches) {
        this.branches = branches;
    }
}
