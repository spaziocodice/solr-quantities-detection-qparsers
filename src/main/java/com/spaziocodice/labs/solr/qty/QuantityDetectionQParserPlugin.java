package com.spaziocodice.labs.solr.qty;

import com.spaziocodice.labs.solr.qty.cfg.Unit;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.search.ExtendedDismaxQParserPlugin;
import org.apache.solr.search.QParserPlugin;

import java.util.Set;
import java.util.TreeSet;

/**
 * A {@link QParserPlugin} which detects and removes all quantities from the input query string.
 *
 * @author agazzarini
 * @since 1.0
 */
public class QuantityDetectionQParserPlugin extends QuantityDetector {
    private ExtendedDismaxQParserPlugin qParser;

    @Override
    public void init(final NamedList args) {
        super.init(args);
        this.qParser = new ExtendedDismaxQParserPlugin();
    }

    @Override
    QueryBuilder queryBuilder(final StringBuilder query) {
        return new QueryBuilder() {
            final Set<QuantityOccurrence> occurrences = new TreeSet<>();

            @Override
            public void newQuantityDetected(final Unit unit, final QuantityOccurrence occurrence) {
                occurrences.add(occurrence);
            }

            @Override
            public String product() {
                occurrences.forEach(occurrence ->
                        query.delete(occurrence.indexOfAmount, occurrence.indexOfUnit + occurrence.unit.length()));
                final String result = query.toString().trim();
                return result.isEmpty() ? "*:*" : result;
            }

            @Override
            public QParserPlugin qparserPlugin() {
                return qParser;
            }
        };
    }
}