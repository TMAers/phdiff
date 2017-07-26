package io.nthienan.phdiff.issue

import org.sonar.api.batch.postjob.issue.PostJobIssue
import org.sonar.api.batch.rule.Severity
import java.util.Comparator

class IssueComparator : Comparator<PostJobIssue> {
    private fun compareComponentKeyAndLine(left: PostJobIssue, right: PostJobIssue): Int {
        return if (left.componentKey() != right.componentKey()) {
            left.componentKey().compareTo(right.componentKey())
        } else {
            compareInt(left.line(), right.line())
        }
    }

    private fun compareSeverity(leftSeverity: Severity, rightSeverity: Severity): Int {
        return if (leftSeverity.ordinal > rightSeverity.ordinal) -1 else 1
    }

    private fun compareInt(leftLine: Int?, rightLine: Int?): Int {
        return when {
            (leftLine == rightLine) -> 0
            (leftLine == null) -> -1
            (rightLine == null) -> 1
            else -> leftLine.compareTo(rightLine)
        }
    }

    override fun compare(left: PostJobIssue?, right: PostJobIssue?): Int {
        // Most severe issues should be displayed first.
        if (left === right) {
            return 0
        }
        if (left == null) {
            return 1
        }
        if (right == null) {
            return -1
        }
        if (left.severity() == right.severity()) {
            // When severity is the same, sort by component key to at least group issues from
            // the same file together.
            return compareComponentKeyAndLine(left, right)
        }
        return compareSeverity(left.severity(), right.severity())
    }
}
