package com.trials.userpreference.demo.nosql;

import android.content.Context;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBSaveExpression;
import com.trials.amazonaws.mobile.AWSMobileClient;
import com.trials.amazonaws.mobile.util.ThreadUtils;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.trials.supertriathlon.Constants;
import com.trials.supertriathlon.R;
import com.trials.amazonaws.models.nosql.SeaDO;
import com.trials.supertriathlon.Sort;
import com.trials.supertriathlon.SystemManager;
import com.trials.userpreference.demo.DemoInstructionFragment;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DemoNoSQLTableSea extends DemoNoSQLTableBase {
    private static final String LOG_TAG = DemoNoSQLTableSea.class.getSimpleName();

    /** Inner classes use this value to determine how many results to retrieve per service call. */
    private static final int RESULTS_PER_RESULT_GROUP = 40;

    /********* Primary Get Query Inner Classes *********/

    public class DemoGetWithPartitionKey extends DemoNoSQLOperationBase {
        private SeaDO result;
        private boolean resultRetrieved = true;

        private DemoGetWithPartitionKey() {
            super("Your Id:", String.valueOf(SystemManager.getUserId()));
        }

        /* Blocks until result is retrieved, should be called in the background. */
        @Override
        public boolean loadItems() throws AmazonClientException {
            // Retrieve an item by passing the partition key using the object mapper.
            result = mapper.load(SeaDO.class, SystemManager.getUserId());
            if (result != null) {
                resultRetrieved = false;
                return true;
            }
            return false;
        }
        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            if (resultRetrieved) {
                return null;
            }
            final List<DemoNoSQLResult> results = new ArrayList<>();
            results.add(new DemoNoSQLSeaResult(result));
            resultRetrieved = true;
            return results;
        }

        @Override
        public void resetResults() {
            resultRetrieved = false;
        }

    }

    /* ******** Secondary Named Index Query Inner Classes ******** */

    /********* Scan Inner Classes *********/

    public class DemoScanWithFilter extends DemoNoSQLOperationBase {

        private PaginatedScanList<SeaDO> results;
        private Iterator<SeaDO> resultsIterator;

        DemoScanWithFilter(final Context context,String attributeName) {
            super(context.getString(R.string.nosql_operation_title_scan_with_filter),attributeName);
            mAttributeName = attributeName;
        }


        @Override
        public boolean loadItems() {
            // Use an expression names Map to avoid the potential for attribute names
            // colliding with DynamoDB reserved words.
            results = mapper.scan(SeaDO.class, DemoNoSQLTableBase.getDynamoDBScanExpression(this.mAttributeName));
            if (results != null) {
                // sort process
                int count = 0;
                int scoreList[] = new int[results.size()];
                int idList[] = new int[results.size()];
                String nameList[] = new String[results.size()];
                int previewRankList[] = new int[results.size()];
                int latestRankList[] = new int[results.size()];
                for (SeaDO up:results) {
                    idList[count] = up.getUserId();
                    nameList[count] = up.getUserName();
                    if (mAttributeName.equals(TABLE_ATTRIBUTE_EASY)) {
                        scoreList[count] = up.getTotalScoreEasy();
                        previewRankList[count] = up.getPreviewRankEasy();
                        latestRankList[count] = up.getLatestRankEasy();
                    } else if (mAttributeName.equals(TABLE_ATTRIBUTE_NORMAL)) {
                        scoreList[count] = up.getTotalScoreNormal();
                        previewRankList[count] = up.getPreviewRankNormal();
                        latestRankList[count] = up.getLatestRankNormal();
                    } else if (mAttributeName.equals(TABLE_ATTRIBUTE_HARD)) {
                        scoreList[count] = up.getTotalScoreHard();
                        previewRankList[count] = up.getPreviewRankHard();
                        latestRankList[count] = up.getLatestRankHard();
                    }
                    count++;
                }
                Sort.insertionSortWithAttribute(
                        scoreList,idList,nameList,
                        previewRankList,latestRankList);
                count = results.size()-1;
                int rank = 1;
                for (SeaDO up:results) {
                    up.setUserId(idList[count]);
                    up.setUserName(nameList[count]);
                    if (mAttributeName.equals(TABLE_ATTRIBUTE_EASY)) {
                        up.setTotalScoreEasy(scoreList[count]);
                        up.setPreviewRankEasy(previewRankList[count]);
                        up.setLatestRankEasy(latestRankList[count]);
                    } else if (mAttributeName.equals(TABLE_ATTRIBUTE_NORMAL)) {
                        up.setTotalScoreNormal(scoreList[count]);
                        up.setPreviewRankNormal(previewRankList[count]);
                        up.setLatestRankNormal(latestRankList[count]);
                    } else if (mAttributeName.equals(TABLE_ATTRIBUTE_HARD)) {
                        up.setTotalScoreHard(scoreList[count]);
                        up.setPreviewRankHard(previewRankList[count]);
                        up.setLatestRankHard(latestRankList[count]);
                    }
                    // when detecting the user's id, to set the new rank for getter
                    if (idList[count]==SystemManager.getUserId()) {
                        previewRank = previewRankList[count];
                        newRank = rank;
                    }
                    rank++;
                    count--;
                }
                resultsIterator = results.iterator();
                if (resultsIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }


        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            return getNextResultsGroupFromIterator(resultsIterator);
        }

        @Override
        public int getNewRank() { return newRank; }

        @Override
        public boolean isScan() {
            return true;
        }

        @Override
        public void resetResults() {
            resultsIterator = results.iterator();
        }
    }

    /**
     * Helper Method to handle retrieving the next group of query results.
     * @param resultsIterator the iterator for all the results (makes a new service call for each result group).
     * @return the next list of results.
     */
    private static List<DemoNoSQLResult> getNextResultsGroupFromIterator(final Iterator<SeaDO> resultsIterator) {
        if (!resultsIterator.hasNext()) {
            return null;
        }
        List<DemoNoSQLResult> resultGroup = new LinkedList<>();
        int itemsRetrieved = 0;
        do {
            // Retrieve the item from the paginated results.
            final SeaDO item = resultsIterator.next();
            // Add the item to a group of results that will be displayed later.
            resultGroup.add(new DemoNoSQLSeaResult(item));
            itemsRetrieved++;
        } while ((itemsRetrieved < RESULTS_PER_RESULT_GROUP) && resultsIterator.hasNext());
        return resultGroup;
    }

    /** The DynamoDB object mapper for accessing DynamoDB. */
    private final DynamoDBMapper mapper;

    public DemoNoSQLTableSea() {
        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
    }

    @Override
    public String getTableName() { return TABLE_SIMPLE_NAME_SEA; }

    @Override
    public void insertData(int userId,String userName,int[] records) throws AmazonClientException {
        Log.d(LOG_TAG, "Inserting Sample data.");
        final SeaDO item = new SeaDO();
        item.setUserId(userId);
        item.setTotalScoreEasy(records[0]);
        item.setTotalScoreHard(records[1]);
        item.setTotalScoreNormal(records[2]);
        item.setPreviewRankEasy(0);
        item.setPreviewRankNormal(0);
        item.setPreviewRankHard(0);
        item.setLatestRankEasy(0);
        item.setLatestRankNormal(0);
        item.setLatestRankHard(0);
        item.setUserName(userName);
        AmazonClientException lastException = null;
        try {
            mapper.save(item);
        } catch (final AmazonClientException ex) {
            Log.e(LOG_TAG, "Failed saving item : " + ex.getMessage(), ex);
            lastException = ex;
        }
        if (lastException != null) {
            // Re-throw the last exception encountered to alert the user.
            throw lastException;
        }
    }


    /* update each item */
    @Override
    public boolean updateItems(Context context) throws AmazonClientException {
        // Use an expression names Map to avoid the potential for attribute names
        // colliding with DynamoDB reserved words.
        PaginatedScanList<SeaDO> results;
        int latestRank[] = new int[TABLE_ATTRIBUTE_NAME_LEVEL_LIST.length];
        int previewRank[] = new int[TABLE_ATTRIBUTE_NAME_LEVEL_LIST.length];
        for (int i = 0; i < TABLE_ATTRIBUTE_NAME_LEVEL_LIST.length; i++) {
            results = mapper.scan(SeaDO.class, DemoNoSQLTableBase.getDynamoDBScanExpression(TABLE_ATTRIBUTE_NAME_LEVEL_LIST[i]));
            // sort process
            int count = 0;
            int scoreList[] = new int[results.size()];
            int idList[] = new int[results.size()];
            String nameList[] = new String[results.size()];
            int priorRank[] = new int[results.size()];  // prior rank means the records that was updated lately.
            for (SeaDO up:results) {
                idList[count] = up.getUserId();
                nameList[count] = up.getUserName();
                if (TABLE_ATTRIBUTE_NAME_LEVEL_LIST[i].equals(TABLE_ATTRIBUTE_EASY)) {
                    scoreList[count] = up.getTotalScoreEasy();
                    priorRank[count] = up.getLatestRankEasy();
                } else if (TABLE_ATTRIBUTE_NAME_LEVEL_LIST[i].equals(TABLE_ATTRIBUTE_NORMAL)) {
                    scoreList[count] = up.getTotalScoreNormal();
                    priorRank[count] = up.getLatestRankNormal();
                } else if (TABLE_ATTRIBUTE_NAME_LEVEL_LIST[i].equals(TABLE_ATTRIBUTE_HARD)) {
                    scoreList[count] = up.getTotalScoreHard();
                    priorRank[count] = up.getLatestRankHard();
                }
                count++;
            }
            Sort.insertionSortWithAttribute(scoreList,idList,nameList,priorRank,priorRank);
            int rank = 1;
            for (int j = idList.length-1; j >= 0; j--) {
                // to detect user's rank
                if (idList[j]==SystemManager.getUserId()) {
                    previewRank[i] = priorRank[j];
                    latestRank[i] = rank;
                    break;
                }
                rank++;
            }
        }
        // get each the best record from the local storage.
        SystemManager systemManager = new SystemManager(context);
        int records[] = new int[LEVEL_NUMBER_LIST.length];
        for (int i=0;i<records.length;i++) {
            records[i] = systemManager.getTheRecords(STAGE_SEA,LEVEL_NUMBER_LIST[i])[RECORD_TOTAL];
        }
        // get each the best record from the local storage.
        // to get aggregate score
        boolean updated = false;
        SeaDO update = new SeaDO();
        update.setUserId(SystemManager.getUserId());
        update.setUserName(SystemManager.getUserName());
        update.setTotalScoreEasy(records[0]);
        update.setTotalScoreNormal(records[1]);
        update.setTotalScoreHard(records[2]);
        update.setPreviewRankEasy(previewRank[0]);
        update.setPreviewRankNormal(previewRank[1]);
        update.setPreviewRankHard(previewRank[2]);
        update.setLatestRankEasy(latestRank[0]);
        update.setLatestRankNormal(latestRank[1]);
        update.setLatestRankHard(latestRank[2]);
        AmazonClientException lastException = null;
        DynamoDBSaveExpression saveExpression = DemoNoSQLTableBase.getDynamoDBSaveExpression(SystemManager.getUserId());
        try {
            mapper.save(update);
            updated = true;
        } catch (final AmazonClientException ex) {
            Log.e(LOG_TAG, "Failed updating item : " + ex.getMessage(), ex);
            lastException = ex;
        } finally {
            if (lastException != null) {
                updated = false;
                // Re-throw the last exception encountered to alert the user.
                throw lastException;
            }
        }
        return updated;
    }


    @Override
    public void removeData(int userId) throws AmazonClientException {
        // Scan for the sample data to remove it.
        PaginatedScanList<SeaDO> results = mapper.scan(SeaDO.class, DemoNoSQLTableBase.getDynamoDBScanExpression(userId));

        Iterator<SeaDO> resultsIterator = results.iterator();

        AmazonClientException lastException = null;

        if (resultsIterator.hasNext()) {
            final SeaDO item = resultsIterator.next();
            // Demonstrate deleting a single item.
            try {
                mapper.delete(item);
            } catch (final AmazonClientException ex) {
                Log.e(LOG_TAG, "Failed deleting item : " + ex.getMessage(), ex);
                lastException = ex;
            }
        }
        if (lastException != null) {
            // Re-throw the last exception encountered to alert the user.
            // The logs contain all the exceptions that occurred during attempted delete.
            throw lastException;
        }
    }

    private List<DemoNoSQLOperationListItem> getSupportedDemoOperations(final Context context) {
        List<DemoNoSQLOperationListItem> noSQLOperationsList = new ArrayList<DemoNoSQLOperationListItem>();
            noSQLOperationsList.add(new DemoGetWithPartitionKey());

        noSQLOperationsList.add(new DemoNoSQLOperationListHeader(context.getString(R.string.nosql_operation_header_scan)));
        noSQLOperationsList.add(new DemoScanWithFilter(context,TABLE_ATTRIBUTE_EASY));
        noSQLOperationsList.add(new DemoScanWithFilter(context,TABLE_ATTRIBUTE_NORMAL));
        noSQLOperationsList.add(new DemoScanWithFilter(context,TABLE_ATTRIBUTE_HARD));
        return noSQLOperationsList;
    }

    @Override
    public void getSupportedDemoOperations(final Context context,
                                           final SupportedDemoOperationsHandler opsHandler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<DemoNoSQLOperationListItem> supportedOperations = getSupportedDemoOperations(context);
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        opsHandler.onSupportedOperationsReceived(supportedOperations);
                    }
                });
            }
        }).start();
    }
}
