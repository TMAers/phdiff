package io.nthienan.phdiff.report

import org.sonar.api.batch.BatchSide
import org.sonar.api.batch.InstantiationStrategy
import org.sonar.api.batch.postjob.issue.PostJobIssue

/**
 * Created on 20-Jul-17.
 * @author nthienan
 */
@BatchSide
@InstantiationStrategy(InstantiationStrategy.PER_BATCH)
class RemarkupInlineReportBuilder(val remarkupUtils: RemarkupUtils) : InlineReportBuilder {

    private var issue: PostJobIssue? = null

    override fun issue(issue: PostJobIssue?): InlineReportBuilder {
        this.issue = issue
        return this
    }

    override fun build(): String {
        val issue = this.issue
        return if (issue!=null) {
            StringBuilder().append(remarkupUtils.icon(issue.severity()))
                .append(" ").append(remarkupUtils.message(issue.message()))
                .append(" ").append(remarkupUtils.rule(issue.ruleKey().toString()))
                .toString()
        } else {
            ""
        }
    }
}
